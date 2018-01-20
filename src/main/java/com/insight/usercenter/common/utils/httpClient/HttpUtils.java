package com.insight.usercenter.common.utils.httpClient;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * @author luwenbao
 * @date 2018/1/5.
 * @remark
 */
public class HttpUtils {

    /**
     * 根据url获取远程文件资源
     *
     * @param url 访问地址
     * @return byte数组
     */
    public static byte[] getByteFromUrl(String url) {
        ByteArrayOutputStream bos = null;
        BufferedInputStream bis = null;
        HttpURLConnection httpUrl = null;
        URL getUrl;
        byte[] data = null;
        try {
            byte[] buf = new byte[1024];
            getUrl = new URL(url);
            httpUrl = (HttpURLConnection) getUrl.openConnection();
            httpUrl.connect();
            bis = new BufferedInputStream(httpUrl.getInputStream());
            bos = new ByteArrayOutputStream();
            int size;
            while ((size = bis.read(buf)) != -1) {
                bos.write(buf, 0, size);
            }
            data = bos.toByteArray();
            bos.flush();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                bos.close();
                bis.close();
                httpUrl.disconnect();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
        }
        return data;
    }

}
