package com.compete.mis.exceptions;

public class BusinessException  extends MisException {
    /**
     * 构造函数。
     * @param message 异常消息。
     */
    public BusinessException(String message){
        super(message);
    }
}
