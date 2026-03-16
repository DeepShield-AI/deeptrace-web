package cn.edu.qcl.dto.data;

import com.alibaba.cola.dto.DTO;
import com.alibaba.cola.dto.PageQuery;
import com.alibaba.cola.dto.PageResponse;
import lombok.Data;


@Data
public class CustomerDTO{
    private String customerId;
    private String memberId;
    private String customerName;
    private String customerType;
    private String companyName;
    private String source;
}
