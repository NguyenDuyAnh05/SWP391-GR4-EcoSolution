EcoSolution Waste Management System
Project Overview
EcoSolution is a Backend system designed to optimize urban waste collection. It facilitates a seamless workflow between Citizens, Managers, and Collectors. The system allows citizens to report waste issues with real-time photo uploads via Cloudinary integration, enabling efficient task assignment and tracking.

System Architecture
The project follows a standard N-Tier Architecture:

Controller Layer: Handles HTTP requests and returns DTO responses.

Service Layer: Contains core business logic and transaction management.

Mapper Layer (BaseMapper): Responsible for data transformation between Entities and DTOs.

Repository Layer: Manages database interactions using Spring Data JPA.

Infrastructure: Cloudinary integration for cloud-based image storage.

API Documentation and Demo Flow
1. Citizen Workflow
   Goal: Report waste issues with proof and view submission history.

Create Waste Report

Method: POST

URL: /api/v1/reports

Body:

JSON
{
"description": "Large pile of plastic bottles near the park entrance.",
"locationAddress": "123 Le Loi Street, Ward 1, District 1, HCMC",
"imageUrl": "http://res.cloudinary.com/demo/image/upload/v123/waste.jpg",
"wasteType": "RECYCLABLE",
"citizenId": 1
}
View Personal History

Method: GET

URL: /api/v1/reports/citizen/{citizenId}

2. Manager Workflow
   Goal: Monitor pending reports and coordinate collection tasks.

View Pending Reports

Method: GET

URL: /api/v1/reports/pending

Description: Retrieves all reports with PENDING status.

Assign Collector

Method: PUT

URL: /api/v1/reports/assign

Body:

JSON
{
"reportId": 1,
"collectorId": 2
}
3. Collector Workflow
   Goal: Manage assigned tasks and update collection progress.

View Assigned Tasks

Method: GET

URL: /api/v1/reports/collector/{collectorId}

Update Task Status

Method: PUT

URL: /api/v1/reports/{id}/status

Body:

JSON
{
"status": "COLLECTED",
"confirmationImageUrl": "http://res.cloudinary.com/demo/image/upload/v123/proof_done.jpg"
}
Key Technical Features
Data Transfer Object (DTO) & Mapping
The system utilizes the DTO pattern to decouple the API contract from the Database Schema. Mapping is handled at the Controller level using a custom BaseMapper interface, ensuring that only necessary data is exposed to the client.

Global Exception Handling
A centralized exception handler manages errors such as ResourceNotFoundException and Validation Errors. Instead of raw Java stack traces, the API returns structured JSON error messages.

Cloudinary Integration
Images are not stored in the database. Only the URLs returned by Cloudinary are persisted, ensuring high performance and reduced storage costs on the application server.

Setup and Running
Configure Cloudinary credentials in application.properties.

Run the application; DataInitializer will automatically seed default users if the database is empty.

Use Postman to test the endpoints following the Demo Flow provided above.