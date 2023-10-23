import java.util.ArrayList;

public class Epic extends Objective {


    private ArrayList<Subtask> subtasks;

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
        return "Epic{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", subtasks=" + subtasks +
                ", status=" + status +
                '}';
    }
}
