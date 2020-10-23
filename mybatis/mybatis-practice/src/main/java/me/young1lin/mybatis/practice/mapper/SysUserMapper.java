package me.young1lin.mybatis.practice.mapper;

import me.young1lin.mybatis.practice.domain.SysUser;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @author young1lin
 * @version 1.0
 * @date 2020/9/27 4:53 下午
 */
@Mapper
public interface SysUserMapper {
    /**
     * 查询所有用户
     *
     * @return List<SysUser> 所有用户
     */
    @Select("select * from user limit 10")
    List<SysUser> listSysUser() ;
}
