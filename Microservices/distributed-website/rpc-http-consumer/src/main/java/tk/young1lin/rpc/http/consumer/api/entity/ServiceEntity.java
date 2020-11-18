package tk.young1lin.rpc.http.consumer.api.entity;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

/**
 * @author young1lin
 * @version 1.0
 * @date 2020/8/3 2:59 下午
 */
@Data
@AllArgsConstructor
public class ServiceEntity implements Serializable {
    private String service;
    private String arg1;
    private String format;
}
