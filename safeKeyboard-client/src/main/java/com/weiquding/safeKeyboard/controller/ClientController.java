package com.weiquding.safeKeyboard.controller;

import com.weiquding.safeKeyboard.common.cache.KeyInstance;
import com.weiquding.safeKeyboard.common.exception.CipherRuntimeException;
import com.weiquding.safeKeyboard.common.util.RSAUtil;
import com.weiquding.safeKeyboard.common.util.RandomUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpSession;
import java.util.Base64;
import java.util.Map;
import java.util.UUID;

/**
 * 模拟获取密钥与密文
 *
 * @author believeyourself
 */
@RestController
public class ClientController {

    @Autowired
    private RestTemplate restTemplate;

    /**
     * @param session
     */
    @RequestMapping("/generateRNC")
    public void generateRNC(HttpSession session) {
        String RNC = RandomUtil.generateClientRandomString();
        session.setAttribute("RNC", RNC);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("RNC", RNC);
        map.add("requestId", UUID.randomUUID().toString());
        map.add("timestamp", String.valueOf(System.currentTimeMillis()));
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);

        Map<String, String> retVal = restTemplate.postForObject("http://localhost:8082/generateRNS", request, Map.class);
        String RNS = retVal.get("RNS");
        String sign = retVal.get("sign");
        boolean verify = RSAUtil.verifySignByRSAPublicKey(KeyInstance.RSA_PUBLIC_KEY, RNS.getBytes(), Base64.getDecoder().decode(sign));
        if(!verify){
            throw new CipherRuntimeException("Signature corrupted");
        }
        session.setAttribute("RNS", RNS);
    }

}
