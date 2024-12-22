package com.atguigu.lease.web.admin.custom.config;

import com.atguigu.lease.model.enums.BaseEnum;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.converter.ConverterFactory;
import org.springframework.stereotype.Component;
/*
自定义类型转换器 String转成BaseEnum
当前是一个工厂类
 */
@Component
public class StringToBaseEnumConverterFatory implements ConverterFactory<String, BaseEnum> {

    @Override
    public <T extends BaseEnum> Converter<String, T> getConverter(Class<T> targetType) {
        return new Converter<String, T>() {

            @Override
            public T convert(String source) {
                for (T enumConstant : targetType.getEnumConstants()) {
                    if (source.equals(String.valueOf(enumConstant.getCode()))) {
                        return enumConstant;
                    }
                }
                return null;
            }

        };
    }

}
