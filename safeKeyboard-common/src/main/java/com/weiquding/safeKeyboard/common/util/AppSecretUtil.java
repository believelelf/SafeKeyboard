package com.weiquding.safeKeyboard.common.util;

import com.weiquding.safeKeyboard.common.exception.BaseBPError;
import lombok.extern.slf4j.Slf4j;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 消息摘要方法
 *
 * @author beliveyourself
 * @version V1.0
 * @date 2020/1/14
 */
@Slf4j
public class AppSecretUtil {

    private static final String TIMESTAMP = "timestamp";

    private static final String VERSION = "version";

    private static final String SIGN = "sign";

    /**
     * 第一步，设所有发送或者接收到的数据为集合M，将集合M内非空参数值的参数按照参数名ASCII码从小到大排序（字典序），使用URL键值对的格式（即key1=value1&key2=value2…）拼接成字符串stringA。
     * <p>
     * 特别注意以下重要规则：
     * ◆ 参数名ASCII码从小到大排序（字典序）；
     * ◆ 如果参数的值为空不参与签名；
     * ◆ 参数名区分大小写；
     * 第二步，在stringA最后拼接上key得到stringSignTemp字符串，并对stringSignTemp进行MD5运算，再将得到的字符串所有字符转换为大写，得到sign值signValue。
     * sign=MD5(stringSignTemp).toUpperCase()="9A0A8659F005D6984697E2CA0A9CF3B7" //注：MD5签名方式
     * sign=hash_hmac("sha256",stringSignTemp,key).toUpperCase()="6A9AE1657590FD6257D693A078E1C3E4BB6BA4DC30B23E0EE2496E54170DACD6" //注：HMAC-SHA256签名方式
     *
     * @param appSecret 密钥
     * @param kvs       键值对
     * @return 签名后参数
     */
    public static Map<String, Object> getParams(String appSecret, Object[] kvs) {
        TreeMap<String, Object> map = expatParams(kvs);
        //  增加随机量
        map.put(TIMESTAMP, new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));
        if (!map.containsKey(VERSION)) {
            map.put(VERSION, "1.0");
        }
        String sign = generateSign(appSecret, map, true);
        map.put(SIGN, sign);
        return map;
    }

    private static TreeMap<String, Object> expatParams(Object[] kvs) {
        if (kvs == null || kvs.length % 2 != 0) {
            throw new IllegalArgumentException("Illegal Argument: " + Arrays.toString(kvs));
        }
        TreeMap<String, Object> map = new TreeMap<>();
        try {
            for (int i = 0; i < kvs.length; i += 2) {
                String key = kvs[i].toString();
                Object value = kvs[i + 1];
                if (value == null) {
                    continue;
                }
                if (value instanceof Iterable) {
                    Iterable<?> iterable = ((Iterable<?>) value);
                    value = iterable.iterator();
                }
                if (value instanceof Iterator) {
                    Iterator<?> it = ((Iterator<?>) value);
                    int index = 0;
                    while (it.hasNext()) {
                        Object obj = it.next();
                        map.put(key + "[" + index + "]", obj);
                        index++;
                    }
                } else if (value.getClass().isArray()) {
                    Object[] arr = (Object[]) value;
                    for (int index = 0; index < arr.length; index++) {
                        Object obj = arr[index];
                        map.put(key + "[" + index + "]", obj);
                    }
                } else {
                    map.put(key, value);
                }
            }
        } catch (Exception e) {
            throw BaseBPError.ORGANIZE_PARAMETERS.getInfo().initialize(e);
        }
        return map;
    }

    private static String generateSign(String appSecret, TreeMap<String, Object> map, boolean isURLEncoding) {
        // 生成URL键值对
        StringBuilder builder = new StringBuilder();
        Iterator<Map.Entry<String, Object>> it = map.entrySet().iterator();
        try {
            while (it.hasNext()) {
                Map.Entry<String, Object> entry = it.next();
                Object value = entry.getValue();
                if (value != null) {
                    String valueStr = isURLEncoding ?
                            URLEncoder.encode(value.toString(), StandardCharsets.UTF_8)
                            : value.toString();
                    entry.setValue(valueStr);
                    builder.append('&').append(entry.getKey()).append('=').append(valueStr);
                }
            }
        } catch (Exception e) {
            throw BaseBPError.SPLICING_PARAMETERS.getInfo().initialize(e);
        }

        // 生成摘要值
        String stringSignTemp = builder.append("&key=").append(appSecret).toString().substring(1);
        byte[] md5Value = MD5Util.md5(stringSignTemp.getBytes(StandardCharsets.UTF_8));
        return Base64.getUrlEncoder().encodeToString(md5Value);
    }

    /**
     * 验证消息摘要
     *
     * @param appSecret 密钥
     * @param data      待验证参数
     */
    @SuppressWarnings("unchecked")
    public static void verifyMessageDigest(String appSecret, Object data) {
        TreeMap<String, Object> params = expatParams(convertKvs(data));
        String sign = generateSign(appSecret, params, false);
        String originSign = (String) ReflectUtil.getFieldValue(data, SIGN);
        if (!sign.equals(originSign)) {
            throw BaseBPError.DATA_DIGEST.getInfo().initialize();
        }

    }

    @SuppressWarnings("unchecked")
    private static Object[] convertKvs(Object data) {
        Map<String, Object> params = null;
        if (data instanceof Map) {
            params = (Map<String, Object>) data;
        } else {
            params = ReflectUtil.bean2Map(data);
        }
        Object[] kvs = new Object[(params.size() - 1) * 2];
        int index = 0;
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            String key = entry.getKey();
            if (SIGN.equals(key)) {
                continue;
            }
            kvs[index++] = key;
            kvs[index++] = entry.getValue();
        }
        return kvs;
    }
}
