public class Task extends Objective {

    public Task(String name, String description) {
        super(name, description);
        this.taskType = TaskType.TASK;
    }
}
