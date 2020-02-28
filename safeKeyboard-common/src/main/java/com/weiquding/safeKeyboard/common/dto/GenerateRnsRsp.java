package com.weiquding.safeKeyboard.common.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

/**
 * description
 *
 * @author beliveyourself
 * @version V1.0
 * @date 2020/2/29
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GenerateRnsRsp {
    @NonNull
    private String rns;
    @NonNull
    private String sign;
}
