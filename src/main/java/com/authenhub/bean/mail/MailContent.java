package com.authenhub.bean.mail;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MailContent {

    private String from;
    private String subject;
    private String mailTo;
    private Map<String, Object> props;
    private List<InputStream> lstAttachmentFileInputStream;
    private List<String> lstAttachmentFileName;
}
