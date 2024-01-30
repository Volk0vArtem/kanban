package tasks;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public abstract class AbstractTask {

    protected String name;
    protected String description;
    protected Status status;
    protected TaskType taskType;
    protected int id;
    protected static int count;
    protected LocalDateTime startTime;
    protected LocalDateTime endTime;
    protected Duration duration;
    protected DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd_MM_yyyy|HH:mm");

    public AbstractTask(){}

    public void setTaskType(TaskType taskType) {
        this.taskType = taskType;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public Duration getDuration() {
        return duration;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public TaskType getTaskType() {
        return taskType;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Status getStatus() {
        return status;
    }

    public static int getCount() {
        return count;
    }

    public static void setCount(int count) {
        AbstractTask.count = count;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public AbstractTask(String name, String description) {
        this.name = name;
        this.description = description;
        this.status = Status.NEW;
        this.id = generateId();
    }

    protected Integer generateId() {
        return count++;
    }

    public static void countReset(){
        count = 0;
    }

    public AbstractTask changeStatus(Status status) {
        AbstractTask newTask = this;
        newTask.setStatus(status);
        return newTask;
    }

    @Override
    public String toString() {
        return "Objective{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status +
                ", id=" + id +
                ", startTime=" + startTime +
                ",endTime=" + endTime +
                "duration=" + duration +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AbstractTask that = (AbstractTask) o;
        return id == that.id && Objects.equals(name, that.name) && Objects.equals(description, that.description) && status == that.status && taskType == that.taskType && Objects.equals(startTime, that.startTime) && Objects.equals(endTime, that.endTime) && Objects.equals(duration, that.duration);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, description, status, taskType, id, startTime, endTime, duration);
    }

    public String toCSV(){
        if (startTime == null && endTime == null && duration == null){
            return id + "," + taskType + "," + name + "," + status + "," + description + "," + startTime
                    + "," + endTime + "," + duration;
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd_MM_yyyy|HH:mm");
        return id + "," + taskType + "," + name + "," + status + "," + description + "," + startTime.format(formatter)
                + "," + endTime.format(formatter) + "," + duration;

    }
}
