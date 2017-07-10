package com.meerkat.base.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by wm on 17/4/18.
 */
public class UAUtil {


    public static final String OSTYPE_ANDROID = "Android";
    public static final String OSTYPE_IOS = "Ios";
    public static final String OSTYPE_WP = "WINDOWS PHONE";
    public static final String OSTYPE_BLACKBERRY = "BLACKBERRY";
    /***
     * pad
     */
    public static final String DEVICE_TYPE_PAD = "Pad";
    /***
     * 手机
     */
    public static final String DEVICE_TYPE_PHONE = "Phone";

    public static void main(String[] args) throws Exception {
        ClientOsInfo info = UAUtil.getMobilOS("Mozilla/5.0 (Linux; Android 6.0.1; Redmi Note 3 Build/MMB29M; wv) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/54.0.2840.85 Mobile Safari/537.36");
        System.out.println(info.getPlatformVersion());
        System.out.println(info.getDeviceModel());
    }

    public static String getModel(String userAgent) {
        String[] split = userAgent.split(";");
        for(String build : split){
            if(build.indexOf("Build")!=-1){
                if(build.indexOf(")")!=-1){
                    build = build.substring(0, build.indexOf(")"));
                }
                if(build.length()>30){
                    build = build.substring(30);
                }
                return build.trim();
            }
        }
        return null;
    }

    /***
     * 校验渠道终端版本号是否合法,eg:0.0.0.3
     *
     * @param clientVersion
     * @return true-->合法 ;false-->非法
     */
    public static boolean verifyClientVersion(String clientVersion) {
        boolean result = Pattern.matches("[\\d\\.]+", clientVersion);
        if (result) {
            result = Pattern.matches("^\\d\\.\\d\\.\\d\\.\\d$", clientVersion);
            return result;
        } else {
            return false;
        }
    }

    /**
     * 根据useragent和手机厂商查手机型号
     *
     * @param UA
     * @return
     */
    public static String getMobModel(String UA, String operatingSystem) {
        if (UA == null) {
            return null;
        }
        // 存放正则表达式
        String rex = "";
        // 苹果产品
        if (operatingSystem.indexOf("IOS") != -1) {
            if (UA.indexOf("IPAD") != -1) {// 判断是否为ipad
                return "IPAD";
            }
            if (UA.indexOf("IPOD") != -1) {// 判断是否为ipod
                return "IPOD";
            }
            if (UA.indexOf("IPONE") != -1) {// 判断是否为ipone
                return "IPONE";
            }
            return "IOS DEVICE";

        }
        // 安卓系统产品
        if (operatingSystem.indexOf("ANDROID") != -1) {
            String re = "BUILD";
            rex = ".*" + ";" + "(.*)" + re;
            Pattern p = Pattern.compile(rex, Pattern.CASE_INSENSITIVE);
            Matcher m = p.matcher(UA);
            boolean rs = m.find();
            if (rs) {
//                System.out.println("Mobil Model is" + m.group(1));
                return m.group(1);
            }
        }
        return null;
    }

