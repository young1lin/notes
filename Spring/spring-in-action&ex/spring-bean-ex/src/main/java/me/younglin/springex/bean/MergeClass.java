package me.younglin.springex.bean;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.stereotype.Component;

/**
 * @author young1lin
 * @version 1.0
 * @date 2020/8/6 1:57 下午
 */
@Component("mergeClass")
@Data
@ToString
@NoArgsConstructor
public class MergeClass {

    private String name = "name";
    private Integer age = 24;
}
