package org.swp391_group4_backend.ecosolution.wasterequest.statemachine;

import org.swp391_group4_backend.ecosolution.wasterequest.domain.entity.RequestStatus;

import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;

/**
 * Authoritative state machine for WasteRequest lifecycle.
 * 5 transitions, 6 statuses, no exceptions. (TECHNICAL_DESIGN §6)
 *
 * PENDING → ASSIGNED (Assignor)
 * PENDING → CANCELLED (Citizen)
 * ASSIGNED → ACCEPTED (Collector)
 * ACCEPTED → IN_PROGRESS (Collector)
 * IN_PROGRESS → COMPLETED (Collector)
 */
public final class WasteRequestStateMachine {

    private static final Map<RequestStatus, Set<RequestStatus>> TRANSITIONS = new EnumMap<>(RequestStatus.class);

    public static final EnumSet<RequestStatus> TERMINAL_STATES = EnumSet.of(
            RequestStatus.COMPLETED,
            RequestStatus.CANCELLED
    );

    static {
        TRANSITIONS.put(RequestStatus.PENDING, EnumSet.of(RequestStatus.ASSIGNED, RequestStatus.CANCELLED));
        TRANSITIONS.put(RequestStatus.ASSIGNED, EnumSet.of(RequestStatus.ACCEPTED));
        TRANSITIONS.put(RequestStatus.ACCEPTED, EnumSet.of(RequestStatus.IN_PROGRESS));
        TRANSITIONS.put(RequestStatus.IN_PROGRESS, EnumSet.of(RequestStatus.COMPLETED));
        TRANSITIONS.put(RequestStatus.COMPLETED, EnumSet.noneOf(RequestStatus.class));
        TRANSITIONS.put(RequestStatus.CANCELLED, EnumSet.noneOf(RequestStatus.class));
    }

    private WasteRequestStateMachine() {
        // Utility class — no instantiation
    }

    /**
     * Returns true if the transition from → to is valid per the defined lifecycle.
     */
    public static boolean isValidTransition(RequestStatus from, RequestStatus to) {
        Set<RequestStatus> allowed = TRANSITIONS.get(from);
        return allowed != null && allowed.contains(to);
    }

    /**
     * Returns true if the given status is a terminal state (COMPLETED or CANCELLED).
     */
    public static boolean isTerminal(RequestStatus status) {
        return TERMINAL_STATES.contains(status);
    }
}

