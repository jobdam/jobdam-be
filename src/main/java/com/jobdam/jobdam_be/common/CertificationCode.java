package com.jobdam.jobdam_be.common;

public class CertificationCode {

    static int length = 6;

    /**
     * 랜덤한 인증번호 6자리 반환하는 함수
     *
     * @return 랜덤한 인증번호 6자리 반환
     */
    public static String getCertificationCode() {
        StringBuilder certificationNumber = new StringBuilder();
        for (int i = 0; i < length; i++) {
            certificationNumber.append((int) (Math.random() * 10));
        }
        return certificationNumber.toString();
    }
}
