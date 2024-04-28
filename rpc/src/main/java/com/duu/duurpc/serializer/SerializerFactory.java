package com.duu.duurpc.serializer;

import com.duu.duurpc.spi.SpiLoader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author : duu
 * @data : 2024/3/22
 * @from ：https://github.com/0oHo0
 **/
public class SerializerFactory {

//    public static final Map<String, Serializer> KEY_SERIALIZER_MAP = new HashMap<String, Serializer>() {
//        {
//            put(SerializerKeys.JDK, new JdkSerializer());
//            put(SerializerKeys.JSON, new JsonSerializer());
//            put(SerializerKeys.HESSIAN, new HessianSerializer());
//            put(SerializerKeys.KRYO, new KryoSerializer());
//        }
//    };

    static {
        SpiLoader.load(Serializer.class);
    }

    public static final Serializer DEFAULT_SERIALIZER = new JdkSerializer();

    public static Serializer getInstance(String key) {
        return SpiLoader.getInstance(Serializer.class,key);
    }

}
