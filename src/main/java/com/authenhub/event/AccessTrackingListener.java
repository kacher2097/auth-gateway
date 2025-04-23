package com.authenhub.event;

import com.authenhub.config.application.JsonMapper;
import com.authenhub.constant.Constant;
import com.authenhub.dto.AccessLogDTO;
import com.authenhub.entity.AccessLog;
import com.authenhub.mapper.AccessLogMapper;
import com.authenhub.repository.jpa.AccessLogJpaRepository;
import com.aventrix.jnanoid.jnanoid.NanoIdUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Slf4j
@Component
@RequiredArgsConstructor
public class AccessTrackingListener {

    private final JsonMapper jsonMapper;
    private final AccessLogMapper accessLogMapper;
    private final AccessLogJpaRepository accessLogJpaRepository;

    @Async
    @EventListener
    public void saveActionProcessJob(AccessTrackingEvent accessTrackingEvent) {
        MDC.put(Constant.TOKEN, NanoIdUtils.randomNanoId());
        log.info("Begin save access log with request {}", jsonMapper.toJson(accessTrackingEvent));
        long startTime = System.currentTimeMillis();
        try {
            AccessLogDTO accessLogDTO = accessTrackingEvent.getAccessLogDTO();
            if (Objects.isNull(accessLogDTO)) {
                log.info("[AccessTrackingListener] access log is null");
                return;
            }
            // Save access log
            AccessLog accessLog = accessLogMapper.toEntity(accessLogDTO);
            accessLogJpaRepository.save(accessLog);
            log.info("[AccessTrackingListener] save access log success");

        } catch (Exception e) {
            log.error("[AccessTrackingListener] save access log have exception", e);
        } finally {
            log.info("[AccessTrackingListener] end save access log in total time is {} ms", System.currentTimeMillis() - startTime);
        }
    }


}
