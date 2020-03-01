package com.weiquding.safeKeyboard.dto;

import lombok.Data;

/**
 * description
 *
 * @author beliveyourself
 * @version V1.0
 * @date 2020/3/1
 */
@Data
public class DigestBean {

    private String appId;
    private String timestamp;
    private String version;
    private String sign;


}
