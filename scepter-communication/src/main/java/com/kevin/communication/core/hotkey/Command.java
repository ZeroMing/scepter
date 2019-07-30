package com.kevin.communication.core.hotkey;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author: liangxuekai
 * @description: 接口映射注解
 * @updateRemark: 修改内容(每次大改都要写修改内容)
 * @date: 2019-07-29 18:55
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Command {

}
