package tasks;

import java.util.Objects;

public abstract class AbstractTask {

    protected String name;
    protected String description;
    protected Status status;
    protected TaskType taskType;
    protected int id;
    protected static int count;

    public AbstractTask(){}

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
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AbstractTask abstractTask = (AbstractTask) o;
        return Objects.equals(name, abstractTask.name) && Objects.equals(description, abstractTask.description) && status == abstractTask.status && taskType == abstractTask.taskType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, description, status, taskType);
    }

    public String toCSV(){
        return id + "," + taskType + "," + name + "," + status + "," + description;
    }


}
