package org.minjay.gamers.accounts.core;

public class UserException extends BasicErrorCodeException {

    private static final int VERIFICATION_INCORRECT_STATUS = 1001;
    private static final int VERIFICATION_DUPLICATE_CLAIM_STATUS = 1002;
    private static final int EMAIL_EXIST_STATUS = 1003;
    private static final int MODIFY_PASSWORD_STATUS = 1003;
    private static final int USERNAME_EXIST_STATUS = 1004;
    private static final int EMAIL_NOT_REGISTER = 1005;

    public UserException(int status) {
        super(status);
    }

    public static UserException verificationIncorrectException() {
        return new UserException(VERIFICATION_INCORRECT_STATUS);
    }

    public static UserException duplicateClaimException() {
        return new UserException(VERIFICATION_DUPLICATE_CLAIM_STATUS);
    }

    public static UserException emailExistException() {
        return new UserException(EMAIL_EXIST_STATUS);
    }

    public static UserException emailNotRegisterException() {
        return new UserException(EMAIL_NOT_REGISTER);
    }

    public static UserException modifyPasswordFailedException() {
        return new UserException(MODIFY_PASSWORD_STATUS);
    }

    public static UserException usernameExistException() {
        return new UserException(USERNAME_EXIST_STATUS);
    }
}
