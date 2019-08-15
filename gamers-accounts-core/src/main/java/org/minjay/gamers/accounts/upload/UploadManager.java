package org.minjay.gamers.accounts.upload;

import org.springframework.web.multipart.MultipartFile;

public interface UploadManager {

    String upload(MultipartFile file);
}
