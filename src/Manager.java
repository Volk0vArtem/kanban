import java.util.ArrayList;
import java.util.HashMap;

public class Manager {

    private int taskId = 0;
    private int subTaskId = 0;
    private int epicId = 0;

    private HashMap<Integer, Task> tasks;
    private HashMap<Integer, Task> subtasks;
    private HashMap<Integer, Task> epics;

    public int getTaskId() {
        return taskId;
    }

    public int getSubTaskId() {
        return subTaskId;
    }

    public int getEpicId() {
        return epicId;
    }

    public HashMap<Integer, Task> getTasks() {
        return tasks;
    }

    public HashMap<Integer, Task> getSubtasks() {
        return subtasks;
    }

    public HashMap<Integer, Task> getEpics() {
        return epics;
    }

    public void clearTasks() {
        tasks.clear();
        taskId = 0;
    }

    public void clearEpics() {
        epics.clear();
        epicId = 0;
    }

    public void clearSubtasks() {
        subtasks.clear();
        subTaskId = 0;
    }

    public void addTask(Task task) {
        tasks.put(taskId, task);
        taskId++;
    }

    public void addEpic(Epic epic) {
        epics.put(epicId, epic);
        epicId++;
    }

    public void addSubtask(Subtask subtask) {
        subtasks.put(subTaskId, subtask);
        subTaskId++;
    }

    public Task getById(int id, TaskType type) {
        switch (type) {
            case TASK:
                return tasks.get(id);
            case EPIC:
                Epic epic = (Epic) epics.get(id);
                return epic;
            case SUBTASK:
                Subtask subtask = (Subtask) subtasks.get(id);
                return subtask;
            default: throw new IllegalArgumentException("Неправильно введен тип задачи");
        }
    }

    public void update(Task task, int id) {
        switch (task.getTaskType()) {
            case TASK:
                if (!tasks.keySet().contains(id)){
                    System.out.println("Задача не найдена");
                    return;
                }
                tasks.put(id, task);
                break;
            case EPIC:
                if (!epics.keySet().contains(id)){
                    System.out.println("Эпик не найдена");
                    return;
                }
                epics.put(id, task);
                break;
            case SUBTASK:
                if (!subtasks.keySet().contains(id)){
                    System.out.println("Подзадача не найдена");
                    return;
                }
                subtasks.put(id, task);
                Subtask subtask = (Subtask) task;
                checkStatus(subtask.getEpic().getId());
                break;
            default: throw new IllegalArgumentException("Неправильно введен тип задачи");
        }
    }

    public void deleteById(int id, TaskType taskType) {
        switch (taskType) {
            case TASK:
                if (!tasks.keySet().contains(id)){
                    System.out.println("Задача не найдена");
                    return;
                }
                tasks.remove(id);
                break;
            case EPIC:
                if (!epics.keySet().contains(id)){
                    System.out.println("Эпик не найдена");
                    return;
                }
                epics.remove(id);
                break;
            case SUBTASK:
                if (!subtasks.keySet().contains(id)){
                    System.out.println("Подзадача не найдена");
                    return;
                }
                subtasks.remove(id);
                break;
            default: throw new IllegalArgumentException("Неправильно введен тип задачи");

        }
    }

    public ArrayList<Subtask> getSubtasksByEpic(int epicId){
        if (!epics.keySet().contains(epicId)){
            throw new IllegalArgumentException("Эпик не найден");
        }
        Epic epic = (Epic) epics.get(epicId);
        return epic.getSubtasks();
    }

    private void checkStatus(int epicId) {
        Epic epic = (Epic) epics.get(epicId);
        int newSubtasks = 0;
        int doneSubtasks = 0;

        for (Subtask subtask : epic.getSubtasks()){
            if (subtask.status == Status.NEW){
                newSubtasks++;
            } else if (subtask.status == Status.DONE){
                doneSubtasks++;
            }
        }

        if (subtasks.size() == newSubtasks){
            epic.setStatus(Status.NEW);
            update(epic, epicId);
        } else if (subtasks.size() == doneSubtasks){
            epic.setStatus(Status.DONE);
            update(epic, epicId);
        } else {
            epic.setStatus(Status.IN_PROGRESS);
            update(epic, epicId);
        }
    }
}
