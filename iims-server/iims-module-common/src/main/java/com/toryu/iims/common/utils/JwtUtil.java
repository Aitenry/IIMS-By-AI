package com.toryu.iims.common.utils;

import com.toryu.iims.common.constant.MessageConstant;
import com.toryu.iims.common.exception.IsTokenException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.util.Date;
import java.util.Map;
import java.util.UUID;

public class JwtUtil {
    /**
     * 生成jwt
     * 使用Hs256算法, 私匙使用固定秘钥
     *
     * @param secretKey jwt秘钥
     * @param ttlMillis jwt过期时间(毫秒)
     * @param claims    设置的信息
     * @return String
     */
    public static String createJWT(String secretKey, long ttlMillis, Map<String, Object> claims) {
        // 指定签名的时候使用的签名算法，也就是header那部分
        SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;

        // 生成JWT的时间
        long expMillis = System.currentTimeMillis() + ttlMillis;
        Date exp = new Date(expMillis);

        String compact = Jwts.builder()
                .setId(UUID.randomUUID().toString())//设置唯一标识
                .setClaims(claims)
                .setExpiration(exp) //设置过期时间
                .setIssuedAt(new Date()) //令牌签发时间
                .signWith(signatureAlgorithm, secretKey)
                .compact();//签名算法, 秘钥
        System.out.println(compact);
        return compact;
    }

    /**
     * Token解密
     *
     * @param secretKey jwt秘钥 此秘钥一定要保留好在服务端, 不能暴露出去, 否则sign就可以被伪造, 如果对接多个客户端建议改造成多个
     * @param token     加密后的token
     * @return Claims
     */
    public static Claims parseJWT(String secretKey, String token) {
        // 得到DefaultJwtParser
        return Jwts.parser()
                // 设置签名的秘钥
                .setSigningKey(secretKey)
                // 设置需要解析的jwt
                .parseClaimsJws(token).getBody();
    }

    /**
     * 校验Token是否过期或者无效
     */
    public static void isExpiration(String secretKey, String token) {
        try {
            Claims claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody();
            System.out.println(claims);
        } catch (Exception e) {
            String message = e.getMessage();
            if (message.contains("JWT signature does not match locally computed signature")) {
                String msg = MessageConstant.INVALID_TOKEN + " " + message;
                throw new IsTokenException(msg);
            } else if (message.contains("JWT expired at")) {
                String msg = MessageConstant.TOKEN_EXPIRED + " " + message;
                throw new IsTokenException(msg);
            } else {
                throw new IsTokenException(e.getMessage());
            }
        }
    }
}
