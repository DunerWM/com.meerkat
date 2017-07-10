package com.meerkat.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

/**
 * Created by wm on 17/4/18.
 */
@Table
public class SsoToken implements Serializable {
    public static final long serialVersionUID = 1;

    @Id
    private Long id;

    @Column
    private String token;

    @Column
    private Long userId;

    @Column
    private Date expiryTime;

    @Column
    private Date createdAt;

    @Column
    private Date updatedAt;
    @Column
    private Integer terminal;
    @Column
    private String appVersion;
    @Column
    private String appChannel;
    @Column
    private String lastVisitIp;
    @Column
    private String platformVersion;
    @Column
    private String deviceModel;
    @Column
    private String deviceToken;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Date getExpiryTime() {
        return expiryTime;
    }

    public void setExpiryTime(Date expiryTime) {
        this.expiryTime = expiryTime;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    @JsonIgnore
    public boolean isExpired() {
        return expiryTime.before(new Date());
    }

    public Integer getTerminal() {
        return terminal;
    }

    public void setTerminal(Integer terminal) {
        this.terminal = terminal;
    }

    public String getAppVersion() {
        return appVersion;
    }

    public void setAppVersion(String appVersion) {
        this.appVersion = appVersion;
    }

    public String getAppChannel() {
        return appChannel;
    }

    public void setAppChannel(String appChannel) {
        this.appChannel = appChannel;
    }

    public String getLastVisitIp() {
        return lastVisitIp;
    }

    public void setLastVisitIp(String lastVisitIp) {
        this.lastVisitIp = lastVisitIp;
    }

    public String getDeviceToken() {
        return deviceToken;
    }

    public void setDeviceToken(String deviceToken) {
        this.deviceToken = deviceToken;
    }

    public String getPlatformVersion() {
        return platformVersion;
    }

    public void setPlatformVersion(String platformVersion) {
        this.platformVersion = platformVersion;
    }

    public String getDeviceModel() {
        return deviceModel;
    }

    public void setDeviceModel(String deviceModel) {
        this.deviceModel = deviceModel;
    }

    @Override
    public String toString() {
        return "SsoToken{" +
                "id=" + id +
                ", token='" + token + '\'' +
                ", userId=" + userId +
                ", expiryTime=" + expiryTime +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", terminal=" + terminal +
                ", appVersion='" + appVersion + '\'' +
                ", appChannel='" + appChannel + '\'' +
                ", lastVisitIp='" + lastVisitIp + '\'' +
                ", platformVersion='" + platformVersion + '\'' +
                ", deviceModel='" + deviceModel + '\'' +
                ", deviceToken='" + deviceToken + '\'' +
                '}';
    }
}
