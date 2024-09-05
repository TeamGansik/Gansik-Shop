package kosta.gansikshop.controller;

import kosta.gansikshop.config.security.CustomUserDetails;
import kosta.gansikshop.dto.login.*;
import kosta.gansikshop.dto.member.MemberResponseDto;
import kosta.gansikshop.dto.member.MemberSingUpFormDto;
import kosta.gansikshop.dto.member.MemberUpdateFormDto;
import kosta.gansikshop.service.EntityValidationService;
import kosta.gansikshop.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/members")
public class MemberController {

    private final MemberService memberService;
    private final EntityValidationService entityValidationService;

    /** 회원가입 */
    @PostMapping
    public ResponseEntity<?> saveMember(@RequestBody MemberSingUpFormDto singUpFormDto) {
        try {
            memberService.saveMember(singUpFormDto);
            return ResponseEntity.status(HttpStatus.CREATED).body("회원가입에 성공했습니다.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    /** 회원수정 */
    @PutMapping
    public ResponseEntity<?> updateMember(@RequestBody MemberUpdateFormDto updateFormDto) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            String userEmail = userDetails.getUsername();
            Long memberId = entityValidationService.validateMemberByEmail(userEmail).getId(); // 이메일로 회원 ID 조회

            memberService.updateMember(memberId, updateFormDto);
            return ResponseEntity.status(HttpStatus.OK).body("회원 정보가 수정되었습니다.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    /** 회원정보 조회 */
    @GetMapping
    public ResponseEntity<?> getMember() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            String userEmail = userDetails.getUsername();
            Long memberId = entityValidationService.validateMemberByEmail(userEmail).getId(); // 이메일로 회원 ID 조회

            MemberResponseDto responseDto = memberService.getMember(memberId);
            return ResponseEntity.status(HttpStatus.OK).body(responseDto);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    /** 회원탈퇴 */
    @DeleteMapping
    public ResponseEntity<?> deleteMember() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            String userEmail = userDetails.getUsername();
            Long memberId = entityValidationService.validateMemberByEmail(userEmail).getId(); // 이메일로 회원 ID 조회

            memberService.deleteMember(memberId);
            return ResponseEntity.status(HttpStatus.OK).body("회원 탈퇴하였습니다.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    /** 로그인 */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginDto loginDto) {
        try {
            String tokens = memberService.login(loginDto);
            String[] splitTokens = tokens.split(":");
            return ResponseEntity.status(HttpStatus.OK).body(LoginResponseDto.createLoginResponseDto(
                    "Bearer " + splitTokens[0],
                            "Bearer " + splitTokens[1]));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    /** 로그아웃 */
    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader("Authorization") String token, @RequestHeader("Refresh-Token") String refreshToken) {
        try {
            String jwtToken = token.replace("Bearer ", "");
            String jwtRefreshToken = refreshToken.replace("Bearer ", "");
            memberService.logout(jwtToken, jwtRefreshToken);
            return ResponseEntity.status(HttpStatus.OK).body("로그아웃 하였습니다.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("서버 오류가 발생했습니다." + e.getMessage());
        }
    }

    /** Access Token 재발급 */
    @PostMapping("/refresh-token")
    public ResponseEntity<?> refreshAccessToken(@RequestHeader("Refresh-Token") String refreshToken) {
        try {
            // Bearer 제거 로직 추가
            String jwtRefreshToken = refreshToken.replace("Bearer ", "");
            System.out.println("Extracted Refresh Token: " + jwtRefreshToken);
            String newAccessToken = memberService.refreshAccessToken(jwtRefreshToken);

            return ResponseEntity.status(HttpStatus.OK).body(RefreshTokenResponse.builder()
                    .newToken("Bearer "+ newAccessToken)
                    .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("서버 오류가 발생했습니다." + e.getMessage());
        }
    }
}
