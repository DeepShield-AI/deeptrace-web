package cn.edu.qcl.exception;


import cn.edu.qcl.dto.data.ErrorCode;
import com.alibaba.cola.exception.BizException;

/**
 * 断言处理类，用于抛出各种API异常
 * Created by macro on 2020/2/27.
 */
public class Asserts {
    public static void fail(String message) {
        throw new BizException(message);
    }

    public static void fail(ErrorCode errorCode) {
        throw new BizException(errorCode.getErrCode(),errorCode.getErrDesc());
    }
}
