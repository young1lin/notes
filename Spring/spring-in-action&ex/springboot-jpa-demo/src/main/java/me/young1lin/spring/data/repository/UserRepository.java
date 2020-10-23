package me.young1lin.spring.data.repository;

import org.springframework.data.repository.CrudRepository;
import me.young1lin.spring.data.entity.UserEntity;

/**
 * @author young1lin
 * @version 1.0
 * @date 2020/8/11 4:32 下午
 */
public interface UserRepository extends CrudRepository<UserEntity,Long> {
    // 这里的 Long 表示 id 类型
}
