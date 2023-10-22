import java.util.ArrayList;

public class Epic extends Task{

    private ArrayList<Subtask> subtasks;

    public Epic(String name, String description) {
        super(name, description);
        this.taskType = TaskType.EPIC;
    }

    public ArrayList<Subtask> getSubtasks() {
        return subtasks;
    }

    public void addSubtask(Subtask subtask){
        subtasks.add(subtask);
    }


}
