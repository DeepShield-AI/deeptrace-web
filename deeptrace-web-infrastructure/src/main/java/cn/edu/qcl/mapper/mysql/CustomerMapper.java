package cn.edu.qcl.mapper.mysql;

import cn.edu.qcl.customer.CustomerDO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CustomerMapper{

  CustomerDO getById(String customerId);
}
