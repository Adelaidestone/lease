package com.atguigu.lease.model.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;


public enum ItemType implements BaseEnum {

    APARTMENT(1, "公寓"),

    ROOM(2, "房间");


    @EnumValue//使用Mybatis当中的类型转换器 TypeHandler 把枚举对象中的code取出来作为条件
    @JsonValue////jackson格式化工具：在处理实体类从数据库返回的属性时（把jav对象转换成json对象） 吧枚举对象code赋值给type字符串类型
    private Integer code;
    private String name;

    @Override
    public Integer getCode() {
        return this.code;
    }


    @Override
    public String getName() {
        return name;
    }

    ItemType(Integer code, String name) {
        this.code = code;
        this.name = name;

    }

}
