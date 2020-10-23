package tk.young1lin.rpc.simple.http.model;

/**
 * @author young1lin
 * @version 1.0
 * @date 2020/7/28 11:27 上午
 */
public class Request {
    /**
     * 编码
     */
    private final byte encode;
    /**
     * 指令
     */
    private final String command;
    /**
     * 指令长度
     */
    private final int commandLength;

    public byte getEncode() {
        return encode;
    }

    public String getCommand() {
        return command;
    }

    public int getCommandLength() {
        return commandLength;
    }

    public static class RequestBuilder {
        private byte encode = 0;
        private String command = "";
        private int commandLength = 0;

        public RequestBuilder encode(byte encode) {
            this.encode = encode;
            return this;
        }

        public RequestBuilder command(String command) {
            this.command = command;
            return this;
        }

        public RequestBuilder commandLength(int commandLength) {
            this.commandLength = commandLength;
            return this;
        }

        public Request build(){
            return new Request(this);
        }
    }

    public Request(RequestBuilder builder) {
        encode = builder.encode;
        command = builder.command;
        commandLength = builder.commandLength;
    }

    @Override
    public String toString() {
        return "Request{" +
                "encode=" + encode +
                ", command='" + command + '\'' +
                ", commandLength=" + commandLength +
                '}';
    }
}
