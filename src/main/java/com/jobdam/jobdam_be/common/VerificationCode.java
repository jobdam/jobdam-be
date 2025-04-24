package com.jobdam.jobdam_be.common;

public class VerificationCode {

    static int length = 6;

    /**
     * 랜덤한 인증번호 6자리 반환하는 함수
     *
     * @return 랜덤한 인증번호 6자리 반환
     */
    public static String getVerificationCode() {
        StringBuilder verificationNumber = new StringBuilder();
        for (int i = 0; i < length; i++) {
            verificationNumber.append((int) (Math.random() * 10));
        }
        return verificationNumber.toString();
    }
}
