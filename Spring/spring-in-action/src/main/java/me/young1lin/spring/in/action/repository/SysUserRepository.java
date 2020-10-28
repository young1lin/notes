package me.young1lin.spring.in.action.repository;

import me.young1lin.spring.in.action.domain.SysUser;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * @author young1lin
 * @version 1.0
 * @date 2020/9/7 5:16 下午
 */
@Repository
public interface SysUserRepository extends CrudRepository<SysUser,Long> {
    /**
     * 根据用户名找到用户
     * @param username
     * @return
     */
    SysUser findByUsername(String username);
}
