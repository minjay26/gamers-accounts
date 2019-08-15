package org.minjay.gamers.accounts.upload.qcloud;

import com.qcloud.cos.COSClient;
import com.qcloud.cos.model.PutObjectRequest;
import org.minjay.gamers.accounts.upload.UploadManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

public class QCloudUploadManager implements UploadManager {
    public static final Logger LOGGER = LoggerFactory.getLogger(QCloudUploadManager.class);

    @Autowired
    private QCloudProperties properties;
    @Autowired
    private COSClient cosClient;

    @Override
    public String upload(MultipartFile file) {
        String key = UUID.randomUUID().toString() + ".png";
        try {
            PutObjectRequest request = new PutObjectRequest(properties.getBucket(), key,
                    file.getInputStream(), null);
            cosClient.putObject(request);
        } catch (Exception ex) {
            LOGGER.error("upload head image to qcloud throw exception : {}", ex.getMessage());
        }
        return properties.getDomain() + "/" + key;
    }

}
