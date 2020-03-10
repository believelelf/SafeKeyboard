package com.weiquding.safeKeyboard.common.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 文件加密下载请求参数
 *
 * @author beliveyourself
 * @version V1.0
 * @date 2020/3/11
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SecureDownloadFileReq {

    private String appId;
    private String fileName;
}
