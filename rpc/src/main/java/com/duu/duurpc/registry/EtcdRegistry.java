package com.duu.duurpc.registry;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.cron.CronUtil;
import cn.hutool.cron.task.Task;
import cn.hutool.json.JSONUtil;
import com.duu.duurpc.config.RegistryConfig;
import com.duu.duurpc.model.ServiceMetaInfo;
import io.etcd.jetcd.*;
import io.etcd.jetcd.kv.DeleteResponse;
import io.etcd.jetcd.kv.GetResponse;
import io.etcd.jetcd.options.GetOption;
import io.etcd.jetcd.options.PutOption;
import io.etcd.jetcd.watch.WatchEvent;
import io.vertx.core.impl.ConcurrentHashSet;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

/**
 * @author Duu
 */
public class EtcdRegistry implements Registry {

    private Client client;

    private KV kvClient;

    /**
     * 根节点
     */
    private static final String ETCD_ROOT_PATH = "/rpc/";
    /**
     * 本地注册的节点Key集合
     */
    private final Set<String> localRegisterNodeKeySet = new HashSet<>();

    private final RegistryServiceCache registryServiceCache = new RegistryServiceCache();

    private final Set<String> watchingKeySet = new ConcurrentHashSet<>();

    @Override
    public void init(RegistryConfig registryConfig) {
        String address = registryConfig.getAddress();
        Long timeout = registryConfig.getTimeout();
        client = Client.builder().endpoints(address).connectTimeout(Duration.ofMillis(timeout)).build();
        kvClient = client.getKVClient();
        heartBeat();
    }

    @Override
    public void registry(ServiceMetaInfo serviceMetaInfo) {
        Lease leaseClient = client.getLeaseClient();
        long leaseId = 0;
        try {
            leaseId = leaseClient.grant(300).get().getID();


            String registerKey = ETCD_ROOT_PATH + serviceMetaInfo.getServiceNodeKey();
            ByteSequence key = ByteSequence.from(registerKey, StandardCharsets.UTF_8);
            ByteSequence value = ByteSequence.from(JSONUtil.toJsonStr(serviceMetaInfo), StandardCharsets.UTF_8);

            PutOption putOption = PutOption.builder().withLeaseId(leaseId).build();
            kvClient.put(key, value, putOption).get();

            //添加到本地缓存
            localRegisterNodeKeySet.add(registerKey);
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void unRegistry(ServiceMetaInfo serviceMetaInfo) throws ExecutionException, InterruptedException {
        String registerKey = ETCD_ROOT_PATH + serviceMetaInfo.getServiceNodeKey();
        ByteSequence key = ByteSequence.from(registerKey, StandardCharsets.UTF_8);
        kvClient.delete(key).get();
        localRegisterNodeKeySet.remove(registerKey);
    }

    @Override
    public List<ServiceMetaInfo> serviceDiscovery(String serviceKey) throws ExecutionException, InterruptedException {
        List<ServiceMetaInfo> serviceMetaInfoList = registryServiceCache.readCache();
        if (serviceMetaInfoList != null) {
            return serviceMetaInfoList;
        }
        String searchPrefix = ETCD_ROOT_PATH + serviceKey + "/";
        ByteSequence searchPrefixUtf8 = ByteSequence.from(searchPrefix, StandardCharsets.UTF_8);
        GetOption getOption = GetOption.builder().isPrefix(true).build();
        List<KeyValue> kvs = kvClient.get(searchPrefixUtf8, getOption).get().getKvs();
        serviceMetaInfoList = kvs.stream().map(key -> {
            watch(key.getKey().toString());
            String value = key.getValue().toString();
            return JSONUtil.toBean(value, ServiceMetaInfo.class);
        }).collect(Collectors.toList());
        registryServiceCache.writeCache(serviceMetaInfoList);
        return serviceMetaInfoList;
    }

    @Override
    public void destroy() {
        for (String nodeKey : localRegisterNodeKeySet) {
            ByteSequence sequence = ByteSequence.from(nodeKey, StandardCharsets.UTF_8);
            try {
                kvClient.delete(sequence).get();
            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException(nodeKey + "节点下线失败", e);
            }
        }
        if (kvClient != null) {
            kvClient.close();
        }
        if (client != null) {
            client.close();
        }
    }

    @Override
    public void heartBeat() {
        CronUtil.schedule("*/10 * * * * *", (Task) () -> {
            for (String nodeKey : localRegisterNodeKeySet) {
                ByteSequence sequence = ByteSequence.from(nodeKey, StandardCharsets.UTF_8);
                List<KeyValue> keyValueList = null;
                try {
                    keyValueList = kvClient.get(sequence).get().getKvs();
                    if (keyValueList == null) {
                        continue;
                    }
                    KeyValue value = keyValueList.get(0);
                    String serviceJson = value.getValue().toString();
                    ServiceMetaInfo serviceMetaInfo = JSONUtil.toBean(serviceJson, ServiceMetaInfo.class);
                    registry(serviceMetaInfo);
                } catch (ExecutionException | InterruptedException e) {
                    throw new RuntimeException(nodeKey + "续签失败", e);
                }
            }
        });
        CronUtil.setMatchSecond(true);
        CronUtil.start();
    }

    @Override
    public void watch(String serviceNodeKey) {
        Watch watchClient = client.getWatchClient();
        boolean add = watchingKeySet.add(serviceNodeKey);
        if (add) {
            ByteSequence sequence = ByteSequence.from(serviceNodeKey, StandardCharsets.UTF_8);
            watchClient.watch(sequence, response -> {
                for (WatchEvent event : response.getEvents()) {
                    switch (event.getEventType()) {
                        case DELETE:
                            registryServiceCache.clearCache();
                            break;
                        case PUT:
                        case UNRECOGNIZED:
                        default:
                            break;
                    }
                }
            });
        }
    }
}