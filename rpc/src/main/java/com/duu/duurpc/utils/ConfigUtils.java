package com.duu.duurpc.utils;

import cn.hutool.core.lang.Dict;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import cn.hutool.setting.dialect.Props;
import cn.hutool.setting.yaml.YamlUtil;

import java.util.ServiceLoader;

/**
 * @author : duu
 * @data : 2024/3/21
 * @from ：https://github.com/0oHo0
 **/
public class ConfigUtils {
    public static <T> T loadConfig(Class<T> tClass, String prefix){
        return loadConfig(tClass,prefix,"");
    }

    public static <T> T loadConfig(Class<T> tClass, String prefix, String environment) {
        T config = loadConfigFromYaml(tClass, prefix, environment);
        if (config != null) {
            return config;
        }
        config = loadConfigFromProperties(tClass, prefix, environment);
        return config;
    }

    public static <T> T loadConfigFromYaml(Class<T> tClass, String prefix, String environment){
        StringBuilder configFileBuilder = new StringBuilder("application");
        if (StrUtil.isNotBlank(environment)) {
            configFileBuilder.append("-").append(environment);
        }
        configFileBuilder.append(".yaml");
        Dict map = YamlUtil.loadByPath(configFileBuilder.toString());
        JSONObject jsonObject = JSONUtil.parseObj(map.getObj(prefix));
        return JSONUtil.toBean(jsonObject, tClass);
    }

    public static <T> T loadConfigFromProperties(Class<T> tClass, String prefix, String environment){
        StringBuilder configFileBuilder = new StringBuilder("application");
        if (StrUtil.isNotBlank(environment)) {
            configFileBuilder.append("-").append(environment);
        }
        configFileBuilder.append(".properties");
        Props props = new Props(configFileBuilder.toString());
        return props.toBean(tClass,prefix);
    }
}
