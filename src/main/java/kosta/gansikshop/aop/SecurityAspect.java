package kosta.gansikshop.aop;

import kosta.gansikshop.security.CustomUserDetails;
import kosta.gansikshop.service.EntityValidationService;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.core.annotation.Order;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Aspect
@Component
@RequiredArgsConstructor
@Order(1)
public class SecurityAspect {

    private final EntityValidationService entityValidationService;
    private static final ThreadLocal<Long> memberIdHolder = new ThreadLocal<>();

    @Around("execution(* kosta.gansikshop.controller.*.*(..)) && " +
            "!@annotation(kosta.gansikshop.aop.PublicApi) && " +
            "!@annotation(kosta.gansikshop.aop.MailApi) && " +
            "!@annotation(kosta.gansikshop.aop.TokenApi)")
    public Object injectMemberId(ProceedingJoinPoint joinPoint) throws Throwable {
        System.out.println("AOP 메서드 실행 중...");

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new AccessDeniedException("인증이 필요합니다.");
        }

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        String userEmail = userDetails.getUsername();
        System.out.println("userEmail = " + userEmail);

        Long memberId = entityValidationService.validateMemberByEmail(userEmail).getId();
        System.out.println("memberId = " + memberId);

        memberIdHolder.set(memberId);

        try {
            return joinPoint.proceed();
        } finally {
            memberIdHolder.remove();
        }
    }

    public static Long getCurrentMemberId() {
        return memberIdHolder.get();
    }
}
