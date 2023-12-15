package com.themoneywallet.usermanagmentservice.utilite;

import org.modelmapper.ModelMapper;
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
}
