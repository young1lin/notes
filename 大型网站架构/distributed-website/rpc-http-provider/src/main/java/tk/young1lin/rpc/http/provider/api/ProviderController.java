package tk.young1lin.rpc.http.provider.api;

import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import tk.young1lin.rpc.rest.http.JsonResult;
import tk.young1lin.rpc.rest.http.JsonResultBuilder;
import tk.young1lin.rpc.rest.http.entity.ServiceEntity;
import tk.young1lin.rpc.http.service.BaseService;

import java.util.Map;

/**
 * @author young1lin
 * @version 1.0
 * @date 2020/7/28 5:07 下午
 */
@RestController
public class ProviderController {

    /**
     * 利用 setter 注入，按类型注入所有 BaseService 类型的 Bean，键值为 beanName
     */
    private final Map<String, BaseService> map;

    public ProviderController(Map<String, BaseService> map){
        this.map = map;
    }

    @PostMapping(path = "/testhttprpc/provider", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public JsonResult provider(@RequestParam(name = "service") String service,
                               @RequestParam("arg1") String arg1) {
        System.out.println("进入了该方法");
        BaseService baseService = map.get(service);
        Object result = baseService.execute(arg1);
        String message = "success";
        int resultCode = 200;
        return JsonResultBuilder.create()
                .message(message)
                .result(result)
                .resultCode(resultCode)
                .build();
    }

    @GetMapping(value = "/p")
    public JsonResult getProvider(ServiceEntity serviceEntity) {
        String service = serviceEntity.getService();
        String arg1 = serviceEntity.getArg1();
        System.out.println("service:" + service + "\t arg1" + arg1);
        BaseService baseService = map.get(service);
        Object result = baseService.execute(arg1);
        String message = "success";
        int resultCode = 200;
        return JsonResultBuilder.create()
                .message(message)
                .result(result)
                .resultCode(resultCode)
                .build();
    }
}
