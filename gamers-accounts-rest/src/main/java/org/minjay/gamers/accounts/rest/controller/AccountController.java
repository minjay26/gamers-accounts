package org.minjay.gamers.accounts.rest.controller;

import org.minjay.gamers.accounts.data.domain.User;
import org.minjay.gamers.accounts.mail.MailSendSupport;
import org.minjay.gamers.accounts.mail.MailSender;
import org.minjay.gamers.accounts.rest.bind.annotation.CurrentUser;
import org.minjay.gamers.accounts.service.UserService;
import org.minjay.gamers.accounts.service.model.EmailVerificationCode;
import org.minjay.gamers.accounts.service.model.ModifyPasswordDto;
import org.minjay.gamers.accounts.service.model.UserRegisterDto;
import org.minjay.gamers.accounts.upload.UploadManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/accounts")
public class AccountController {

    @Autowired
    public MailSender mailSender;
    @Autowired
    public UserService userService;
    @Autowired
    public UploadManager uploadManager;

    @PutMapping("/verification_code_apply")
    public ResponseEntity<Void> sendVerificationCode(@Validated @RequestBody EmailVerificationCode verificationCode,
                                                     final @CurrentUser User user) {

        mailSender.send(verificationCode, user);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/register")
    public ResponseEntity<Void> register(@RequestBody @Validated UserRegisterDto dto) {
        userService.register(dto);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/modify_password_apply")
    public ResponseEntity<Void> modifyPasswordApply(@Validated @RequestBody EmailVerificationCode verificationCode,
                                                    final @CurrentUser User user) {
        verificationCode.setType(MailSendSupport.TEMPLATE_MODIFY_PASSWORD);
        userService.modifyPasswordApply(user, verificationCode);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/modify_password")
    public ResponseEntity<Void> modifyPassword(@Validated @RequestBody ModifyPasswordDto dto,
                                               final @CurrentUser User user) {
        userService.modifyPassword(user, dto);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/upload_head_image")
    public ResponseEntity<String> uploadHeadImage(@RequestParam("file") MultipartFile file,
                                                  final @CurrentUser User user) {
        String url = uploadManager.upload(file);
        user.setHeadImage(url);
        userService.save(user);
        return ResponseEntity.ok(url);
    }
}
