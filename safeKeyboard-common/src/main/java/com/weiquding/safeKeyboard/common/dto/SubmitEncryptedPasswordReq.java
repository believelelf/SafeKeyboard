package com.weiquding.safeKeyboard.common.dto;

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
public class SubmitEncryptedPasswordReq {

    private String password;
}
