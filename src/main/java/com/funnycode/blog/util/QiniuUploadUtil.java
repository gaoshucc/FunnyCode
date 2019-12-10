package com.funnycode.blog.util;

import com.google.gson.Gson;
import com.qiniu.common.QiniuException;
import com.qiniu.common.Zone;
import com.qiniu.http.Response;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.UploadManager;
import com.qiniu.storage.model.DefaultPutRet;
import com.qiniu.util.Auth;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

/**
 * @author CC
 * @date 2019-09-20 00:49
 */
public class QiniuUploadUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(QiniuUploadUtil.class);

    /**
     * 简单上传
     */
    private static String getUploadToken(){
        Auth auth = Auth.create(PropertiesUtil.getProperty("qiniu.accessKey"), PropertiesUtil.getProperty("qiniu.secretKey"));
        return auth.uploadToken(PropertiesUtil.getProperty("qiniu.bucket"));
    }

    /**
     * 覆盖上传
     */
    private static String getCoverUploadToken(String fileName){
        Auth auth = Auth.create(PropertiesUtil.getProperty("qiniu.accessKey"), PropertiesUtil.getProperty("qiniu.secretKey"));
        return auth.uploadToken(PropertiesUtil.getProperty("qiniu.bucket"), fileName);
    }

    public static String uploadPic(String filePath,String fileName) throws QiniuException {
        String upToken = getUploadToken();
        Configuration cfg = new Configuration(Zone.huanan());
        UploadManager uploadManager = new UploadManager(cfg);
        try {
            Response response = uploadManager.put(filePath, fileName, upToken);
            //解析上传成功的结果
            DefaultPutRet putRet = new Gson().fromJson(response.bodyString(), DefaultPutRet.class);

            return String.format("%s/%s", PropertiesUtil.getProperty("qiniu.domain"), putRet.key);
        }catch (QiniuException ex){
            Response r = ex.response;
            LOGGER.error(r.toString());
            LOGGER.error(r.bodyString());
        }
        return null;
    }

    public static String uploadFile(MultipartFile file, String filename) throws Exception {
        //默认不指定key的情况下，以文件内容的hash值作为文件名
        String upToken = getUploadToken();
        Configuration cfg = new Configuration(Zone.huanan());
        UploadManager uploadManager = new UploadManager(cfg);
        try {
            InputStream inputStream=file.getInputStream();
            ByteArrayOutputStream swapStream = new ByteArrayOutputStream();
            byte[] buff = new byte[600];
            int rc = 0;
            while ((rc = inputStream.read(buff, 0, 100)) > 0) {
                swapStream.write(buff, 0, rc);
            }

            byte[] uploadBytes  = swapStream.toByteArray();
            try {
                Response response = uploadManager.put(uploadBytes, filename, upToken);
                //解析上传成功的结果
                DefaultPutRet putRet;
                putRet = new Gson().fromJson(response.bodyString(), DefaultPutRet.class);
                return String.format("%s/%s", PropertiesUtil.getProperty("qiniu.domain"), putRet.key);

            } catch (QiniuException ex) {
                Response r = ex.response;
                LOGGER.error(r.toString());
                LOGGER.error(r.bodyString());
            }
        } catch (UnsupportedEncodingException ex) {
            LOGGER.error("上传图片异常={}", ex.getMessage());
        }
        return null;
    }
}
