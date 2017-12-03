package com.pf.gdtest;

/**
 * @author zhaopf
 * @version 1.0
 * @QQ 1308108803
 * @date 2017/12/3
 */
public interface ApplicationConfig {
    /**
     * 是否是调试模式,默认打开
     */
    boolean DEVELOP_DEBUG_MODE = true;
    /**
     * 服务器未捕捉异常信息是否显示
     */
    boolean IS_SERVER_ERR_TOAST = DEVELOP_DEBUG_MODE;
    /**
     * 默认的同步上传图片最大数量
     */
    int MAX_UPLOAD_DEFAULT = 3;
    /**
     * 压缩图片缓存路径
     */
    String PATH_CACHE_ZIP_IMGS = "/compress_imgs";
    /**
     * 文件压缩路径
     */
    String FOLDER_NAME = "/compress_imgs";
    /**
     * 图片默认压缩大小(kb)
     */
    int DEFAULT_MAX_SIZE = 256;
}