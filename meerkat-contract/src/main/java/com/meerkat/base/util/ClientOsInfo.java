package com.meerkat.base.util;

/**
 * Created by wm on 17/4/18.
 */
public class ClientOsInfo {

    private String platformVersion;
    private String deviceType;
    private String osType;
    private String version;
    private String deviceModel;
    private String userAgent;
    /***
     * 是否是移动设备
     * @return
     */
    public boolean isMobile(){
        return (this.getDeviceType() ==""|| this.getDeviceType() ==null);
    }

    /***
     * 比如 Android_3.0
     */
    public String getPlatformVersion() {
        return platformVersion;
    }

    public void setPlatformVersion(String platformVersion) {
        this.platformVersion = platformVersion;
    }

    /***
     * Pad或Phone
     */
    public String getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }

    /***
     * os type
     */
    public String getOsType() {
        return osType;
    }

    public void setOsType(String osType) {
        this.osType = osType;
    }

    /***
     * 只是版本号,例如"4.1.1"
     */
    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    public String getDeviceModel() {
        return deviceModel;
    }

    public void setDeviceModel(String deviceModel) {
        this.deviceModel = deviceModel;
    }

}
