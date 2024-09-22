package kosta.gansikshop.controller;

import kosta.gansikshop.aop.PublicApi;
import kosta.gansikshop.aop.SecurityAspect;
import kosta.gansikshop.aop.TokenApi;
import kosta.gansikshop.dto.login.*;
import kosta.gansikshop.dto.member.MemberResponseDto;
import kosta.gansikshop.dto.member.MemberSingUpFormDto;
import kosta.gansikshop.dto.member.MemberUpdateFormDto;
import kosta.gansikshop.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/members")
public class MemberController {

    private final MemberService memberService;

    /** 회원가입 */
    @PublicApi
    @PostMapping
    public ResponseEntity<?> saveMember(@RequestBody MemberSingUpFormDto singUpFormDto) {
        memberService.saveMember(singUpFormDto);
        return ResponseEntity.status(HttpStatus.CREATED).body("회원가입에 성공했습니다.");
    }

    /** 회원수정 */
    @PutMapping
    public ResponseEntity<?> updateMember(@RequestBody MemberUpdateFormDto updateFormDto) {
        Long memberId = SecurityAspect.getCurrentMemberId();
        memberService.updateMember(memberId, updateFormDto);
        return ResponseEntity.status(HttpStatus.OK).body("회원 정보가 수정되었습니다.");
    }

    /** 회원정보 조회 */
    @GetMapping
    public ResponseEntity<?> getMember() {
        Long memberId = SecurityAspect.getCurrentMemberId();
        MemberResponseDto responseDto = memberService.getMember(memberId);
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    /** 회원탈퇴 */
    @DeleteMapping
    public ResponseEntity<?> deleteMember() {
        Long memberId = SecurityAspect.getCurrentMemberId();
        memberService.deleteMember(memberId);
        return ResponseEntity.status(HttpStatus.OK).body("회원 탈퇴하였습니다.");
    }

    /** 로그인 */
    @PublicApi
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginDto loginDto) {
        String tokens = memberService.login(loginDto);
        String[] splitTokens = tokens.split(":");
        return ResponseEntity.status(HttpStatus.OK).body(LoginResponseDto.createLoginResponseDto(
                "Bearer " + splitTokens[0], "Bearer " + splitTokens[1]));
    }

    /** 로그아웃 */
    @TokenApi
    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader("Authorization") String token, @RequestHeader("Refresh-Token") String refreshToken) {
        String jwtToken = token.replace("Bearer ", "");
        String jwtRefreshToken = refreshToken.replace("Bearer ", "");
        memberService.logout(jwtToken, jwtRefreshToken);
        return ResponseEntity.status(HttpStatus.OK).body("로그아웃에 성공했습니다.");
    }

    /** Access Token 재발급 */
    @TokenApi
    @PostMapping("/refresh-token")
    public ResponseEntity<?> refreshAccessToken(@RequestHeader("Refresh-Token") String refreshToken) {
        String jwtRefreshToken = refreshToken.replace("Bearer ", "");
        String newAccessToken = memberService.refreshAccessToken(jwtRefreshToken);
        return ResponseEntity.status(HttpStatus.OK).body(RefreshTokenResponse.createRefreshTokenResponse(
                "Bearer " + newAccessToken));
    }
}

