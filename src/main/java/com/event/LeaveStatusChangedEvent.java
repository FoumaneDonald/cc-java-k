package com.event;

import com.enums.LeaveRequestStatus;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

import java.util.UUID;

@Getter
public class LeaveStatusChangedEvent extends ApplicationEvent {

    private final UUID leaveRequestId;
    private final UUID employeeId;
    private final LeaveRequestStatus previousStatus;
    private final LeaveRequestStatus newStatus;
    private final String rejectionReason;

    public LeaveStatusChangedEvent(Object source,
                                   UUID leaveRequestId,
                                   UUID employeeId,
                                   LeaveRequestStatus previousStatus,
                                   LeaveRequestStatus newStatus,
                                   String rejectionReason) {
        super(source);
        this.leaveRequestId = leaveRequestId;
        this.employeeId = employeeId;
        this.previousStatus = previousStatus;
        this.newStatus = newStatus;
        this.rejectionReason = rejectionReason;
    }

	public UUID getLeaveRequestId() {
		return leaveRequestId;
	}

	public UUID getEmployeeId() {
		return employeeId;
	}

	public LeaveRequestStatus getPreviousStatus() {
		return previousStatus;
	}

	public LeaveRequestStatus getNewStatus() {
		return newStatus;
	}

	public String getRejectionReason() {
		return rejectionReason;
	}
    
    
}
