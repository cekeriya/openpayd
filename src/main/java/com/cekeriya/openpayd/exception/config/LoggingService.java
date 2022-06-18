package com.cekeriya.openpayd.exception.config;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.stereotype.Component;

@Aspect
@Slf4j
@Component
@EnableAspectJAutoProxy
public class LoggingService {

	@Pointcut("within(com.cekeriya.openpayd..*)")
	public void generalPointcut() {
	}

	@AfterThrowing(pointcut = "generalPointcut()", throwing = "e")
	public void logAfterThrowing(JoinPoint joinPoint, Throwable e) {
		log.error("[Exception occurred in {}.{} method] [Input values = {}] [Message = {}",
				joinPoint.getSignature().getDeclaringTypeName(),
				joinPoint.getSignature().getName(),
				joinPoint.getArgs(),
				e.getCause() != null ? e.getCause() : e.toString());
	}
}
