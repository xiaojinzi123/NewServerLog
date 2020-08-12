package com.xiaojinzi;

public interface NetworkProvider {

    /**
     * 获取设备名称, 可能重复
     */
    String getDeviceName();

    /**
     * 获取一个唯一的 tag
     */
    String getTag();

    /**
     * 获取组合的设备名称, 是唯一的
     */
    String getCombineDeviceName();

    /**
     * 发送数据
     */
    void send(String data);

}
