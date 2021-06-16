package com.ecoplay.android.models.model;

import java.util.Map;

import lombok.Data;

@Data
public class UserModel {
    private String username;
    private String email;
    private String password; // 로그인
    private String password1; // 회원가입
    private String password2; // 회원가입
    private String new_password1; // 패스워드 찾기
    private String new_password2; // 패스워드 찾기
    private String access_token; // 로그인
    private String refresh_token; // 로그인
    private String non_field_errors; // 에러
    private String detail; // 에러
    private boolean legacy; // 로그인
    private Map<String, String> user;
}