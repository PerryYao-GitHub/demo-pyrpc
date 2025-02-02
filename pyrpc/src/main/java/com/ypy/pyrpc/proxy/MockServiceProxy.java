package com.ypy.pyrpc.proxy;

import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;

@Slf4j
public class MockServiceProxy implements InvocationHandler {
    static Random rd = new Random();

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Class<?> methodReturnType = method.getReturnType();
        log.info("mock invoke {}", method.getName());
        return getDefaultObject(methodReturnType);
    }

    private Object getDefaultObject(Class<?> type) {
        // 8 primitive type
        if (type == boolean.class || type == Boolean.class) return rd.nextBoolean();
        if (type == byte.class || type == Byte.class) return (byte) rd.nextInt(256);
        if (type == short.class || type == Short.class) return (short) rd.nextInt(Short.MAX_VALUE + 1);
        if (type == char.class || type == Character.class) return (char) rd.nextInt(Character.MAX_VALUE + 1);
        if (type == int.class || type == Integer.class) return rd.nextInt(10000);
        if (type == long.class || type == Long.class) return rd.nextLong();
        if (type == float.class || type == Float.class) return rd.nextFloat();
        if (type == double.class || type == Double.class) return rd.nextDouble();

        // handle common type
        if (type == String.class) {
            int length = rd.nextInt(10) + 5; // 随机长度 5-14
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < length; i++) {
                sb.append((char) (rd.nextInt(26) + 'a')); // 随机小写字母
            }
            return sb.toString();
        }
        if (type == List.class) {
            List<Object> list = new ArrayList<>();
            list.add(getDefaultObject(Object.class)); // 添加一个随机对象
            return list;
        }
        if (type == Map.class) {
            Map<Object, Object> map = new HashMap<>();
            map.put(getDefaultObject(Object.class), getDefaultObject(Object.class)); // 添加一个随机键值对
            return map;
        }
        if (type == Set.class) {
            Set<Object> set = new HashSet<>();
            set.add(getDefaultObject(Object.class)); // 添加一个随机对象
            return set;
        }

        // handle self-defined class
        try {
            Object instance = type.getDeclaredConstructor().newInstance();
            Field[] fields = type.getDeclaredFields();
            for (Field field : fields) {
                // 跳过静态字段
                if (Modifier.isStatic(field.getModifiers())) continue;
                field.setAccessible(true);
                Class<?> fieldType = field.getType();
                field.set(instance, getDefaultObject(fieldType)); // 递归
            }
            return instance;
        } catch (Exception e) {
            log.warn("Failed to create mock object for type: {}", type.getName(), e);
            return null;
        }
    }
}
