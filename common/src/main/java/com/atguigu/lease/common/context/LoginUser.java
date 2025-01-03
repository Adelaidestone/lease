package com.atguigu.lease.common.context;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class LoginUser {

    private Long userId;

    private String username;
}
