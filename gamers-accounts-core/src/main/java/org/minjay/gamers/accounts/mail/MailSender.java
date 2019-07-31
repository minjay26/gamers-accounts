package org.minjay.gamers.accounts.mail;

import org.minjay.gamers.accounts.data.domain.User;
import org.minjay.gamers.accounts.service.model.EmailVerificationCode;

public interface MailSender {

    void send(EmailVerificationCode verificationCode, User user);
}
