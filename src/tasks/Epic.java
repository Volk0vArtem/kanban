package tasks;

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
