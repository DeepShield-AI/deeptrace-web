package cn.edu.qcl.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.dataformat.yaml.YAMLGenerator;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.SafeConstructor;
import org.yaml.snakeyaml.representer.Representer;

import java.util.Map;

/**
 * JSON与YAML互相转换工具类
 * 提供JSON字符串与YAML字符串之间的相互转换功能
 */
public class JsonYamlConverter {
    
    private static final ObjectMapper JSON_MAPPER = new ObjectMapper();
    private static final ObjectMapper YAML_MAPPER = new ObjectMapper(new YAMLFactory());
    private static final Yaml YAML = new Yaml(new SafeConstructor(new LoaderOptions()), new Representer(new DumperOptions()));
    
    /**
     * 将JSON字符串转换为YAML字符串
     * 
     * @param jsonString JSON格式的字符串
     * @return YAML格式的字符串
     * @throws RuntimeException 转换过程中发生错误时抛出异常
     */
    public static String jsonToYaml(String jsonString) {
        if (jsonString == null || jsonString.trim().isEmpty()) {
            return "";
        }
        
        try {
            // 将JSON字符串解析为JsonNode
            JsonNode jsonNode = JSON_MAPPER.readTree(jsonString);
            
            // 将JsonNode转换为YAML字符串
            String yamlString = YAML_MAPPER.writeValueAsString(jsonNode);
            return yamlString;
            // 使用SnakeYAML进行格式化输出
//            Object obj = YAML.load(yamlString);
//            return YAML.dump(obj);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("JSON to YAML conversion failed", e);
        }
    }
    
    /**
     * 将YAML字符串转换为JSON字符串
     * 
     * @param yamlString YAML格式的字符串
     * @return JSON格式的字符串
     * @throws RuntimeException 转换过程中发生错误时抛出异常
     */
    public static String yamlToJson(String yamlString) {
        if (yamlString == null || yamlString.trim().isEmpty()) {
            return "{}";
        }
        
        try {
            // 将YAML字符串解析为Object
            Object yamlObj = YAML.load(yamlString);
            
            // 将Object转换为JSON字符串
            return JSON_MAPPER.writeValueAsString(yamlObj);
        } catch (Exception e) {
            throw new RuntimeException("YAML to JSON conversion failed", e);
        }
    }
    
    /**
     * 将JSON字符串转换为指定类型的对象
     * 
     * @param jsonString JSON格式的字符串
     * @param clazz 目标类型
     * @param <T> 泛型类型参数
     * @return 指定类型的对象
     * @throws RuntimeException 转换过程中发生错误时抛出异常
     */
    public static <T> T jsonToObject(String jsonString, Class<T> clazz) {
        if (jsonString == null || jsonString.trim().isEmpty()) {
            return null;
        }
        
        try {
            return JSON_MAPPER.readValue(jsonString, clazz);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("JSON to Object conversion failed", e);
        }
    }
    
    /**
     * 将YAML字符串转换为指定类型的对象
     * 
     * @param yamlString YAML格式的字符串
     * @param clazz 目标类型
     * @param <T> 泛型类型参数
     * @return 指定类型的对象
     * @throws RuntimeException 转换过程中发生错误时抛出异常
     */
    public static <T> T yamlToObject(String yamlString, Class<T> clazz) {
        if (yamlString == null || yamlString.trim().isEmpty()) {
            return null;
        }
        
        try {
            Object yamlObj = YAML.load(yamlString);
            return JSON_MAPPER.convertValue(yamlObj, clazz);
        } catch (Exception e) {
            throw new RuntimeException("YAML to Object conversion failed", e);
        }
    }
    
    /**
     * 将对象转换为JSON字符串
     * 
     * @param obj 待转换的对象
     * @return JSON格式的字符串
     * @throws RuntimeException 转换过程中发生错误时抛出异常
     */
    public static String objectToJson(Object obj) {
        if (obj == null) {
            return "{}";
        }
        
        try {
            return JSON_MAPPER.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Object to JSON conversion failed", e);
        }
    }
    
    /**
     * 将对象转换为YAML字符串
     * 
     * @param obj 待转换的对象
     * @return YAML格式的字符串
     * @throws RuntimeException 转换过程中发生错误时抛出异常
     */
    public static String objectToYaml(Object obj) {
        if (obj == null) {
            return "";
        }
        
        try {
            // 先将对象转换为JSON字符串，再通过YAML解析器转换
            String jsonString = JSON_MAPPER.writeValueAsString(obj);
            Object parsedObj = JSON_MAPPER.readValue(jsonString, Object.class);
            return YAML.dump(parsedObj);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Object to YAML conversion failed", e);
        }
    }
}