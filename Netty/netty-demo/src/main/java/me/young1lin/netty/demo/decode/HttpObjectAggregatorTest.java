package me.young1lin.netty.demo.decode;

import io.netty.handler.codec.http.HttpObjectAggregator;

/**
 * @author young1lin
 * @version 1.0
 * @date 2020/10/29 9:27 下午
 */
public class HttpObjectAggregatorTest extends HttpObjectAggregator {

    public HttpObjectAggregatorTest(int maxContentLength) {
        super(maxContentLength);
    }

    public HttpObjectAggregatorTest(int maxContentLength, boolean closeOnExpectationFailed) {
        super(maxContentLength, closeOnExpectationFailed);
    }

}
