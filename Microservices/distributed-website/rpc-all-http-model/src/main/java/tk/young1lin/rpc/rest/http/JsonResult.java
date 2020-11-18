package tk.young1lin.rpc.rest.http;

/**
 * @author young1lin
 * @version 1.0
 * @date 2020/7/28 5:11 下午
 */
public class JsonResult {
    /**
     * 结果状态码
     */
    private int resultCode;
    /**
     * 状态码解释消息
     */
    private String message;
    /**
     * 结果
     */
    private Object result;

    public int getResultCode() {
        return resultCode;
    }

    public String getMessage() {
        return message;
    }

    public Object getResult() {
        return result;
    }

    public void setResultCode(int resultCode) {
        this.resultCode = resultCode;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setResult(Object result) {
        this.result = result;
    }

    public JsonResult() {
    }

    public JsonResult(int resultCode, String message, Object result) {
        this.resultCode = resultCode;
        this.message = message;
        this.result = result;
    }

    public JsonResult(JsonResultBuilder jsonResultBuilder) {
        this.message = jsonResultBuilder.getMessage();
        this.result = jsonResultBuilder.getResult();
        this.resultCode = jsonResultBuilder.getResultCode();
    }

    @Override
    public String toString() {
        return "JsonResult{" +
                "resultCode=" + resultCode +
                ", message='" + message + '\'' +
                ", result=" + result.toString() +
                '}';
    }
}
