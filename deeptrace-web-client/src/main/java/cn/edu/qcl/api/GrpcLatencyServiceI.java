package cn.edu.qcl.api;

public interface GrpcLatencyServiceI {

    Object getGrpcLatency(String host, String method,
                          Double percentile,
                          String valueColumn,
                          String groupByColumn,
                          Long start,
                          Long end);
}
