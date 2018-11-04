package com.bitspace.food.util;

import com.bitspace.food.config.SystemConfig;
import com.bitspace.food.util.db.DbDataTypeUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.apache.commons.codec.binary.Base64;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


/**
 *All rights Reserved, Designed By www.bitzone.zone
 *@package_name com.bitspace.food
 *@class_name   TransactionApplication
 *@auth         Administrator
 *@create_time  18-3-13 下午8:00
 *@company      香港币特空间交易平台有限公司
 *@comments
 *@method_name
 *@return
 * Copyright (c) 2018 www.bitzone.zone Inc. All rights reserved.
 * 香港币特空间交易平台有限公司版权所有
 * 注意：本内容仅限于香港币特空间交易平台有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public class JwtUtil {

	private static String secretCode = "cblcJDjkddwj";  //

	/**
	 * 由字符串生成加密key
	 */
	public static SecretKey generalKey(){
		String stringKey = SystemConfig.getJwtCnf().getKeyPrefix() + SystemConfig.getJwtCnf().getSecretKey() ;
		byte[] encodedKey = Base64.decodeBase64(stringKey);
		SecretKey key = new SecretKeySpec(encodedKey, 0, encodedKey.length, "AES");
		return key;
	}

	/**
	 * 创建jwtToken
	 */
	public static String createJwtToken(long uid) throws Exception{
		return  createJwtToken(uid,generalSubject(uid), SystemConfig.getJwtCnf().getExpireTime());
	}


	public static String createJwtToken(long uid,String subject,long ttl) throws Exception{
		SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;
		long nowMillis = System.currentTimeMillis();
		Date now = new Date(nowMillis);
		JwtBuilder builder = Jwts.builder()
				.setIssuedAt(now)
				.setSubject(subject)
				.signWith(signatureAlgorithm, generalKey());
		if(ttl>0){
            long expMills = nowMillis + ttl;
			builder.setExpiration(new Date(expMills));
		}
		return builder.compact();
	}

	public static Claims parseJwtToken(String jwt) throws Exception{

		SecretKey key = generalKey();
		Claims claims = Jwts.parser()
				.setSigningKey(key)
				.parseClaimsJws(jwt).getBody();

		return claims;
	}

	public static String generalSubject(long uid) throws Exception{
        Map<String,Object> map = new HashMap<String,Object>();
		map.put("uid",uid);
		return JsonUtil.toJson(map);
	}

	public static Long getUidByJwt(String token) throws Exception{
		//String jwtToken = AESUtil.decryptFromBase64(token,secretCode);
		Claims claims = parseJwtToken(token);
		if(claims!=null){
			String subject = claims.getSubject();
			Map<String,Object> map = JsonUtil.fromJson(subject,Map.class);
			if(map.containsKey("uid")){
				Long uid = DbDataTypeUtil.toLong(map.get("uid"));
				return uid;
			}
		}
		return null;
	}
	
	public static Long getRoleByJwt(String token) throws Exception{
		//String jwtToken = AESUtil.decryptFromBase64(token,secretCode);
		Claims claims = parseJwtToken(token);
		if(claims!=null){
			String subject = claims.getSubject();
			Map<String,Object> map = JsonUtil.fromJson(subject,Map.class);
			if(map.containsKey("role")){
				Long uid = DbDataTypeUtil.toLong(map.get("role"));
				return uid;
			}
		}
		return null;
	}

	public static String getToken(HttpServletRequest request){
		return request.getHeader(SystemConfig.getJwtCnf().getTokenHeader());
	}

	public static Long getUid(HttpServletRequest request){
		String token = request.getHeader(SystemConfig.getJwtCnf().getTokenHeader());
		if(token == null)return null;
		try {
			return JwtUtil.getUidByJwt(token);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}


	public static void main(String[] args) throws Exception {
		String token = JwtUtil.createJwtToken(1L);
		Long uid = JwtUtil.getUidByJwt("eyJhbGciOiJIUzI1NiJ9.eyJpYXQiOjE1MDYwNjIxODYsInN1YiI6IntcInVpZFwiOjgzfSIsImV4cCI6MTUwNjE0ODU4Nn0.9om2bLjaYFwbpoz508c7Rz50Sq9g6GGIXxMZeHViSnE");
		System.out.println(uid);
//		System.out.println(AESUtil.decryptFromBase64(token,secretCode));
//		System.out.println(token);
//		System.out.println(uid);
	}

}
