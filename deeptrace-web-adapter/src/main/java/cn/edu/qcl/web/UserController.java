package cn.edu.qcl.web;

import cn.edu.qcl.api.UserServiceI;
import cn.edu.qcl.dto.data.UserDTO;
import cn.edu.qcl.dto.param.UserLoginParam;
import cn.edu.qcl.dto.param.UserParam;
import com.alibaba.cola.dto.Response;
import com.alibaba.cola.dto.SingleResponse;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/user")
public class UserController {
    @Value("${jwt.tokenHead}")
    private String tokenHead;

    /**
     * 服务对象
     */
    @Resource
    private UserServiceI userServiceI;


    /**
     * 用户注册
     *
     * @param userParam
     * @return
     */
    @RequestMapping(value = "/register", method = RequestMethod.POST)
    @ResponseBody
    public SingleResponse register(@Validated @RequestBody UserParam userParam) {
        UserDTO user;
        try {
            user = userServiceI.register(userParam);
            if (user == null) {
                return SingleResponse.buildFailure("","register fail");
            }
        } catch (Exception e) {
            return SingleResponse.buildFailure("",e.getMessage());
        }
        return SingleResponse.of(user);
    }

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    @ResponseBody
    public SingleResponse login(@Validated @RequestBody UserLoginParam param) {
        String token = userServiceI.login(param.getUsername(), param.getPassword());
        if (token == null) {
            return SingleResponse.buildFailure("","用户名或密码错误");
        }
        Map<String, String> tokenMap = new HashMap<>();
        tokenMap.put("token", token);
        tokenMap.put("tokenHead", tokenHead);
        return SingleResponse.of(tokenMap);
    }
    /**
     * 分页查询
     *
     * @param user 筛选条件
     * @param pageRequest      分页对象
     * @return 查询结果
     */
    @GetMapping
    public ResponseEntity<Page<UserDTO>> queryByPage(UserDTO user, PageRequest pageRequest) {
        return ResponseEntity.ok(this.userServiceI.queryByPage(user, pageRequest));
    }

    /**
     * 通过主键查询单条数据
     *
     * @param id 主键
     * @return 单条数据
     */
    @GetMapping("{id}")
    public ResponseEntity<UserDTO> queryById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(this.userServiceI.queryById(id));
    }

    /**
     * 新增数据
     *
     * @param user 实体
     * @return 新增结果
     */
    @PostMapping
    public ResponseEntity<UserDTO> add(UserDTO user) {
        return ResponseEntity.ok(this.userServiceI.insert(user));
    }

    /**
     * 编辑数据
     *
     * @param user 实体
     * @return 编辑结果
     */
    @PutMapping
    public ResponseEntity<UserDTO> edit(UserDTO user) {
        return ResponseEntity.ok(this.userServiceI.update(user));
    }

    /**
     * 删除数据
     *
     * @param id 主键
     * @return 删除是否成功
     */
    @DeleteMapping
    public ResponseEntity<Boolean> deleteById(Long id) {
        return ResponseEntity.ok(this.userServiceI.deleteById(id));
    }

}
