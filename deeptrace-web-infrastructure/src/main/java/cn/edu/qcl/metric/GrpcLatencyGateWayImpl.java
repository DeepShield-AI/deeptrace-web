package cn.edu.qcl.metric;

import cn.edu.qcl.mapper.greptime.GrpcLatencyMapper;
import cn.edu.qcl.metric.gateway.GrpcLatencyGateWay;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Repository;

@Repository
public class GrpcLatencyGateWayImpl implements GrpcLatencyGateWay {
    @Resource
    private GrpcLatencyMapper grpcLatencyMapper;

    @Override
    public Object getGrpcLatency(String host, String method,
                                 Double percentile,
                                 String valueColumn,
                                 String groupByColumn,
                                 Long start,
                                 Long end) {
        Object res = null;
        switch ( method) {
            case "1":
                res =  grpcLatencyMapper.selectLatestFiveByHostAndMethod(host);
                break;
            case "2":
                res =  grpcLatencyMapper.selectPercentileLatencyByTimeRange(percentile, valueColumn, groupByColumn,start, end);
                break;
            case "3":
                res =  grpcLatencyMapper.queryTQL();
                break;
            default:
                res =  grpcLatencyMapper.selectLatestFiveByHostAndMethod(host);
                break;
        }
        return res;
    }
}
