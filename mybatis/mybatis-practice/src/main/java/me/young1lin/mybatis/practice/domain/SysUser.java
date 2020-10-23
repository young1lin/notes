package me.young1lin.mybatis.practice.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * @author young1lin
 * @version 1.0
 * @date 2020/9/23 10:26 上午
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class SysUser {
    private Long id;
    private String createTime;
    private String name;
    private String password;
    private String phone;
    private String nickName;
}
