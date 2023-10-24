public class Task extends AbstractTask {

    public Task(String name, String description) {
        super(name, description);
        this.taskType = TaskType.TASK;
    }
}
