package com.imooc.ecommerce.service.impl;

import com.alibaba.fastjson.JSON;
import com.imooc.ecommerce.constant.AuthorityConstant;
import com.imooc.ecommerce.constant.CommonConstant;
import com.imooc.ecommerce.dao.EcommerceUserDao;
import com.imooc.ecommerce.entity.EcommerceUser;
import com.imooc.ecommerce.service.IJWTService;
import com.imooc.ecommerce.vo.LoginUserInfo;
import com.imooc.ecommerce.vo.UsernameAndPassword;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sun.misc.BASE64Decoder;

import javax.annotation.Resource;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Objects;
import java.util.UUID;


/**
 * JWT  X相关服务接口实现
 */
@Slf4j
@Service
@Transactional(rollbackFor = Exception.class)
public class JWTServiceImpl implements IJWTService {

    @Resource
    private EcommerceUserDao ecommerceUserDao;

    @Override
    public String generateToken(String username, String password) throws Exception {

        return generateToken(username, password, 0);
    }

    @Override
    public String generateToken(String username, String password, int expire) throws Exception {


        // 首先需要验证用户是否能通过授权校验，即输入的用户名和密码能否匹配数据表记录
        EcommerceUser ecommerceUser = ecommerceUserDao.findByUsernameAndPassword(username, password);
        if (Objects.isNull(ecommerceUser)) {
            log.error("can not find user :[{}] , [{}]", username, password);
            return null;
        }

        // Token 中塞入对象，即JWT中存储的信息，后端拿到这些信息就可以知道哪些用户在操作

        LoginUserInfo loginUserInfo = new LoginUserInfo(ecommerceUser.getId(), ecommerceUser.getUsername());

        if (expire <= 0) {
            expire = AuthorityConstant.DEFAULT_EXPIRE_DAY;
        }

        // 计算超时时间
        ZonedDateTime zonedDateTime = LocalDate.now().plus(1, ChronoUnit.DAYS).atStartOfDay(ZoneId.systemDefault());
        Date expireDate = Date.from(zonedDateTime.toInstant());

        return Jwts.builder()
                // JWT   payload------>KV 用户信息
                .claim(CommonConstant.JWT_USER_INFO_KEY, JSON.toJSONString(loginUserInfo))
                // jwt id 没有作用
                .setId(UUID.randomUUID().toString())
                // JWT 过期时间
                .setExpiration(expireDate)
                // jwt 签名----->加密
                .signWith(getPrivateKey(), SignatureAlgorithm.RS256)
                .compact();

    }

    @Override
    public String registerUserAndGenerateToken(UsernameAndPassword usernameAndPassword) throws Exception {


        // 先去校验用户名是否存在，如果存在，不能重复注册
        EcommerceUser ecommerceUser = ecommerceUserDao.findByUsername(usernameAndPassword.getUsername());
        if (Objects.nonNull(ecommerceUser)) {
            log.error("username is registered : [{}]", ecommerceUser.getUsername());
            return null;
        }
        ecommerceUser = new EcommerceUser();
        ecommerceUser.setUsername(usernameAndPassword.getUsername());
        ecommerceUser.setPassword(usernameAndPassword.getPassword()); //MD5编码以后

        // 注册一个新用户，写一条记录到数据表中
        EcommerceUser save = ecommerceUserDao.save(ecommerceUser);
        log.info("register user success: [{}],[{}]", save.getUsername(), save.getId());

        // 生成token
        return generateToken(save.getUsername(), save.getPassword());

    }


    /**
     * 根据本地存储的私钥获取到 PrivateKey 对象
     *
     * @return
     */
    public PrivateKey getPrivateKey() throws Exception {

        PKCS8EncodedKeySpec pkcs8EncodedKeySpec = new PKCS8EncodedKeySpec(
                new BASE64Decoder().decodeBuffer(AuthorityConstant.PRIVETE_KEY));
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePrivate(pkcs8EncodedKeySpec);


    }
}
