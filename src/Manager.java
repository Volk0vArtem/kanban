import java.util.ArrayList;
import java.util.HashMap;

public class Manager {

    private int taskId = 0;
    private int subtaskId = 0;
    private int epicId = 0;

    private HashMap<Integer, Task> tasks;
    private HashMap<Integer, Subtask> subtasks;
    private HashMap<Integer, Epic> epics;

    public Manager() {
        tasks = new HashMap<>();
        subtasks = new HashMap<>();
        epics = new HashMap<>();
    }

    public int getTaskId() {
        return taskId;
    }

    public int getSubtaskId() {
        return subtaskId;
    }

    public int getEpicId() {
        return epicId;
    }

    public HashMap<Integer, Task> getTasks() {
        return tasks;
    }

    public HashMap<Integer, Subtask> getSubtasks() {
        return subtasks;
    }

    public HashMap<Integer, Epic> getEpics() {
        return epics;
    }

    public void clearTasks() {
        tasks.clear();
    }

    public void clearEpics() {
        epics.clear();
        subtasks.clear();
    }

    public void clearSubtasks() {
        subtasks.clear();
        for (Epic epic : epics.values()) {
            update(epic.changeStatus(Status.NEW), epic.getId());
        }
    }

    public void addObjective(AbstractTask abstractTask, TaskType taskType) {
        switch (taskType) {
            case TASK:
                abstractTask.setId(taskId);
                tasks.put(taskId, (Task) abstractTask);
                taskId++;
                break;
            case EPIC:
                abstractTask.setId(epicId);
                epics.put(epicId, (Epic) abstractTask);
                epicId++;
                break;
            case SUBTASK:
                abstractTask.setId(subtaskId);
                Subtask subtask = (Subtask) abstractTask;
                subtasks.put(subtaskId, subtask);
                subtask.getEpic().addSubtask(subtask);
                subtaskId++;
                break;
            default:
                throw new IllegalArgumentException("Неправильно введен тип задачи");
        }
    }

    public AbstractTask getById(int id, TaskType type) {
        switch (type) {
            case TASK:
                return tasks.get(id);
            case EPIC:
                return epics.get(id);
            case SUBTASK:
                Subtask subtask = subtasks.get(id);
                return subtask;
            default:
                throw new IllegalArgumentException("Неправильно введен тип задачи");
        }
    }

    public void update(AbstractTask abstractTask, int id) {
        switch (abstractTask.getTaskType()) {
            case TASK:
                if (!tasks.containsKey(id)) {
                    System.out.println("Задача не найдена");
                    return;
                }
                tasks.put(id, (Task) abstractTask);
                break;
            case EPIC:
                if (!epics.containsKey(id)) {
                    System.out.println("Эпик не найден");
                    return;
                }
                epics.put(id, (Epic) abstractTask);
                break;
            case SUBTASK:
                if (!subtasks.containsKey(id)) {
                    System.out.println("Подзадача не найдена");
                    return;
                }
                subtasks.put(id, (Subtask) abstractTask);
                Subtask subtask = (Subtask) abstractTask;
                checkEpicStatus(subtask.getEpic().getId());
                break;
            default:
                throw new IllegalArgumentException("Неправильно введен тип задачи");
        }
    }

    public void deleteById(int id, TaskType taskType) {
        switch (taskType) {
            case TASK:
                if (!tasks.containsKey(id)) {
                    System.out.println("Задача не найдена");
                    return;
                }
                tasks.remove(id);
                break;
            case EPIC:
                if (!epics.containsKey(id)) {
                    System.out.println("Эпик не найден");
                    return;
                }
                Epic epic = epics.get(id);
                for (Subtask subtask : getSubtasksByEpic(epic.getId())) {
                    for (int subId : subtasks.keySet()) {
                        if (subtasks.get(subId).getEpic().equals(epic)) {
                            subtasks.remove(subId);
                        }
                    }
                }
                epics.remove(id);
                break;
            case SUBTASK:
                if (!subtasks.containsKey(id)) {
                    System.out.println("Подзадача не найдена");
                    return;
                }
                int epicId = subtasks.get(id).getEpic().getId();
                subtasks.remove(id);
                checkEpicStatus(epicId);
                break;
            default:
                throw new IllegalArgumentException("Неправильно введен тип задачи");

        }
    }

    public ArrayList<Subtask> getSubtasksByEpic(int epicId) {
        if (!epics.containsKey(epicId)) {
            throw new IllegalArgumentException("Эпик не найден");
        }
        Epic epic = epics.get(epicId);
        return epic.getSubtasks();
    }

    private void checkEpicStatus(int id) {
        Epic epic = epics.get(id);
        int inProgress = 0;
        int done = 0;

        for (Subtask subtask : epic.getSubtasks()) {
            if (subtask.status == Status.IN_PROGRESS) {
                inProgress++;
            } else if (subtask.status == Status.DONE) {
                done++;
            }
        }

        if (inProgress > 0) {
            epic.setStatus(Status.IN_PROGRESS);
            update(epic, id);
        } else if (done > 0 && inProgress == 0) {
            epic.setStatus(Status.DONE);
            update(epic, id);
        } else {
            epic.setStatus(Status.NEW);
            update(epic, id);
        }
    }
}
