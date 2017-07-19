package com.huier.fw_rxjava.entity;

/**
 * 作者：张玉辉
 * 时间：2017/7/19.
 */

public class UserInfo {
    UserBaseInfoResponse mBaseInfo;
    UserExtraInfoResponse mExtraInfo;

    public UserInfo(UserBaseInfoResponse baseInfo, UserExtraInfoResponse extraInfo) {
        mBaseInfo = baseInfo;
        mExtraInfo = extraInfo;
    }
}
