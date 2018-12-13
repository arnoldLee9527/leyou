package com.leyou.upload.service.impl;

import com.github.tobato.fastdfs.domain.StorePath;
import com.github.tobato.fastdfs.service.FastFileStorageClient;
import com.leyou.upload.service.UploadService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class UploadServiceImpl implements UploadService {

    //输出日志
   private static final Logger logger = LoggerFactory.getLogger(UploadServiceImpl.class);

    //限定支持的文件类型
    private final List<String> suffix = Arrays.asList("image/jpeg","image/png");

    @Autowired
    FastFileStorageClient fileStorageClient;


    @Override
    public String uploadImage(MultipartFile file) {
//        TODO
        try {
            //1、判断图片格式
            String fileSuffix = file.getContentType();
            if (!suffix.contains(fileSuffix)){
                //输出错误信息
                logger.info("图片格式不正确");
                return null;
            }

            //2、判断图片内容
            BufferedImage bufferedImage = ImageIO.read(file.getInputStream());
            if (bufferedImage == null){
                logger.info("文件内容不正确");
                return null;
            }

            //3、保存图片
            // 3.1、获取文件后缀名
            String extension = StringUtils.substringAfterLast(file.getOriginalFilename(), ".");

            StorePath storePath = this.fileStorageClient.uploadFile(file.getInputStream(), file.getSize(), extension, null);
            return "http://image.leyou.com/"+storePath.getFullPath();
        } catch (IOException e) {
            return null;
        }

    }
}
