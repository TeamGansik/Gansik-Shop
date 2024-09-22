package kosta.gansikshop.service.email;

import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class AuthenticationService {
    private final Map<String, CodeWithTimestamp> verificationCodes = new ConcurrentHashMap<>();
    private static final long EXPIRATION_TIME = 3 * 60 * 1000; // 제한시간 3분

    // 인증 코드를 생성하고 저장
    public int generateVerificationCode(String email) {
        int verificationCode = new Random().nextInt(900000) + 100000;
        verificationCodes.put(email, new CodeWithTimestamp(verificationCode));
        return verificationCode;
    }

    // 인증 코드가 유효한지 확인
    public boolean verifyCode(String email, String inputCode) {
        CodeWithTimestamp storedCodeWithTimestamp = verificationCodes.get(email);

        // 저장된 코드가 없거나, 3분이 지났는지 확인
        if (storedCodeWithTimestamp == null || System.currentTimeMillis() - storedCodeWithTimestamp.timestamp() > EXPIRATION_TIME) {
            return false;
        }

        // 입력된 코드와 저장된 코드가 일치하는지 확인
        return storedCodeWithTimestamp.code() == Integer.parseInt(inputCode);
    }

    // 인증 코드 삭제
    public void clearCode(String email) {
        verificationCodes.remove(email);
    }
}
