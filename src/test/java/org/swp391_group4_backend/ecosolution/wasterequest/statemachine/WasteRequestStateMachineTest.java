package org.swp391_group4_backend.ecosolution.wasterequest.statemachine;

import org.junit.jupiter.api.Test;
import org.swp391_group4_backend.ecosolution.wasterequest.domain.entity.RequestStatus;

import static org.junit.jupiter.api.Assertions.*;

class WasteRequestStateMachineTest {

    @Test
    void isValidTransition_ValidTransitions_ReturnsTrue() {
        assertTrue(WasteRequestStateMachine.isValidTransition(RequestStatus.PENDING, RequestStatus.ASSIGNED));
        assertTrue(WasteRequestStateMachine.isValidTransition(RequestStatus.PENDING, RequestStatus.CANCELLED));
        assertTrue(WasteRequestStateMachine.isValidTransition(RequestStatus.ASSIGNED, RequestStatus.ACCEPTED));
        assertTrue(WasteRequestStateMachine.isValidTransition(RequestStatus.ACCEPTED, RequestStatus.IN_PROGRESS));
        assertTrue(WasteRequestStateMachine.isValidTransition(RequestStatus.IN_PROGRESS, RequestStatus.COMPLETED));
    }

    @Test
    void isValidTransition_InvalidTransitions_ReturnsFalse() {
        assertFalse(WasteRequestStateMachine.isValidTransition(RequestStatus.PENDING, RequestStatus.ACCEPTED));
        assertFalse(WasteRequestStateMachine.isValidTransition(RequestStatus.ASSIGNED, RequestStatus.PENDING));
        assertFalse(WasteRequestStateMachine.isValidTransition(RequestStatus.COMPLETED, RequestStatus.PENDING));
        assertFalse(WasteRequestStateMachine.isValidTransition(RequestStatus.CANCELLED, RequestStatus.PENDING));
    }

    @Test
    void isTerminal_TerminalStates_ReturnsTrue() {
        assertTrue(WasteRequestStateMachine.isTerminal(RequestStatus.COMPLETED));
        assertTrue(WasteRequestStateMachine.isTerminal(RequestStatus.CANCELLED));
    }

    @Test
    void isTerminal_NonTerminalStates_ReturnsFalse() {
        assertFalse(WasteRequestStateMachine.isTerminal(RequestStatus.PENDING));
        assertFalse(WasteRequestStateMachine.isTerminal(RequestStatus.ASSIGNED));
        assertFalse(WasteRequestStateMachine.isTerminal(RequestStatus.ACCEPTED));
        assertFalse(WasteRequestStateMachine.isTerminal(RequestStatus.IN_PROGRESS));
    }
}
