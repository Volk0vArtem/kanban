public class Subtask extends Task{

    private Epic epic;

    public Epic getEpic() {
        return epic;
    }

    public Subtask(String name, String description, Epic epic) {
        super(name, description);
        this.epic = epic;
        this.taskType = TaskType.SUBTASK;
    }
}
