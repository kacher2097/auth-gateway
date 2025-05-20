package com.authenhub.bean.passwordmanager;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CheckPasswordStrengthResponse {
    private int score;
    private String strength;
    private List<String> feedback;
}
