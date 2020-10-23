package me.younglin.springex.bean;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.List;

/**
 * //@Lazy 这里如果标明 Lazy 启动时不会报错，运行时，进行 依赖查找 时，会报错
 * @author young1lin
 * @version 1.0
 * @date 2020/8/6 2:01 下午
 */
@Component
@Slf4j
public class UserMergeClass {

    private List<MergeClass> mergeClassList;

    public UserMergeClass(MergeClass mergeSubClass, @Qualifier("mergeClass") MergeClass mergeClass, List<MergeClass> mergeClassList){
        this.mergeClassList = mergeClassList;
        log.info(mergeSubClass.toString());
        log.info(mergeClass.toString());
    }

    @PostConstruct
    public void init(){
        log.info("UserMergeClass init");
        log.info(Arrays.toString(mergeClassList.toArray()));
    }
}

