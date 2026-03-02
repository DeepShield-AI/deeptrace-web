package cn.edu.qcl.customer.gateway;

import cn.edu.qcl.customer.Credit;

//Assume that the credit info is in another distributed Service
public interface CreditGateway {
    Credit getCredit(String customerId);
}
