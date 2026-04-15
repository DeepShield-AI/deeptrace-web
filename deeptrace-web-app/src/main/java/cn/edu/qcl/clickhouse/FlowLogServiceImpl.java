package cn.edu.qcl.clickhouse;

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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Flow Log 查询服务实现
 * 使用 ClickHouse Java Client 查询数据
 */
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
}