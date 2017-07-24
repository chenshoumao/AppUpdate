package com.solar.test;

import org.apache.log4j.Logger;

/**
 * 自定义异常类(继承运行时异常)
 * @author AlanLee
 * @version 2016/11/26
 */
public class MyException extends RuntimeException {

    private static final long serialVersionUID = 1L;
    
    private static Logger logger = Logger.getLogger(MyException.class);
    

    /**
     * 错误编码
     */
    private String errorCode;

    /**
     * 消息是否为属性文件中的Key
     */
    private boolean propertiesKey = true;

    /**
     * 构造一个基本异常.
     *
     * @param message
     *            信息描述
     */
    public MyException(String message)
    {
        super(message);
        logger.error("message is " + message );
    }

    /**
     * 构造一个基本异常.
     *
     * @param errorCode
     *            错误编码
     * @param message
     *            信息描述
     */
    public MyException(String errorCode, String message)
    {
        this(errorCode, message, true);
        logger.error(errorCode + " " + message);
    }

    /**
     * 构造一个基本异常.
     *
     * @param errorCode
     *            错误编码
     * @param message
     *            信息描述
     */
    public MyException(String errorCode, String message, Throwable cause)
    {
        this(errorCode, message, cause, true);
        logger.error("errorCode is " + errorCode );
        logger.error("message is " + message );
        logger.error("cause is " + cause );
    }

    /**
     * 构造一个基本异常.
     *
     * @param errorCode
     *            错误编码
     * @param message
     *            信息描述
     * @param propertiesKey
     *            消息是否为属性文件中的Key
     */
    public MyException(String errorCode, String message, boolean propertiesKey)
    {
        super(message);
        this.setErrorCode(errorCode);
        this.setPropertiesKey(propertiesKey);
        logger.error("errorCode is " + errorCode );
        logger.error("message is " + message );
        logger.error("propertiesKey is " + propertiesKey );
        
    }

    /**
     * 构造一个基本异常.
     *
     * @param errorCode
     *            错误编码
     * @param message
     *            信息描述
     */
    public MyException(String errorCode, String message, Throwable cause, boolean propertiesKey)
    {
        super(message, cause);
        this.setErrorCode(errorCode);
        this.setPropertiesKey(propertiesKey);
        logger.error("errorCode is " + errorCode );
        logger.error("message is " + propertiesKey );
        logger.error("cause is " + cause );
        logger.error("propertiesKey is " + propertiesKey );
    }

    /**
     * 构造一个基本异常.
     *
     * @param message
     *            信息描述
     * @param cause
     *            根异常类（可以存入任何异常）
     */
    public MyException(String message, Throwable cause)
    {
        super(message, cause);
    }
    
    public String getErrorCode()
    {
        return errorCode;
    }

    public void setErrorCode(String errorCode)
    {
        this.errorCode = errorCode;
    }

    public boolean isPropertiesKey()
    {
        return propertiesKey;
    }

    public void setPropertiesKey(boolean propertiesKey)
    {
        this.propertiesKey = propertiesKey;
    }
    
}