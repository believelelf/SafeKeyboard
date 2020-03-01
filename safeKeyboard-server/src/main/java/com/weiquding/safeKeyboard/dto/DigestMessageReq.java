package com.weiquding.safeKeyboard.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.List;

/**
 * 消息摘要测试Bean
 *
 * @author beliveyourself
 * @version V1.0
 * @date 2020/3/1
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class DigestMessageReq extends DigestBean {

    private String name;
    private List<String> email;
    private String[] address;


}
