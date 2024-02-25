package com.zelong.bilibili.service;

import com.mysql.cj.util.StringUtils;
import com.zelong.bilibili.constant.UserConstant;
import com.zelong.bilibili.dao.UserDao;
import com.zelong.bilibili.domain.User;
import com.zelong.bilibili.domain.UserInfo;
import com.zelong.bilibili.exception.ConditionException;
import com.zelong.bilibili.service.utils.MD5Util;
import com.zelong.bilibili.service.utils.RSAUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class UserService {

    @Autowired
    private UserDao userDao;

    public void addUser(User user) {
        String phone = user.getPhone();
        if (StringUtils.isNullOrEmpty(phone)) {
            throw new ConditionException("Phone number should not be empty!");
        }
        User dbUser = this.getUserByPhone(phone);
        if(dbUser != null) {
            throw new ConditionException("This phone number has been registered!");
        }
        Date now = new Date();
        String salt = String.valueOf(now.getTime());
        String password = user.getPassword();
        String rawPassword;
        try {
           rawPassword = RSAUtil.decrypt(password);
        } catch (Exception e) {
            throw new ConditionException("Failed to decrypt password!");
        }
        String md5Password = MD5Util.sign(rawPassword, salt, "UTF-8");
        user.setSalt(salt);
        user.setPassword(md5Password);
        user.setCreateTime(now);
        userDao.addUser(user);
        // add user info
        UserInfo userInfo = new UserInfo();
        userInfo.setUserId(user.getId());
        userInfo.setNick(UserConstant.DEFAULT_NICK);
        userInfo.setBirth(UserConstant.DEFAULT_BIRTH);
        userInfo.setGender(UserConstant.GENDER_NA);
        userInfo.setCreateTime(now);
        userDao.addUserInfo(userInfo);
    }

    public User getUserByPhone(String phone) {
        return userDao.getUserByPhone(phone);
    }
}
