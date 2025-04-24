package com.authenhub.event;

import com.authenhub.dto.AccessLogDTO;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.ApplicationEvent;

import java.time.Clock;

@Getter
@Setter
public class AccessTrackingEvent extends ApplicationEvent {

    private AccessLogDTO accessLogDTO;

    public AccessTrackingEvent(Object source) {
        super(source);
    }

    public AccessTrackingEvent(Object source, AccessLogDTO accessLogDTO) {
        super(source);
        this.accessLogDTO = accessLogDTO;
    }
}
