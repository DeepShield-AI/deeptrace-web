package cn.edu.qcl.dto;

import cn.edu.qcl.dto.data.CustomerDTO;
import com.alibaba.cola.dto.Command;
import lombok.Data;

@Data
public class CustomerAddCmd{

    private CustomerDTO customerDTO;

}
