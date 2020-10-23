package tk.young1lin.rpc.rest.http;


/**
 * @author young1lin
 * @version 1.0
 * @date 2020/7/28 5:16 下午
 */
public class JsonResultBuilder {

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

    int getResultCode() {
        return resultCode;
    }

    String getMessage() {
        return message;
    }

    Object getResult() {
        return result;
    }

    private JsonResultBuilder(){}

    public static JsonResultBuilder create(){
        return new JsonResultBuilder();
    }

    public JsonResultBuilder resultCode(int resultCode) {
        this.resultCode = resultCode;
        return this;
    }

    public JsonResultBuilder message(String message) {
        this.message = message;
        return this;
    }

    public JsonResultBuilder result(Object result) {
        this.result = result;
        return this;
    }

    public JsonResult build() {
        return new JsonResult(this);
    }
}
