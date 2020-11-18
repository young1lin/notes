package tk.young1lin.rpc.http.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.HashMap;
import java.util.Map;

import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;


/**
 * 这个是有用的
 *
 * @author young1lin
 * @version 1.0
 * @date 2020/7/28 7:01 下午
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK, classes = {ConsumerApplication.class})
public class ConsumerControllerTestUseful {

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext webApplicationContext;


    public ConsumerControllerTestUseful() {
    }

    @Before
    public void setUp() {
        mockMvc = webAppContextSetup(webApplicationContext).build();
    }

    @Test
    public void hello() throws Exception {
        Map<String, String> map = new HashMap<>(1);
        map.put("name", "张三");
        String requestStr = asJsonString(map);
        System.out.println(requestStr);
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders
                .post("/hello")
                .content(requestStr)
                .header("Content-Type", "application/json"))
                .andReturn();

        String resultStr = result.getResponse().getContentAsString();
        System.out.println(resultStr);
    }

    private String asJsonString(Map<String, String> map) throws JsonProcessingException {
        ObjectMapper obj = new ObjectMapper();
        return obj.writeValueAsString(map);
    }
}
