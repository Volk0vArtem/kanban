package tasks;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Task extends AbstractTask {

    public Task(String name, String description, LocalDateTime startTime, Duration duration){
        super(name, description);
        this.taskType = TaskType.TASK;
        this.startTime = startTime;
        this.duration = duration;
        this.endTime = startTime.plus(duration);
    }

    public Task(String id, String name, String status, String description, String startTime,
                String endTime, String duration){
        this.id = Integer.parseInt(id);
        this.name = name;
        this.status = Status.valueOf(status);
        this.description = description;
        this.taskType = TaskType.TASK;
        if (startTime.equals("null")){
            this.startTime = null;
            this.endTime = null;
            this.duration = null;
        } else {
            this.startTime = LocalDateTime.parse(startTime, formatter);
            this.endTime = LocalDateTime.parse(endTime, formatter);
            this.duration = Duration.parse(duration);
        }
    }

    public Task(String name, String description) {
        super(name, description);
        this.taskType = TaskType.TASK;
    }

    public String toString() {
        return "Tasks.Task{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status +
                ", id=" + id +
                '}';
    }
}
