package tasks;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class Epic extends AbstractTask {

    private final ArrayList<Subtask> subtasks;

    public Epic(String id, String name, String status, String description){
        this.id = Integer.parseInt(id);
        this.name = name;
        this.status = Status.valueOf(status);
        this.description = description;
        subtasks = new ArrayList<>();
        this.taskType = TaskType.EPIC;
    }

    public Epic(String id, String name, String status, String description, String startTime,
                String endTime, String duration){
        this.id = Integer.parseInt(id);
        this.name = name;
        this.status = Status.valueOf(status);
        this.description = description;
        subtasks = new ArrayList<>();
        this.taskType = TaskType.EPIC;
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

    public Epic(String name, String description) {
        super(name, description);
        this.taskType = TaskType.EPIC;
        subtasks = new ArrayList<>();
    }

    public ArrayList<Subtask> getSubtasks() {
        return subtasks;
    }

    public void addSubtask(Subtask subtask) {
        subtasks.add(subtask);
    }

    public void findEndTime(){
        if (subtasks.size() == 0 || ((subtasks.get(0).getStartTime() == null) &&
                (subtasks.get(0).getEndTime() == null) )){
            return;
        }
        LocalDateTime min = subtasks.get(0).getStartTime();
        LocalDateTime max = subtasks.get(0).getEndTime();
        for (Subtask subtask : subtasks){
            if (min == null && max == null) continue;
            if (subtask.getStartTime().isBefore(min)){
                min = subtask.getStartTime();
            }
            if (subtask.getEndTime().isAfter(max)){
                max = subtask.getEndTime();
            }
        }
        if (!(min == null) && !(max == null)) {
            this.duration = Duration.between(min, max);
            this.startTime = min;
            this.endTime = max;
        }
    }

    @Override
    public String toString() {
        return "Epic{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", duration=" + duration +
                ", subtasks=" + subtasks +
                '}';
    }
}
