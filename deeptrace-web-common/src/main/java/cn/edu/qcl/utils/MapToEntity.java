package cn.edu.qcl.utils;

import cn.edu.qcl.annotation.ColumnAlias;

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
                ColumnAlias col = field.getAnnotation(ColumnAlias.class);
                if (col != null) {
                    String column = col.value();
                    Object value = map.get(column);

                    if (value != null) {
                        field.setAccessible(true);
                        field.set(obj, value.toString()); // 这里简单写死为String，可加类型判断
                    }
                }
            }
            return obj;
        } catch (Exception e) {
            throw new RuntimeException("Map转Bean失败", e);
        }
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
