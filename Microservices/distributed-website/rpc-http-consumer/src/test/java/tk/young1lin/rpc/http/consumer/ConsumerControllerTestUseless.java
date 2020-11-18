package tk.young1lin.rpc.http.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.client.RestTemplate;
import tk.young1lin.rpc.http.consumer.api.controller.ConsumerController;

import java.util.HashMap;
import java.util.Map;


/**
 * useless 没用，参考了 Spring boot 官方文档和其他英文博客网站，结果没用
 * @author young1lin
 * @version 1.0
 * @date 2020/7/28 7:01 下午
 */
//@RunWith(SpringRunner.class)
@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = ConsumerController.class)
public class ConsumerControllerTestUseless {

    private MockMvc mockMvc;

    private ConsumerController consumerController;

    private RestTemplate restTemplate;

    public ConsumerControllerTestUseless() {
        restTemplate = Mockito.mock(RestTemplate.class);
        consumerController = new ConsumerController(restTemplate);
    }

    @Before
    public void setUp() {
        this.mockMvc = MockMvcBuilders.standaloneSetup(consumerController)
                .build();
    }

    @Test
    public void hello() throws Exception {
        Map<String, String> map = new HashMap<>(1);
        map.put("name", "张三");
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders
                .post("/hello")
                .content(asJsonString(map))
                .header("Content-Type","application/json"))
                .andReturn();

        String resultStr = result.getResponse().getContentAsString();
        System.out.println(resultStr);
    }

    private String asJsonString(Map<String, String> map) throws JsonProcessingException {
        ObjectMapper obj = new ObjectMapper();
        return obj.writeValueAsString(map);
    }
}
