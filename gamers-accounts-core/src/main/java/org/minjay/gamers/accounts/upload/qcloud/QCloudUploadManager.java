package org.minjay.gamers.accounts.upload.qcloud;

import com.qcloud.cos.COSClient;
import com.qcloud.cos.model.PutObjectRequest;
import org.minjay.gamers.accounts.data.domain.User;
import org.minjay.gamers.accounts.data.repository.UserRepository;
import org.minjay.gamers.accounts.upload.UploadManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

public class QCloudUploadManager implements UploadManager {
    public static final Logger LOGGER = LoggerFactory.getLogger(QCloudUploadManager.class);

    @Autowired
    private QCloudProperties properties;
    @Autowired
    private COSClient cosClient;
    @Autowired
    private UserRepository userRepository;

    @Transactional
    @Override
    public String upload(MultipartFile file, User user) {
        try {
            PutObjectRequest request = new PutObjectRequest(properties.getBucket(), file.getOriginalFilename(),
                    file.getInputStream(), null);
            cosClient.putObject(request);
            user.setHeadImage(properties.getDomain() + "/" + file.getOriginalFilename());
            userRepository.save(user);
        } catch (Exception ex) {
            LOGGER.error("upload head image to qcloud throw exception : {}", ex.getMessage());
        }
        return user.getHeadImage();
    }
}
