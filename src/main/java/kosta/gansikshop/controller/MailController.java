package kosta.gansikshop.controller;

import kosta.gansikshop.service.email.AuthenticationService;
import kosta.gansikshop.service.email.MailService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/mail")
public class MailController {

    private final MailService mailService;
    private final AuthenticationService authenticationService;

    @PostMapping
    public ResponseEntity<Map<String, Object>> mailSend(@RequestBody Map<String, String> request) {
        Map<String, Object> map = new HashMap<>();
        String email = request.get("email");

        if (email == null || email.isEmpty()) {
            throw new IllegalArgumentException("이메일 주소가 필요합니다.");
        }

        mailService.sendMail(email);
        map.put("success", Boolean.TRUE);
        return ResponseEntity.ok(map);
    }


    /** 인증 번호 확인 */
    @PostMapping("/check")
    public ResponseEntity<?> mailCheck(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        String userNumber = request.get("userNumber");

        boolean isMatch = authenticationService.verifyCode(email, userNumber);

        if (isMatch) {
            authenticationService.clearCode(email);
        }

        return ResponseEntity.ok(isMatch);
    }
}
