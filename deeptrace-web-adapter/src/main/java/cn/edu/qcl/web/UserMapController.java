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

    /**
     * 根据ID查询用户映射
     */
    @GetMapping("/{id}")
    public SingleResponse<UserMapDTO> getById(@PathVariable Long id) {
        UserMapDTO dto = userMapService.getById(id);
        if (dto == null) {
            Asserts.fail("用户不存在");
        }
        return SingleResponse.of(dto);
    }

    /**
     * 根据名称模糊查询
     */
    @GetMapping("/search")
    public MultiResponse<UserMapDTO> searchByName(@RequestParam String name) {
        List<UserMapDTO> list = userMapService.listByName(name);
        return MultiResponse.of(list);
    }

    /**
     * 分页查询
     */
    @GetMapping("/page")
    public MultiResponse<UserMapDTO> listByPage(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize) {
        List<UserMapDTO> list = userMapService.listByPage(page, pageSize);
        return MultiResponse.of(list);
    }

    /**
     * 统计总数
     */
    @GetMapping("/count")
    public SingleResponse<Long> count() {
        long count = userMapService.count();
        return SingleResponse.of(count);
    }
}