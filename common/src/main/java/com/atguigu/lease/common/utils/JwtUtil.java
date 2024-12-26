package com.atguigu.lease.common.utils;

import com.atguigu.lease.common.exception.LeaseException;
import com.atguigu.lease.common.result.ResultCodeEnum;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.util.Date;

public class JwtUtil {

    private static long expireTime = 60 * 60 * 1000;
    //密钥
    private static SecretKey secretKey = Keys.hmacShaKeyFor("c0cc0dfdfaef2370e4d56711175fe349def6ed4cba25d3c7a01fc6ef6568220d".getBytes());

    /*
     *   创建一个token: 携带登录用户的信息
     *    登录用户的信息:  1.用户名(账号) username   2.用户的id  作为token payload负载
     *
     * claim:方法 设置登录用户的信息(负载)
     * signWith() 设置签名
     *
     * eyJhbGciOiJIUzUxMiIsInppcCI6IkdaSVAifQ.
     * H4sIAAAAAAAA_6tWKi5NUrJSKq4sDi1OLdItyc9OzVPSUUqtKFCyMjQ3NjUwNrY0MNdRKgXKeqYAxSBMv8TcVKC2qozEvPTixDylWgA-gYavSQAAAA.
     * VO_II6jw0CbRI_hm_t8Zvlnht0H82_PprSe_ChGVPplqXe1v5E3uk69COdof9XrRQzanlKv2Ih1BZ2FmkoVFJw
     * */
    public static String createToken(Long userId, String userName) {

        String token = Jwts.builder()
                .setSubject("sysUser-token")
                .setExpiration(new Date(System.currentTimeMillis() + expireTime))
                .claim("userId", userId)
                .claim("userName", userName)
                .signWith(secretKey)
                .compressWith(CompressionCodecs.GZIP)
                .compact();
        return token;
    }

    /*
     *   验证token(解析token)
     * */
    public static Claims parseToken(String token) {
        try {
            Jws<Claims> claimsJws = Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(token);

            //获取负载数据
            Claims body = claimsJws.getBody();
            return body;
        } catch (ExpiredJwtException e) {
            throw new LeaseException(ResultCodeEnum.ADMIN_CAPTCHA_CODE_EXPIRED);
            //  throw  new RuntimeException(e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
