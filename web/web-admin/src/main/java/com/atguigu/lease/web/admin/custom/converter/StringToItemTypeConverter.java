package com.atguigu.lease.web.admin.custom.converter;

import com.atguigu.lease.model.enums.ItemType;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.type.TypeFactory;

import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.converter.GenericConverter;
import org.springframework.stereotype.Component;

@Component
public class StringToItemTypeConverter implements Converter<String, ItemType> {
   /*
   /s：被转换的数据
    */
    @Override
    public ItemType convert(String code) {
        for (ItemType value : ItemType.values()) {
            if (code.equals(String.valueOf(value.getCode()))) {
                return value;
            }
        }
        return null;
    }


}
