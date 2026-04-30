package cn.edu.qcl.web;

import cn.edu.qcl.api.UserMapServiceI;
import cn.edu.qcl.dto.data.UserMapDTO;
import cn.edu.qcl.exception.Asserts;
import com.alibaba.cola.dto.MultiResponse;
import com.alibaba.cola.dto.Response;
import com.alibaba.cola.dto.SingleResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * UserMap Controller
 * 提供 ClickHouse user_map 表的查询接口
 */
@RestController
@RequestMapping("/api/usermap")
public class UserMapController {

    @Autowired
    private UserMapServiceI userMapService;

    /**
     * 查询所有用户映射
     */
    @GetMapping("/list")
    public MultiResponse<UserMapDTO> listAll() {
        List<UserMapDTO> list = userMapService.listAll();
        return MultiResponse.of(list);
    }

}