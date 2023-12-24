package managers;

import tasks.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InMemoryTaskManager implements TaskManager{

    protected final HashMap<Integer, Task> tasks;
    protected final HashMap<Integer, Subtask> subtasks;
    protected final HashMap<Integer, Epic> epics;
    protected final HistoryManager historyManager;

    public InMemoryTaskManager() {
        tasks = new HashMap<>();
        subtasks = new HashMap<>();
        epics = new HashMap<>();
        historyManager = Managers.getDefaultHistory();
    }

    public List<AbstractTask> getHistory() {
        return historyManager.getHistory();
    }

    public HistoryManager getHistoryManager() {
        return historyManager;
    }

    @Override
    public HashMap<Integer, Task> getTasks() {
        return tasks;
    }
    @Override
    public HashMap<Integer, Subtask> getSubtasks() {
        return subtasks;
    }
    @Override
    public HashMap<Integer, Epic> getEpics() {
        return epics;
    }
    @Override
    public void clearTasks() {
        tasks.clear();
    }
    @Override
    public void clearEpics() {
        epics.clear();
        subtasks.clear();
    }
    @Override
    public void clearSubtasks() {
        subtasks.clear();
        for (Epic epic : epics.values()) {
            update(epic.changeStatus(Status.NEW), epic.getId());
        }
    }
    @Override
    public void addObjective(AbstractTask abstractTask, TaskType taskType) {
        switch (taskType) {
            case TASK:
                tasks.put(abstractTask.getId(), (Task) abstractTask);
                break;
            case EPIC:
                epics.put(abstractTask.getId(), (Epic) abstractTask);
                break;
            case SUBTASK:
                Subtask subtask = (Subtask) abstractTask;
                subtasks.put(subtask.getId(), subtask);
                subtask.getEpic().addSubtask(subtask);
                break;
            default:
                throw new IllegalArgumentException("Неправильно введен тип задачи");
        }
    }
    @Override
    public AbstractTask getById(int id, TaskType type) {
        switch (type) {
            case TASK:
                historyManager.addToHistory(tasks.get(id));
                return tasks.get(id);
            case EPIC:
                historyManager.addToHistory(epics.get(id));
                return epics.get(id);
            case SUBTASK:
                historyManager.addToHistory(subtasks.get(id));
                return subtasks.get(id);
            default:
                throw new IllegalArgumentException("Неправильно введен тип задачи");
        }
    }

    public AbstractTask getById(int id) {
        for (Integer i : tasks.keySet()){
            if (i == id){
                return tasks.get(id);
            }
        }

        for (Integer i : epics.keySet()){
            if (i == id){
                return epics.get(id);
            }
        }

        for (Integer i : subtasks.keySet()){
            if (i == id){
                return subtasks.get(id);
            }
        }
        throw new RuntimeException("Задача не найдена");
    }


    @Override
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
    @Override
    public void deleteById(int id, TaskType taskType) {
        switch (taskType) {
            case TASK:
                if (!tasks.containsKey(id)) {
                    System.out.println("Задача не найдена");
                    return;
                }
                historyManager.remove(id);
                tasks.remove(id);
                break;
            case EPIC:
                if (!epics.containsKey(id)) {
                    System.out.println("Эпик не найден");
                    return;
                }
                Epic epic = epics.get(id);
                List<Integer> subs = new ArrayList<>();
                for (Subtask subtask : getSubtasksByEpic(epic.getId())) {
                    for (int subId : subtasks.keySet()) {
                        if (subtasks.get(subId).getEpic().equals(epic)) {
                            historyManager.remove(subId);
                            subs.add(subId);
                        }
                    }
                }
                for (Integer i : subs){
                    subtasks.remove(i);
                }
                historyManager.remove(id);
                epics.remove(id);
                break;
            case SUBTASK:
                if (!subtasks.containsKey(id)) {
                    System.out.println("Подзадача не найдена");
                    return;
                }
                int epicId = subtasks.get(id).getEpic().getId();
                historyManager.remove(id);
                subtasks.remove(id);
                checkEpicStatus(epicId);
                break;
            default:
                throw new IllegalArgumentException("Неправильно введен тип задачи");

        }
    }
    @Override
    public ArrayList<Subtask> getSubtasksByEpic(int epicId) {
        if (!epics.containsKey(epicId)) {
            throw new IllegalArgumentException("Эпик не найден");
        }
        Epic epic = epics.get(epicId);
        return epic.getSubtasks();
    }

    protected void checkEpicStatus(int id) {
        Epic epic = epics.get(id);
        int inProgress = 0;
        int done = 0;

        for (Subtask subtask : epic.getSubtasks()) {
            if (subtask.getStatus() == Status.IN_PROGRESS) {
                inProgress++;
            } else if (subtask.getStatus() == Status.DONE) {
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
