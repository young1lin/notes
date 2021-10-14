package tk.young1lin.kafkatest;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * @author young1lin
 * @version 1.0
 * @date 2020/7/16 9:46 下午
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class MessageEntity {
    private String name;
    private Long id;
    private String remark;
}
