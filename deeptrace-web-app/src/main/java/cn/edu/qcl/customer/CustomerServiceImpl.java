package cn.edu.qcl.customer;

import com.alibaba.cola.dto.MultiResponse;
import com.alibaba.cola.dto.Response;
import com.alibaba.cola.catchlog.CatchAndLog;
import cn.edu.qcl.api.CustomerServiceI;
import cn.edu.qcl.dto.CustomerAddCmd;
import cn.edu.qcl.dto.CustomerListByNameQry;
import cn.edu.qcl.dto.data.CustomerDTO;
import org.springframework.stereotype.Service;

import cn.edu.qcl.customer.executor.CustomerAddCmdExe;
import cn.edu.qcl.customer.executor.query.CustomerListByNameQryExe;

import javax.annotation.Resource;


@Service
@CatchAndLog
public class CustomerServiceImpl implements CustomerServiceI {

    @Resource
    private CustomerAddCmdExe customerAddCmdExe;

    @Resource
    private CustomerListByNameQryExe customerListByNameQryExe;

    public Response addCustomer(CustomerAddCmd customerAddCmd) {
        return customerAddCmdExe.execute(customerAddCmd);
    }

    @Override
    public MultiResponse<CustomerDTO> listByName(CustomerListByNameQry customerListByNameQry) {
        return customerListByNameQryExe.execute(customerListByNameQry);
    }

}