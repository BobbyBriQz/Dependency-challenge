package com.mhp.coding.challenges.dependency.aspects;

import com.mhp.coding.challenges.dependency.inquiry.Inquiry;
import com.mhp.coding.challenges.dependency.notifications.EmailHandler;
import com.mhp.coding.challenges.dependency.notifications.PushNotificationHandler;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class InquiryServiceAspect {

    private Logger logger = LoggerFactory.getLogger(InquiryServiceAspect.class);

    @Value("${inquiry.send.email:true}")
    private boolean shouldSendEmail;

    @Value("${inquiry.send.notification:true}")
    private boolean shouldSendNotification;

    @Autowired
    EmailHandler emailHandler;

    @Autowired
    PushNotificationHandler pushNotificationHandler;

    @Pointcut(value= "execution(* com.mhp.coding.challenges.dependency.inquiry.InquiryService.create(..))")
    private void sendNotification(){

    }

    @Around(value= "sendNotification()")
    public void aroundAdvice(ProceedingJoinPoint joinPoint){
        try {
            Inquiry inquiry = (Inquiry) joinPoint.getArgs()[0];
            joinPoint.proceed();
            if(shouldSendEmail) emailHandler.sendEmail(inquiry);
            if(shouldSendNotification) pushNotificationHandler.sendNotification(inquiry);
        }catch (Throwable t){
            logger.info("An error occurred: ", t);
        }
    }
}
