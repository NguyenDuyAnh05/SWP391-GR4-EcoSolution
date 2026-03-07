package org.swp391_group4_backend.ecosolution.tasks.exception;
import org.swp391_group4_backend.ecosolution.tasks.domain.entity.TaskStatus;
public class InvalidTaskTransitionException extends RuntimeException {
  private final TaskStatus from;
  private final TaskStatus to;
  public InvalidTaskTransitionException(TaskStatus from, TaskStatus to) {
    super("Invalid task status transition from " + from + " to " + to);
    this.from = from;
    this.to = to;
  }
  public TaskStatus getFrom() {
    return from;
  }
  public TaskStatus getTo() {
    return to;
  }
}
