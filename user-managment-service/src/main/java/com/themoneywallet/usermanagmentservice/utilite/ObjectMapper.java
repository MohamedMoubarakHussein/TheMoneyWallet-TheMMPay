package com.themoneywallet.usermanagmentservice.utilite;

import java.lang.reflect.Field;

import org.modelmapper.ModelMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class ObjectMapper {
    private final ModelMapper modelMapper;
    public <T, U> U map(T source , U distantion){
        this.modelMapper.map(source, distantion);
        return distantion;
    }

    public <T,U> U map2(T source , U distantion){
        Class<?> sourceClass = source.getClass();
        try{
        for(Field field : sourceClass.getDeclaredFields()){
            field.setAccessible(true);
            Object getValue = field.get(source);
            if(getValue != null){
                Field dist = distantion.getClass().getDeclaredField(field.getName());
                dist.setAccessible(true);
                dist.set(distantion, getValue);
            }
        }}catch(NoSuchFieldException | IllegalAccessException ex){
            ex.printStackTrace();
        }
        return distantion;
    }
}
