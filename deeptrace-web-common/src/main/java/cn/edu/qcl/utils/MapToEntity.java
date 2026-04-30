package cn.edu.qcl.utils;

import cn.edu.qcl.annotation.MapToBeanProperty;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MapToEntity {
    /**
     * 将 Map<String, Object> 动态映射为实体类
     */
    public static <T> T mapToEntity(Map<String, Object> map, Class<T> clazz) {
        try {
            T obj = clazz.getDeclaredConstructor().newInstance();
            for (Field field : clazz.getDeclaredFields()) {
                MapToBeanProperty col = field.getAnnotation(MapToBeanProperty.class);
                if (col != null) {
                    String column = col.value();
                    Object value = map.get(column);

                    if (value != null) {
                        field.setAccessible(true);
                        // Convert value to the correct type
                        Object convertedValue = convertToTargetType(value, field.getType());
                        field.set(obj, convertedValue);
                    }
                }
            }
            return obj;
        } catch (Exception e) {
            throw new RuntimeException("Map转Bean失败", e);
        }
    }

    /**
     * Convert value to target type
     */
    private static Object convertToTargetType(Object value, Class<?> targetType) {
        if (value == null) {
            return null;
        }

        // If value is already the target type, return directly
        if (targetType.isInstance(value)) {
            return value;
        }

        String valueStr = value.toString();

        // Handle primitive types and their wrappers
        if (targetType == String.class) {
            return valueStr;
        } else if (targetType == Integer.class || targetType == int.class) {
            return Integer.valueOf(valueStr);
        } else if (targetType == Long.class || targetType == long.class) {
            return Long.valueOf(valueStr);
        } else if (targetType == Double.class || targetType == double.class) {
            return Double.valueOf(valueStr);
        } else if (targetType == Float.class || targetType == float.class) {
            return Float.valueOf(valueStr);
        } else if (targetType == Boolean.class || targetType == boolean.class) {
            return Boolean.valueOf(valueStr);
        } else if (targetType == Short.class || targetType == short.class) {
            return Short.valueOf(valueStr);
        } else if (targetType == Byte.class || targetType == byte.class) {
            return Byte.valueOf(valueStr);
        }

        // For other types, return as String
        return valueStr;
    }

    // 批量转换
    public static <T> List<T> mapListToEntityList(List<Map<String, Object>> maps, Class<T> clazz) {
        List<T> list = new ArrayList<>();
        for (Map<String, Object> map : maps) {
            list.add(mapToEntity(map, clazz));
        }
        return list;
    }
}
