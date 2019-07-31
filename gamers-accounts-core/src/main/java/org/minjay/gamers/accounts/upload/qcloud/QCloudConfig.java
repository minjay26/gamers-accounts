package org.minjay.gamers.accounts.upload.qcloud;

import com.qcloud.cos.COSClient;
import com.qcloud.cos.ClientConfig;
import com.qcloud.cos.auth.BasicCOSCredentials;
import com.qcloud.cos.auth.COSCredentials;
import com.qcloud.cos.region.Region;
import org.minjay.gamers.accounts.upload.UploadManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class QCloudConfig {

    @Bean
    public QCloudProperties qCloudProperties() {
        return new QCloudProperties();
    }

    @Bean
    public COSClient cosClient(QCloudProperties qCloudProperties) {
        COSCredentials cred = new BasicCOSCredentials(qCloudProperties.getSecretId(), qCloudProperties.getSecretKey());
        ClientConfig clientConfig = new ClientConfig(new Region("ap-shanghai"));
        return new COSClient(cred, clientConfig);
    }

    @Bean
    public UploadManager uploadManager() {
        return new QCloudUploadManager();
    }
}
