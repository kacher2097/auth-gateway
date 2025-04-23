package com.authenhub.event;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AccessTrackingPublisher {

    private final ApplicationEventPublisher applicationEventPublisher;

    public void publishEvent(AccessTrackingEvent event) {
        // Publish the event
        applicationEventPublisher.publishEvent(event);
    }

}
