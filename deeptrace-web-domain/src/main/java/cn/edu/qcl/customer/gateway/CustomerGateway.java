package cn.edu.qcl.customer.gateway;

import cn.edu.qcl.customer.Customer;

public interface CustomerGateway {
    Customer getByById(String customerId);
}
