package kosta.gansikshop.aop;

import kosta.gansikshop.security.CustomUserDetails;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Aspect
@Component
public class LoggingAspect {

    private static final ThreadLocal<String> currentTransactionId = new ThreadLocal<>();
    private static final ThreadLocal<Integer> depth = ThreadLocal.withInitial(() -> 0);
    private static final ThreadLocal<StringBuilder> indent = ThreadLocal.withInitial(StringBuilder::new);

    @Around("execution(* kosta.gansikshop.service..*(..)) || execution(* kosta.gansikshop.controller..*(..)) || execution(* kosta.gansikshop.repository..*(..))")
    public Object logExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        String userEmail = getUserEmail();
        String transactionId = userEmail != null ? userEmail : "anonymous";
        currentTransactionId.set(transactionId);

        int currentDepth = depth.get();
        StringBuilder currentIndent = indent.get();
        StringBuilder newIndent = new StringBuilder(currentIndent);
        for (int i = 0; i < currentDepth; i++) {
            newIndent.append("   ");
        }
        indent.set(newIndent);

        long start = System.currentTimeMillis();
        String methodName = joinPoint.getSignature().toShortString();
        String logPrefix = currentIndent.toString();

        try {
            if (currentDepth == 0) {
                System.out.println("[" + LocalDateTime.now() + " " + transactionId + "] " + methodName + "()");
            } else {
                System.out.println("[" + LocalDateTime.now() + " " + transactionId + "] " + logPrefix + "|-->" + methodName + "()");
            }
            depth.set(currentDepth + 1);
            Object proceed = joinPoint.proceed();
            long executionTime = System.currentTimeMillis() - start;
            if (currentDepth == 0) {
                System.out.println("[" + LocalDateTime.now() + " " + transactionId + "] " + methodName + " time=" + executionTime + "ms");
            } else {
                System.out.println("[" + LocalDateTime.now() + " " + transactionId + "] " + logPrefix + "|<--" + methodName + " time=" + executionTime + "ms");
            }
            return proceed;
        } catch (Throwable ex) {
            long executionTime = System.currentTimeMillis() - start;
            if (currentDepth == 0) {
                System.out.println("[" + LocalDateTime.now() + " " + transactionId + "] " + methodName + " time=" + executionTime + "ms \n" +
                        "ex=" + ex.getClass().getName() + ": " + ex.getMessage());
            } else {
                System.out.println("[" + LocalDateTime.now() + " " + transactionId + "] " + logPrefix + "|<X-" + methodName + " time=" + executionTime + "ms \n" +
                        logPrefix + "ex=" + ex.getClass().getName() + ": " + ex.getMessage());
            }
            throw ex;
        } finally {
            depth.set(currentDepth - 1);
            indent.set(currentIndent);
        }
    }

    private String getUserEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof CustomUserDetails userDetails) {
            return userDetails.getUsername(); // 이메일 반환
        }
        return null;
    }
}