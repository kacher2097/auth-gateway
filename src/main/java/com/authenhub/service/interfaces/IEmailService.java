package com.authenhub.service.interfaces;

import com.authenhub.bean.mail.MailContent;
import jakarta.mail.MessagingException;

public interface IEmailService {
    void sendEmail(MailContent mail, String token, int typeMail) throws MessagingException;
}
