package cn.edu.qcl.metric.gateway;

public interface GrpcLatencyGateWay {
    Object getGrpcLatency(String host, String method,
                          Double percentile,
                          String valueColumn,
                          String groupByColumn,
                          Long start,
                          Long end);
}
