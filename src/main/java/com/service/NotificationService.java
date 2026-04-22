package com.service;

import com.event.LeaveStatusChangedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class NotificationService {

    // RG-M2-10: notify approver or employee on each status change
    @Async
    @EventListener
    public void onLeaveStatusChanged(LeaveStatusChangedEvent event) {
        switch (event.getNewStatus()) {
            case SUBMITTED ->
                System.out.printf("[NOTIFY] Leave request {} submitted by employee {}. Awaiting Chef de Service approval.",
                        event.getLeaveRequestId(), event.getEmployeeId());
            case VALIDATED ->
            	System.out.printf("[NOTIFY] Leave request {} validated by Chef. Awaiting RH Manager confirmation.",
                        event.getLeaveRequestId());
            case CONFIRMED ->
            	System.out.printf("[NOTIFY] Leave request {} confirmed by RH. Employee {} notified.",
                        event.getLeaveRequestId(), event.getEmployeeId());
            case REJECTED ->
            	System.out.printf("[NOTIFY] Leave request {} rejected. Employee {} notified. Reason: {}",
                        event.getLeaveRequestId(), event.getEmployeeId(), event.getRejectionReason());
            default -> { /* DRAFT — no notification needed */ }
        }
        // TODO: wire to actual email/SMS/push service here
    }
}
