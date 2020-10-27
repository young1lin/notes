package com.young1lin.spring.core.resolve.http;

import org.springframework.stereotype.Component;

/**
 * @author young1lin
 * @version 1.0
 * @date 2020/10/27 7:52 上午
 */
@Component
public class HttpInvokerTestImpl implements HttpInvokerTest {

    @Override
    public String getTestPo(String desp) {
        return "getTestPo" + desp;
    }

}
