package cn.edu.qcl.web;

import cn.edu.qcl.api.GrpcLatencyServiceI;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/greptimedb")
public class GrpcLatencyController {

    @Resource
    private GrpcLatencyServiceI grpcLatencyServiceI;

    @RequestMapping(value = "/getGrpcLatency", method = RequestMethod.GET)
    public Object getGrpcLatency(
            @RequestParam(value = "host", required = false) String host,
            @RequestParam(value = "method", required = false) String method,
            @RequestParam(value = "percentile", required = false) Double percentile,
            @RequestParam(value = "valueColumn", required = false) String valueColumn,
            @RequestParam(value = "groupByColumn", required = false) String groupByColumn,
            @RequestParam(value = "start", required = false) Long start,
            @RequestParam(value = "end", required = false) Long end
    ){
        return grpcLatencyServiceI.getGrpcLatency(host, method, percentile, valueColumn, groupByColumn, start, end);
    }

}
