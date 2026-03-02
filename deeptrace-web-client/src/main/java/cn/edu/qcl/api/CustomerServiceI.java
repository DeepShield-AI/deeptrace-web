package cn.edu.qcl.api;

import com.alibaba.cola.dto.MultiResponse;
import com.alibaba.cola.dto.Response;
import cn.edu.qcl.dto.CustomerAddCmd;
import cn.edu.qcl.dto.CustomerListByNameQry;
import cn.edu.qcl.dto.data.CustomerDTO;

public interface CustomerServiceI {

    Response addCustomer(CustomerAddCmd customerAddCmd);

    MultiResponse<CustomerDTO> listByName(CustomerListByNameQry customerListByNameQry);
}
