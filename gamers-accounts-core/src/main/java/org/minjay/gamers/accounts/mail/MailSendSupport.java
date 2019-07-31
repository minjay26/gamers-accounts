package org.minjay.gamers.accounts.mail;

import org.minjay.gamers.accounts.core.UserException;
import org.minjay.gamers.accounts.data.domain.User;
import org.minjay.gamers.accounts.data.repository.UserRepository;
import org.minjay.gamers.accounts.service.model.EmailVerificationCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.util.StringUtils;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.util.concurrent.TimeUnit;

public class MailSendSupport implements MailSender {

    private static final String TEMPLATE_PREFIX = "email.template.";

    public static final String TEMPLATE_BIND_EMAIL = "bindEmail";
    public static final String TEMPLATE_MODIFY_PASSWORD = "modifyPassword";

    public static final String VERIFICATION_CODE_KEY_PREFIX = "verification:";

    @Autowired
    private Environment env;
    @Autowired
    private JavaMailSender mailSender;
    @Autowired
    private ValueOperations<String, String> valueOperations;
    @Autowired
    private UserRepository userRepository;

    @Override
    public void send(EmailVerificationCode verificationCode, User user) {
        if (!verificationCode.getType().equals(TEMPLATE_BIND_EMAIL) &&
                !user.getEmail().equals(verificationCode.getEmail())) {
            throw new RuntimeException();
        }

        if (verificationCode.getType().equals(TEMPLATE_BIND_EMAIL)) {
            User found = userRepository.findByEmail(verificationCode.getEmail());
            if (found != null) {
                throw UserException.emailExistException();
            }
        }

        String verificationCodeKey = VERIFICATION_CODE_KEY_PREFIX + verificationCode.getType() + ":" + verificationCode.getEmail();
        String found = valueOperations.get(verificationCodeKey);
        if (!StringUtils.isEmpty(found)) {
            throw UserException.duplicateClaimException();
        }

        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage);
        String code = VerificationUtils.generate();
        try {
            messageHelper.setText(getTemplate(verificationCode.getType(), code), true);
            messageHelper.setSubject("gamers验证码");
            messageHelper.setFrom("minjay26@163.com");
            messageHelper.setTo(verificationCode.getEmail());
        } catch (MessagingException ex) {
        }
        mailSender.send(messageHelper.getMimeMessage());
        valueOperations.set(verificationCodeKey, code, 60, TimeUnit.MINUTES);
    }

    private String getTemplate(String templateKey, String code) {
        return StringUtils.replace(env.getProperty(TEMPLATE_PREFIX + templateKey), "code", code);
    }

}
