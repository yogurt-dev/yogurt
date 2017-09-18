package com.github.jyoghurt.common.payment.common.module;

public class PaymentResult<R> {
    private String errorCode;
    private String errorMessage;
    private R result;

    public PaymentResult() {
    }

    public PaymentResult(R result) {
        this.result = result;
    }

    public PaymentResult(String errorCode, String errorMessage) {
        super();
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    /**
     * @return the errorCode
     */
    public String getErrorCode() {
        return errorCode;
    }

    /**
     * @param errorCode the errorCode to set
     */
    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    /**
     * @return the errorMessage
     */
    public String getErrorMessage() {
        return errorMessage;
    }

    /**
     * @param errorMessage the errorMessage to set
     */
    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public R getResult() {
        return result;
    }

    public PaymentResult<R> setResult(R result) {
        this.result = result;
        return this;
    }
}