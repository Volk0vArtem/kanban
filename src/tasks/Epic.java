package tasks;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class Epic extends AbstractTask {

    private ArrayList<Subtask> subtasks;

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
        if (subtasks.size() == 0){
            return;
        }
        LocalDateTime min = subtasks.get(0).startTime;
        LocalDateTime max = subtasks.get(0).endTime;
        for (Subtask subtask : subtasks){
            if (min == null && max == null) continue;
            if (subtask.getStartTime().isBefore(min)){
                min = subtask.getStartTime();
            }
            if (subtask.getEndTime().isAfter(max)){
                max = subtask.getEndTime();
            }
        }
        this.duration = Duration.between(min,max);
        this.startTime = min;
        this.endTime = max;
    }

    @Override
    public String toString() {
        return "Tasks.Epic{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", subtasks=" + subtasks +
                ", status=" + status +
                ", id=" + id +
                '}';
    }
}
