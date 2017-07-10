package com.meerkat.api.util;

import com.google.gson.Gson;
import com.qiniu.common.QiniuException;
import com.qiniu.common.Zone;
import com.qiniu.http.Response;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.UploadManager;
import com.qiniu.storage.model.DefaultPutRet;
import com.qiniu.util.Auth;

import javax.imageio.ImageIO;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Iterator;
import java.util.Properties;

/**
 * Created by Friedemann Lee on 2017-04-05 21:16.
 */
public class ImageUtil {
    private static String ak = null;
    private static String sk = null;
    private static String foodBucket = null;
    private static String avatarBucket = null;
    private static String foodDomain = null;
    private static String avatarDomain = null;

    public ImageUtil() {
        if (ak != null)return;
        Properties prop = new Properties();
        InputStream in = this.getClass().getResourceAsStream("/qiniu.properties");
        try {
            prop.load(in);
            ak = prop.getProperty("AccessKey").trim();
            sk = prop.getProperty("SecretKey").trim();
            foodBucket = prop.getProperty("FoodBucket").trim();
            avatarBucket = prop.getProperty("AvatarBucket").trim();
            foodDomain = prop.getProperty("FoodDomain").trim();
            avatarDomain = prop.getProperty("AvatarDomain").trim();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 图片裁剪并上传到七牛
     * @param inputStream 图上输入流
     * @param mineType 图片mine,如 "image/png"
     * @param x 裁剪起点x坐标
     * @param y 裁剪起点y坐标
     * @param width 裁剪宽度
     * @param height 裁剪调试
     * @return
     * @throws IOException
     */
    public String imageCut(InputStream inputStream, String mineType, int x, int y, int width, int height) throws IOException {
        FileInputStream is = null;
        ImageInputStream iis = null;
        String outPath = "E:/out/a.jpg";

        BufferedInputStream bis = null;
        try {
            if (x <= 0) x = 266;
            if (y <= 0) y = 50;
            if (width <= 0) width = 300;
            if (height <= 0) height = 300;
            bis = new BufferedInputStream(inputStream);

//            is = new FileInputStream(imagePath);
            /**
             * 返回包含所有当前已注册 ImageReader 的 Iterator，这些 ImageReader
             * 声称能够解码指定格式。 参数：formatName - 包含非正式格式名称 .
             * (例如 "jpeg" 或 "tiff")等 。
             */
//            Iterator<ImageReader> it = ImageIO.getImageReadersByFormatName("png");
            Iterator<ImageReader> it = ImageIO.getImageReadersByMIMEType(mineType);
            ImageReader reader = it.next();
            // 获取图片流
//            iis = ImageIO.createImageInputStream(is);
            iis = ImageIO.createImageInputStream(bis);
            /**
             * <p>
             * iis:读取源。true:只向前搜索
             * </p>
             * .将它标记为 ‘只向前搜索’。
             * 此设置意味着包含在输入源中的图像将只按顺序读取，可能允许 reader
             * 避免缓存包含与以前已经读取的图像关联的数据的那些输入部分。
             */
            reader.setInput(iis, true);
            /**
             * <p>
             * 描述如何对流进行解码的类
             * <p>
             * .用于指定如何在输入时从 Java Image I/O
             * 框架的上下文中的流转换一幅图像或一组图像。用于特定图像格式的插件
             * 将从其 ImageReader 实现的 getDefaultReadParam 方法中返回
             * ImageReadParam 的实例。
             */
            ImageReadParam param = reader.getDefaultReadParam();
            /**
             * 图片裁剪区域。Rectangle 指定了坐标空间中的一个区域，通过 Rectangle 对象
             * 的左上顶点的坐标(x，y)、宽度和高度可以定义这个区域。
             */
            Rectangle rect = new Rectangle(x, y, width, height);
            // 提供一个 BufferedImage，将其用作解码像素数据的目标。
            param.setSourceRegion(rect);
            /**
             * 使用所提供的 ImageReadParam 读取通过索引 imageIndex 指定的对象，并将
             * 它作为一个完整的 BufferedImage 返回。
             */
            BufferedImage bi = reader.read(0, param);
            // 保存新图片
//            ImageIO.write(bi, "jpg", new File(outPath));
            //将图片上传到七牛云
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            ImageIO.write(bi, "jpg", byteArrayOutputStream);
            byte[] bytes = byteArrayOutputStream.toByteArray();
            String url = uploadQiNiu(bytes);
            return url;
        } catch (IOException e) {
            e.printStackTrace();
            throw e;
        } finally {
            if (is != null) is.close();
            if (iis != null) iis.close();
            if (bis != null) bis.close();
        }
    }

    /**
     * 获取图片访问token，默认有效时间为60分钟
     * @param url
     * @return
     */
    public String getDownloadToken(String url) {
        Auth auth = Auth.create(ak, sk);
        String token = auth.privateDownloadUrl(url);
        return token;
    }

    /**
     * 上传到七牛云，现默认上传到华南区的foodBucket空间上
     *
     * @param bytes
     * @return
     * @throws IOException
     */
    private String uploadQiNiu(byte[] bytes) throws IOException {
        Configuration cfg = new Configuration(Zone.zone2());
        UploadManager uploadManager = new UploadManager(cfg);
        Auth auth = Auth.create(ak, sk);
        String upToken = auth.uploadToken(foodBucket);
        String key = null;
        String url = foodDomain + "/";
        try {
            Response response = uploadManager.put(bytes, key, upToken);
            //解析上传成功的结果
            DefaultPutRet putRet = new Gson().fromJson(response.bodyString(), DefaultPutRet.class);
            url += putRet.key;
        } catch (QiniuException ex) {
            ex.printStackTrace();
            throw new IOException(ex.getMessage());
        }
        return url;
    }

}
