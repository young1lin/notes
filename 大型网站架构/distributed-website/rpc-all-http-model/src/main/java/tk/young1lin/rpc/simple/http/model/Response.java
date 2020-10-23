package tk.young1lin.rpc.simple.http.model;

/**
 * @author young1lin
 * @version 1.0
 * @date 2020/7/28 11:27 上午
 */
public class Response {
    /**
     * 编码
     */
    private final byte encode;

    /**
     * 响应
     */
    private final String response;

    /**
     * 响应长度
     */
    private final int responseLength;

    public byte getEncode() {
        return encode;
    }

    public String getResponse() {
        return response;
    }

    public int getResponseLength() {
        return responseLength;
    }

    public static class ResponseBuilder {
        private byte encode = 0;
        private String response = "";
        private int responseLength = 0;

        public Response.ResponseBuilder encode(byte encode) {
            this.encode = encode;
            return this;
        }

        public Response.ResponseBuilder response(String response) {
            this.response = response;
            return this;
        }

        public Response.ResponseBuilder responseLength(int responseLength) {
            this.responseLength = responseLength;
            return this;
        }

        public Response build() {
            return new Response(this);
        }
    }

    public Response(Response.ResponseBuilder builder) {
        encode = builder.encode;
        response = builder.response;
        responseLength = builder.responseLength;
    }

    @Override
    public String toString() {
        return "Response{" +
                "encode=" + encode +
                ", response='" + response + '\'' +
                ", responseLength=" + responseLength +
                '}';
    }
}
