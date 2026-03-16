package cn.edu.qcl.metric;

import cn.edu.qcl.api.GrpcLatencyServiceI;
import cn.edu.qcl.metric.gateway.GrpcLatencyGateWay;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class GrpcLatencyServiceImpl implements GrpcLatencyServiceI {

    @Resource
    private GrpcLatencyGateWay grpcLatencyGateWay;
    @Override
    public Object getGrpcLatency(String host, String method, Double percentile, String valueColumn, String groupByColumn, Long start, Long end) {
        return grpcLatencyGateWay.getGrpcLatency(host, method, percentile, valueColumn, groupByColumn, start, end);
    }
}
