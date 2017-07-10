package com.meerkat.service;

import com.meerkat.entity.User;

/**
 * Created by wm on 17/4/18.
 */
public interface IUserService {

    /**
     * 创建user
     *
     * @param user
     * @return
     */
    Long create(User user);


    User getById(Long userId);

    /**
     * 根据微信unionId获取user
     *
     * @param unionId
     * @return
     */
    User getByUnionId(String unionId);

    /**
     * 根据微信openId获取user
     *
     * @param openId
     * @return
     */
    User getByOpenId(String openId);

}
