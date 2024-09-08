package kosta.gansikshop.service;

import kosta.gansikshop.domain.Address;
import kosta.gansikshop.domain.Member;
import kosta.gansikshop.dto.login.LoginDto;
import kosta.gansikshop.dto.member.MemberResponseDto;
import kosta.gansikshop.dto.member.MemberSingUpFormDto;
import kosta.gansikshop.dto.member.MemberUpdateFormDto;
import kosta.gansikshop.repository.member.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final EntityValidationService entityValidationService;
    private final TokenService tokenService;

    /** 회원 가입 */
    @Transactional
    public void saveMember(MemberSingUpFormDto memberSingUpFormDto) {
        if (entityValidationService.existEmail(memberSingUpFormDto.getEmail())) {
            throw new IllegalArgumentException("이미 가입된 이메일이 존재합니다.");
        }

        Address address = Address.createAddress(
                memberSingUpFormDto.getPostcode(),
                memberSingUpFormDto.getRoadAddress(),
                memberSingUpFormDto.getDetailAddress()
        );

        Member member = Member.createMember(
                memberSingUpFormDto.getName(),
                memberSingUpFormDto.getEmail(),
                bCryptPasswordEncoder.encode(memberSingUpFormDto.getPassword()),
                memberSingUpFormDto.getPhone(),
                address
        );

        memberRepository.save(member);
    }

    /** 회원 수정 */
    @Transactional
    public void updateMember(Long memberId, MemberUpdateFormDto memberUpdateFormDto) {
        Member findMember = entityValidationService.validateMember(memberId);

        boolean isSameName = findMember.getName().equals(memberUpdateFormDto.getName());
        boolean isSamePassword = findMember.getPassword().equals(bCryptPasswordEncoder.encode(memberUpdateFormDto.getPassword()));
        boolean isSamePhone = findMember.getPhone().equals(memberUpdateFormDto.getPhone());

        Address currentAddress = findMember.getAddress();
        boolean isSameAddress = currentAddress.getPostcode().equals(memberUpdateFormDto.getPostcode()) &&
                currentAddress.getRoadAddress().equals(memberUpdateFormDto.getRoadAddress()) &&
                currentAddress.getDetailAddress().equals(memberUpdateFormDto.getDetailAddress());

        // 모든 필드가 동일한 경우 예외 처리
        if (isSameName && isSamePassword && isSamePhone && isSameAddress) {
            throw new IllegalArgumentException("수정된 정보가 없습니다.");
        }

        Address newAddress = Address.createAddress(
                memberUpdateFormDto.getPostcode(),
                memberUpdateFormDto.getRoadAddress(),
                memberUpdateFormDto.getDetailAddress()
        );

        findMember.updateMember(memberUpdateFormDto.getName(),
                bCryptPasswordEncoder.encode(memberUpdateFormDto.getPassword()),
                memberUpdateFormDto.getPhone(),
                newAddress);
    }

    /** 회원 조회 */
    @Transactional(readOnly = true)
    public MemberResponseDto getMember(Long memberId) {
        Member findMember = entityValidationService.validateMember(memberId);

        return MemberResponseDto.createMemberResponseDto(
                findMember.getEmail(),
                findMember.getName(),
                findMember.getPhone(),
                findMember.getAddress().getPostcode(),
                findMember.getAddress().getRoadAddress(),
                findMember.getAddress().getDetailAddress(),
                findMember.getRole().toString()
        );
    }

    /** 회원 탈퇴 */
    @Transactional
    public void deleteMember(Long memberId) {
        Member findMember = entityValidationService.validateMember(memberId);

        memberRepository.delete(findMember);
    }

    /** 로그인 */
    @Transactional(readOnly = true)
    public String login(LoginDto loginDto) {
        Member member = entityValidationService.validateMemberByEmail(loginDto.getEmail());

        if (!bCryptPasswordEncoder.matches(loginDto.getPassword(), member.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        String accessToken = tokenService.generateAccessToken(member.getEmail());
        String refreshToken = tokenService.generateRefreshToken(member.getEmail());

        return accessToken + ":" + refreshToken;
    }

    /** 로그아웃 */
    @Transactional
    public void logout(String token, String refreshToken) {
        tokenService.invalidateToken(token);
        tokenService.invalidateToken(refreshToken);
    }

    /** Access Token 재발급 */
    @Transactional(readOnly = true)
    public String refreshAccessToken(String refreshToken) {
        return tokenService.refreshAccessToken(refreshToken);
    }
}
