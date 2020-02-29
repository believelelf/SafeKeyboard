package com.weiquding.safeKeyboard.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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
public class GetEncryptedPasswordRsp {
    private String password;
}