    /**
     * 判断手机的操作系统 IOS/android/windows phone/BlackBerry
     *
     * @param UA
     * @return
     */
    public static ClientOsInfo getMobilOS(String UA) {
        if(StringUtil.isBlank(UA)){
            return null;
        }
        String sourceUA = UA;
        UA = UA.toUpperCase();
        ClientOsInfo osInfo = new ClientOsInfo();
        // 存放正则表达式
        String rex = "";
        // IOS 判断字符串
        String iosString = " LIKE MAC OS X";
        if (UA.indexOf(iosString) != -1) {
            if (isMatch(UA, "\\([\\s]*iPhone[\\s]*;", Pattern.CASE_INSENSITIVE)) {
                osInfo.setDeviceType(DEVICE_TYPE_PHONE);
            } else if (isMatch(UA, "\\([\\s]*iPad[\\s]*;", Pattern.CASE_INSENSITIVE)) {
                osInfo.setDeviceType(DEVICE_TYPE_PAD);
            }
            rex = ".*" + "[\\s]+(\\d[_\\d]*)" + iosString;
            Pattern p = Pattern.compile(rex, Pattern.CASE_INSENSITIVE);
            Matcher m = p.matcher(UA);
            boolean rs = m.find();
            if (rs) {
                String osVersion = m.group(1).replace("_", ".");
                osInfo.setVersion(osVersion);
//              System.out.println("Mobil OS is" + " IOS" +osVersion);
                osInfo.setPlatformVersion(OSTYPE_IOS + "_" + osVersion);
            } else {
//                System.out.println("IOS");
                osInfo.setPlatformVersion(OSTYPE_IOS);
            }
            osInfo.setOsType(OSTYPE_IOS);
            return osInfo;
        }
        // Android 判断
        String androidString = "ANDROID";
        if (UA.indexOf(androidString) != -1) {
            if (isMatch(UA, "\\bMobi", Pattern.CASE_INSENSITIVE)) {
                osInfo.setDeviceType(DEVICE_TYPE_PHONE);
            } else {
                osInfo.setDeviceType(DEVICE_TYPE_PAD);
            }
            rex = ".*" + androidString + "[\\s]*(\\d*[\\._\\d]*)";
            Pattern p = Pattern.compile(rex, Pattern.CASE_INSENSITIVE);
            Matcher m = p.matcher(UA);
            boolean rs = m.find();
            if (rs) {
                String version = m.group(1).replace("_", ".");
                osInfo.setVersion(version);
//                System.out.println("Mobil OS is " + OSTYPE_ANDROID + version);
                osInfo.setPlatformVersion(OSTYPE_ANDROID + "_" + version);
            } else {
//                System.out.println("Android");
                osInfo.setPlatformVersion(OSTYPE_ANDROID);
            }
            osInfo.setOsType(OSTYPE_ANDROID);
            if (sourceUA.indexOf("Build") != -1) {
                osInfo.setDeviceModel(getModel(sourceUA));
            }
            return osInfo;
        }
        // windows phone 判断
        String wpString = "WINDOWS PHONE";
        if (UA.indexOf(wpString) != -1) {
            rex = ".*" + wpString + "[\\s]*[OS\\s]*([\\d][\\.\\d]*)";
            Pattern p = Pattern.compile(rex, Pattern.CASE_INSENSITIVE);
            Matcher m = p.matcher(UA);
            boolean rs = m.find();
            if (rs) {
//                System.out.println("Mobil OS is " + OSTYPE_WP + m.group(1));
                String version = m.group(1);
                osInfo.setVersion(version);
                osInfo.setPlatformVersion(OSTYPE_WP + "_" + version);
            } else {
//                System.out.println("WINDOWS PHONE");
                osInfo.setPlatformVersion(OSTYPE_WP);
            }
            osInfo.setOsType(OSTYPE_WP);
            return osInfo;
        }
        // BlackBerry 黑莓系统判断
        String bbString = "BLACKBERRY";
        if (UA.indexOf(bbString) != -1) {
            rex = ".*" + bbString + "[\\s]*([\\d]*)";
            Pattern p = Pattern.compile(rex, Pattern.CASE_INSENSITIVE);
            Matcher m = p.matcher(UA);
            boolean rs = m.find();
            if (rs) {
//                System.out.println("Mobil OS is" + " BLACKBERRY " + m.group(1));
                String version = m.group(1);
                osInfo.setVersion(version);
                osInfo.setPlatformVersion(OSTYPE_BLACKBERRY + "_" + version);
            } else {
//                System.out.println("BLACKBERRY");
                osInfo.setPlatformVersion(OSTYPE_BLACKBERRY);
            }
            osInfo.setOsType(OSTYPE_BLACKBERRY);
            return osInfo;
        }
        if (UA.contains("LINUX")) {//android
            if (isMatch(UA, "\\bMobi", Pattern.CASE_INSENSITIVE)) {
                osInfo.setDeviceType(DEVICE_TYPE_PHONE);
            } else {
                osInfo.setDeviceType(DEVICE_TYPE_PAD);
            }

            Pattern p = Pattern.compile("U;\\s*(Adr[\\s]*)?(\\d[\\.\\d]*\\d)[\\s]*;", Pattern.CASE_INSENSITIVE);
            Matcher m = p.matcher(UA);
            boolean result = m.find();
            String find_result = null;
            if (result) {
                find_result = m.group(2);
            }
            if (find_result == "" || find_result == null) {
                osInfo.setPlatformVersion(OSTYPE_ANDROID);
                return osInfo;
            } else {
                osInfo.setVersion(find_result);
                osInfo.setPlatformVersion(OSTYPE_ANDROID + "_" + find_result);
                return osInfo;
            }
        }

        //UCWEB/2.0 (iOS; U; iPh OS 4_3_2; zh-CN; iPh4)
        if (UA.matches(".*((IOS)|(iPAD)).*(IPH).*")) {
            if (isMatch(UA, "[\\s]*iPh[\\s]*", Pattern.CASE_INSENSITIVE)) {
                osInfo.setDeviceType(DEVICE_TYPE_PHONE);
            } else {
                osInfo.setDeviceType(DEVICE_TYPE_PAD);
            }
            Pattern p = Pattern.compile("U;\\s*(IPH[\\s]*)?(OS[\\s]*)?(\\d[\\._\\d]*\\d)[\\s]*;", Pattern.CASE_INSENSITIVE);
            Matcher m = p.matcher(UA);
            boolean result = m.find();
            String find_result = null;
            if (result) {
                find_result = m.group(3);
            }
            if (find_result == "" || find_result == null) {
                osInfo.setPlatformVersion(OSTYPE_IOS);
                osInfo.setOsType(OSTYPE_IOS);
                return osInfo;
            } else {
                String version = find_result.replace("_", ".");
                osInfo.setVersion(version);
                osInfo.setPlatformVersion(OSTYPE_IOS + "_" + version);
                osInfo.setOsType(OSTYPE_IOS);
                return osInfo;
            }
        }
        return osInfo;
    }

    public static boolean isMatch(String source, String regx, int flags) {
        Pattern p = Pattern.compile(regx, flags);
        Matcher m = p.matcher(source);
        boolean result = m.find();
        return result;
    }
}
