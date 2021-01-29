package me.younglin.springex.service;

import lombok.extern.slf4j.Slf4j;
import me.younglin.springex.bean.GenericBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * @author young1lin
 * @version 1.0
 * @date 2020/7/22 11:43 下午
 */
@Component("scopeService")
@Scope("thread-local")
@Slf4j
public class ScopeService {

    private GenericBean genericBean;

    @Autowired
    public void setGenericBean(GenericBean genericBean){
        this.genericBean = genericBean;
    }

    @PostConstruct
    public void init(){
        log.info(genericBean.toString());
        //System.out.println(genericBean.toString());
    }

    public String getMessage() {
        String s = "I'm ok";
        System.out.println(s);
        return s;
    }
}
