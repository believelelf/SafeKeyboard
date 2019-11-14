package com.weiquding.safeKeyboard.controller;

import com.weiquding.safeKeyboard.common.util.RandomUtil;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.util.Map;

/**
 * 模拟生成密钥与密文
 *
 * @author believeyourself
 */
@RestController
public class ServerController {

    @RequestMapping("/generateRNS")
    public Map<String, String> generateRNC(@RequestParam("RNC") String RNC, HttpSession session) {
        Map<String, String> map = RandomUtil.generateServerRandomStringAndSign();
        session.setAttribute("RNC", RNC);
        session.setAttribute("RNS", map.get("RNS"));
        return map;
    }
}
