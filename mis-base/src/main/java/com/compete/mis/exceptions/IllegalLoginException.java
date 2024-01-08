package com.compete.mis.exceptions;

public final class IllegalLoginException extends MisException {
    /**
     * 构造函数。
     * @param message 异常消息。
     */
    public IllegalLoginException(String message){
        super(message);
    }
}
