### Waste Request Module Overview

The `waste-request` module is a core part of the EcoSolution system, managing the lifecycle of waste collection requests from creation by a Citizen to fulfillment by a Collector.

### 1. High-Level Concept
The module acts as a coordination hub between three primary actors:
*   **Citizen**: Submits requests for waste collection at a specific location and preferred date.
*   **Assignor (Staff/Admin)**: Reviews pending requests and assigns them to available Collectors.
*   **Collector**: Receives assignments, travels to the location, and marks the request as completed with evidence (image and actual waste quantity).

The module enforces a strict state machine to ensure data integrity and process compliance, following the path: `PENDING` → `ASSIGNED` → `ACCEPTED` → `IN_PROGRESS` → `COMPLETED` (or `CANCELLED` by the Citizen).

### 2. Main Components to Focus On
To work effectively on this module, you should be familiar with these key components:

*   **`WasteRequestController`**: The entry point for all API calls. It separates endpoints by actor (Citizen, Assignor, Collector).
*   **`WasteRequestService` (and its implementation `WasteRequestServiceImpl`)**: Contains the core business logic, including validation, state transitions, and role checks.
*   **`WasteRequestStateMachine`**: A utility class that defines the authoritative rules for status transitions. It prevents invalid flows (e.g., jumping from `PENDING` to `COMPLETED`).
*   **`WasteRequest` Entity**: The central data model that stores original report details (location, type, quantity) and collector-reported data (actual quantity, evidence image).
*   **`WasteRequestRepository`**: Handles database interactions for retrieving and saving requests.

### 3. Data Flow for the Citizen Actor
The Citizen starts the data flow through the following steps:

1.  **Creation**:
    *   The Citizen submits a `WasteRequestCreateRequestDto` via `POST /api/v1/requests`.
    *   The system validates the user's status (active/not suspended).
    *   A `WasteRequest` record is created with status `PENDING`.
    *   Data stored: `wasteType`, `quantity`, `address`, `latitude/longitude`, and `preferredDate`.

2.  **Tracking**:
    *   The Citizen can view their submitted requests via `GET /api/v1/requests/my`.
    *   This retrieves a list of `WasteRequestResponseDto` showing the current status (e.g., if it has been assigned yet).

3.  **Cancellation (Optional)**:
    *   If the Citizen no longer needs the service, they can call `PATCH /api/v1/requests/{id}/cancel`.
    *   The system checks if the request is still `PENDING`. Once it moves to `ASSIGNED`, cancellation might be restricted (per business rules in `WasteRequestServiceImpl`).

4.  **Completion Relationship**:
    *   While the Citizen doesn't "perform" the completion, the module relates the final data (actual quantity collected and the evidence image) back to the original request ID so the Citizen can verify what was picked up.