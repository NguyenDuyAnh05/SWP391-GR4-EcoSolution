package org.swp391_group4_backend.ecosolution.auth.domain.entity;

public enum UserRole {
  CITIZEN,           // Regular citizen user
  COLLECTOR,         // Waste collector
  ASSIGNOR,          // Enterprise role: assigns tasks to collectors
  ENTERPRISE_ADMIN,  // Enterprise role: manages enterprise, assigns roles
  SYSTEM_ADMIN       // System-wide administrator
}
