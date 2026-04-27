/*
package cn.edu.qcl.clickhouse;

import cn.edu.qcl.dto.data.SpanDTO;
import cn.edu.qcl.dto.param.SpanQueryParam;
import com.clickhouse.client.ClickHouseClient;
import com.clickhouse.client.ClickHouseNode;
import com.clickhouse.client.ClickHouseResponse;
import com.clickhouse.data.ClickHouseFormat;
import com.clickhouse.data.ClickHouseRecord;
import com.clickhouse.data.ClickHouseValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

*/
/**
 * Flow Log 查询服务实现
 * 使用 ClickHouse Java Client 查询数据
 *//*

@Service
public class FlowLogServiceImpl implements FlowLogService {

    private static final Logger log = LoggerFactory.getLogger(FlowLogServiceImpl.class);

    @Autowired
    private ClickHouseClient clickHouseClient;

    @Autowired
    private ClickHouseNode clickHouseNode;

    @Override
    public List<Map<String, Object>> queryL4FlowLog(int limit) {
        String sql = String.format(
            "SELECT `_id` AS id, eth_type AS name FROM flow_log.l4_flow_log LIMIT %d",
            limit
        );
        
        log.info("Executing ClickHouse query: {}", sql);
        
        List<Map<String, Object>> results = new ArrayList<>();
        
        try (ClickHouseResponse response = clickHouseClient.read(clickHouseNode)
                .query(sql)
                .format(ClickHouseFormat.RowBinaryWithNamesAndTypes)
                .executeAndWait()) {
            
            // 遍历结果集
            for (ClickHouseRecord record : response.records()) {
                Map<String, Object> row = new HashMap<>();
                // 获取第一列 (id)
                ClickHouseValue idValue = record.getValue(0);
                row.put("id", idValue.asString());
                // 获取第二列 (name)
                ClickHouseValue nameValue = record.getValue(1);
                row.put("name", nameValue.asString());
                results.add(row);
            }
            
            log.info("Query completed, returned {} records", results.size());
            
        } catch (Exception e) {
            log.error("Failed to execute ClickHouse query: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to query ClickHouse", e);
        }
        
        return results;
    }

    @Override
    public List<SpanDTO> querySpan(SpanQueryParam queryParam) {
        StringBuilder sqlBuilder = new StringBuilder();
        sqlBuilder.append("SELECT ");
        sqlBuilder.append("time, start_time, end_time, response_duration, ");
        sqlBuilder.append("app_service, app_instance, request_domain, request_resource, endpoint, ");
        sqlBuilder.append("response_status, response_code, response_exception, response_result, ");
        sqlBuilder.append("request_length, response_length, ");
        sqlBuilder.append("ip4_0, ip4_1, ip6_0, ip6_1, ");
        sqlBuilder.append("client_port, server_port, process_id_0, process_id_1, ");
        sqlBuilder.append("attribute_names, attribute_values ");
        sqlBuilder.append("FROM flow_log.l7_flow_log WHERE 1=1 ");

        List<Object> params = new ArrayList<>();

        // 必填条件：租户ID
        if (queryParam.getUserId() != null) {
            sqlBuilder.append("AND user_id = ? ");
            params.add(queryParam.getUserId());
        }

        //  必填条件：TraceID
        if (StringUtils.hasText(queryParam.getTraceId())) {
            sqlBuilder.append("AND trace_id = ? ");
            params.add(queryParam.getTraceId());
        }


        // 排序
        String orderBy = StringUtils.hasText(queryParam.getOrderBy()) ? queryParam.getOrderBy() : "time";
        String orderDirection = StringUtils.hasText(queryParam.getOrderDirection()) ? queryParam.getOrderDirection() : "DESC";
        sqlBuilder.append("ORDER BY ").append(orderBy).append(" ").append(orderDirection).append(" ");

        // 限制条数
        int limit = queryParam.getLimit() != null ? queryParam.getLimit() : 100;
        sqlBuilder.append("LIMIT ").append(limit);

        String sql = sqlBuilder.toString();
        log.info("Executing ClickHouse Span query: {}, params: {}", sql, params);

        List<SpanDTO> results = new ArrayList<>();

        try (ClickHouseResponse response = clickHouseClient.read(clickHouseNode)
                .query(sql)
                .params(params.toArray())
                .format(ClickHouseFormat.RowBinaryWithNamesAndTypes)
                .executeAndWait()) {

            for (ClickHouseRecord record : response.records()) {
                SpanDTO span = new SpanDTO();
                int idx = 0;

                span.setTime(parseDateTime(record.getValue(idx++)));
                span.setStartTime(parseDateTime(record.getValue(idx++)));
                span.setEndTime(parseDateTime(record.getValue(idx++)));
                span.setResponseDuration(parseLong(record.getValue(idx++)));
                span.setAppService(parseString(record.getValue(idx++)));
                span.setAppInstance(parseString(record.getValue(idx++)));
                span.setRequestDomain(parseString(record.getValue(idx++)));
                span.setRequestResource(parseString(record.getValue(idx++)));
                span.setEndpoint(parseString(record.getValue(idx++)));
                span.setResponseStatus(parseString(record.getValue(idx++)));
                span.setResponseCode(parseInteger(record.getValue(idx++)));
                span.setResponseException(parseString(record.getValue(idx++)));
                span.setResponseResult(parseString(record.getValue(idx++)));
                span.setRequestLength(parseLong(record.getValue(idx++)));
                span.setResponseLength(parseLong(record.getValue(idx++)));
                span.setIp4_0(parseLong(record.getValue(idx++)));
                span.setIp4_1(parseLong(record.getValue(idx++)));
                span.setIp6_0(parseString(record.getValue(idx++)));
                span.setIp6_1(parseString(record.getValue(idx++)));
                span.setClientPort(parseInteger(record.getValue(idx++)));
                span.setServerPort(parseInteger(record.getValue(idx++)));
                span.setProcessId_0(parseLong(record.getValue(idx++)));
                span.setProcessId_1(parseLong(record.getValue(idx++)));
                span.setAttributeNames(parseStringList(record.getValue(idx++)));
                span.setAttributeValues(parseStringList(record.getValue(idx++)));

                results.add(span);
            }

            log.info("Span query completed, returned {} records", results.size());

        } catch (Exception e) {
            log.error("Failed to execute ClickHouse Span query: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to query Span from ClickHouse", e);
        }

        return results;
    }

    private LocalDateTime parseDateTime(ClickHouseValue value) {
        if (value == null || value.isNullOrEmpty()) {
            return null;
        }
        try {
            String strValue = value.asString();
            if (strValue == null || strValue.isEmpty()) {
                return null;
            }
            return LocalDateTime.parse(strValue.replace(" ", "T"));
        } catch (Exception e) {
            log.warn("Failed to parse datetime: {}", value.asString());
            return null;
        }
    }

    private String parseString(ClickHouseValue value) {
        if (value == null || value.isNullOrEmpty()) {
            return null;
        }
        String str = value.asString();
        return str != null && !str.isEmpty() ? str : null;
    }

    private Long parseLong(ClickHouseValue value) {
        if (value == null || value.isNullOrEmpty()) {
            return null;
        }
        try {
            return value.asLong();
        } catch (Exception e) {
            return null;
        }
    }

    private Integer parseInteger(ClickHouseValue value) {
        if (value == null || value.isNullOrEmpty()) {
            return null;
        }
        try {
            return value.asInteger();
        } catch (Exception e) {
            return null;
        }
    }

    private List<String> parseStringList(ClickHouseValue value) {
        if (value == null || value.isNullOrEmpty()) {
            return null;
        }
        try {
            String str = value.asString();
            if (str == null || str.isEmpty()) {
                return null;
            }
            // ClickHouse Array format: ['a', 'b', 'c']
            if (str.startsWith("[") && str.endsWith("]")) {
                str = str.substring(1, str.length() - 1);
                if (str.isEmpty()) {
                    return new ArrayList<>();
                }
                return Arrays.stream(str.split(","))
                        .map(s -> s.trim().replace("'", ""))
                        .filter(s -> !s.isEmpty())
                        .collect(Collectors.toList());
            }
            return Arrays.asList(str);
        } catch (Exception e) {
            log.warn("Failed to parse string list: {}", value.asString());
            return null;
        }
    }
}*/
