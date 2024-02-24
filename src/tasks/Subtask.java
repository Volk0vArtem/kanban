package tasks;

import java.time.Duration;
import java.time.LocalDateTime;

public class Subtask extends AbstractTask {

    private Epic epic;
    private int epicId;

    public Subtask(String name, String description, Epic epic, LocalDateTime startTime, Duration duration) {
        super(name, description);
        this.epic = epic;
        this.taskType = TaskType.SUBTASK;
        this.startTime = startTime;
        this.duration = duration;
        this.endTime = startTime.plus(duration);
        epic.getSubtasks().add(this);
        epic.findEndTime();
    }

    public Subtask(String name, String description, Epic epic) {
        super(name, description);
        this.epic = epic;
        this.taskType = TaskType.SUBTASK;
        epic.getSubtasks().add(this);
    }

    public Subtask(String id, String name, String status, String description, Epic epic, String startTime, String duration) {
        this.id = Integer.parseInt(id);
        this.name = name;
        this.status = Status.valueOf(status);
        this.description = description;
        this.epic = epic;
        this.taskType = TaskType.SUBTASK;
        if (startTime.equals("null")){
            this.startTime = null;
            this.endTime = null;
            this.duration = null;
        } else {
            this.startTime = LocalDateTime.parse(startTime, formatter);
            this.duration = Duration.parse(duration);
            this.endTime = this.startTime.plus(this.duration);
        }
        epic.getSubtasks().add(this);
    }

    public Subtask(String id, String name, String status, String description, String epicId, String startTime, String duration) {
        this.id = Integer.parseInt(id);
        this.name = name;
        this.status = Status.valueOf(status);
        this.description = description;
        this.epicId = Integer.parseInt(epicId);
        this.taskType = TaskType.SUBTASK;
        if (startTime.equals("null")){
            this.startTime = null;
            this.endTime = null;
            this.duration = null;
        } else {
            this.startTime = LocalDateTime.parse(startTime, formatter);
            this.duration = Duration.parse(duration);
            this.endTime = this.startTime.plus(this.duration);
        }
    }

    public Subtask(String id, String name, String status, String description, Epic epic) {
        this.id = Integer.parseInt(id);
        this.name = name;
        this.status = Status.valueOf(status);
        this.description = description;
        this.epic = epic;
        this.taskType = TaskType.SUBTASK;
        epic.getSubtasks().add(this);
    }


    public Epic getEpic() {
        return epic;
    }
    public int getEpicId(){
        return epicId;
    }

    public void setEpic(Epic epic){
        this.epic = epic;
        epic.addSubtask(this);
        epic.findEndTime();
    }

    @Override
    public String toString() {
        return "Subtask{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", duration=" + duration +
                ", epic=" + epic.getId() +
                '}';
    }

    @Override
    public String toCSV() {
        if (startTime == null && endTime == null && duration == null){
            return id + "," + taskType + "," + name + "," + status + "," + description + "," + startTime
                    + "," + endTime + "," + duration + "," + epic.getId();
        }
        return id + "," + taskType + "," + name + "," + status + "," + description + "," + startTime.format(formatter) +
                "," + endTime.format(formatter) + "," + duration + "," + epic.getId();
    }
}
