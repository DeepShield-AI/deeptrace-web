package cn.edu.qcl.customer;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CustomerMapper{

  CustomerDO getById(String customerId);
}
