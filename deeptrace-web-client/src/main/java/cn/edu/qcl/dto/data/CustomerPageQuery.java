package cn.edu.qcl.dto.data;

import com.alibaba.cola.dto.PageQuery;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class CustomerPageQuery extends PageQuery {
    private String name;
}
