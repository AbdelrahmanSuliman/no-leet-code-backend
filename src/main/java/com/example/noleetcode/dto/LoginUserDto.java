package com.example.noleetcode.dto;
//User could use either email or username to login
public record LoginUserDto(String username,
                           String password) {}
