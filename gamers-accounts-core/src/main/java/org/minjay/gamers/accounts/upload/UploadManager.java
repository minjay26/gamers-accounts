package org.minjay.gamers.accounts.upload;

import org.minjay.gamers.accounts.data.domain.User;
import org.springframework.web.multipart.MultipartFile;

public interface UploadManager {

    String upload(MultipartFile file, User user);
}
