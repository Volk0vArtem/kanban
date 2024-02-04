package managers;

import exceptions.TimeIntersectException;
import tasks.*;

import java.util.*;

public class InMemoryTaskManager implements TaskManager {

    protected final HashMap<Integer, Task> tasks;
    protected final HashMap<Integer, Subtask> subtasks;
    protected final HashMap<Integer, Epic> epics;
    protected final HistoryManager historyManager;
    protected final TreeSet<AbstractTask> prioritizedTasks;
    protected final ArrayList<AbstractTask> tasksWithoutTime;

    public InMemoryTaskManager() {
        tasks = new HashMap<>();
        subtasks = new HashMap<>();
        epics = new HashMap<>();
        historyManager = Managers.getDefaultHistory();
        AbstractTask.countReset();
        prioritizedTasks = new TreeSet<>(new Comparator<>() {
            @Override
            public int compare(AbstractTask o1, AbstractTask o2) {
                if ((o1.getTaskType().equals(TaskType.EPIC) && o2.getTaskType().equals(TaskType.SUBTASK) ||
                        o2.getTaskType().equals(TaskType.EPIC) && o1.getTaskType().equals(TaskType.SUBTASK)) &&
                        o1.getStartTime().isEqual(o2.getStartTime())){
                    return 1;
                }
                return o1.getStartTime().compareTo(o2.getStartTime());
            }
        });
        tasksWithoutTime = new ArrayList<>();
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
                try {
                    addToPrioritizedTasksList(abstractTask);
                } catch (TimeIntersectException e){
                    System.out.println(e.getMessage());
                    return;
                }
                tasks.put(abstractTask.getId(), (Task) abstractTask);
                break;
            case EPIC:
                try {
                    addToPrioritizedTasksList(abstractTask);
                } catch (TimeIntersectException e){
                    System.out.println(e.getMessage());
                    return;
                }
                epics.put(abstractTask.getId(), (Epic) abstractTask);
                break;
            case SUBTASK:
                try {
                    addToPrioritizedTasksList(abstractTask);
                } catch (TimeIntersectException e){
                    System.out.println(e.getMessage());
                    ((Subtask) abstractTask).getEpic().getSubtasks().remove(abstractTask); // TODO: 04.02.2024  
                    return;
                }
                Subtask subtask = (Subtask) abstractTask;
                subtasks.put(subtask.getId(), subtask);
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
        for (Integer i : tasks.keySet()) {
            if (i == id) {
                return tasks.get(id);
            }
        }

        for (Integer i : epics.keySet()) {
            if (i == id) {
                return epics.get(id);
            }
        }

        for (Integer i : subtasks.keySet()) {
            if (i == id) {
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
                Subtask subtask = (Subtask) abstractTask;
                subtasks.put(id, subtask);
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
                for (Integer i : subs) {
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
        int newSubtask = 0;

        for (Subtask subtask : epic.getSubtasks()) {
            if (subtask.getStatus() == Status.IN_PROGRESS) {
                inProgress++;
            } else if (subtask.getStatus() == Status.DONE) {
                done++;
            } else if (subtask.getStatus() == Status.NEW) {
                newSubtask++;
            }
        }
            if (inProgress > 0) {
                epic.setStatus(Status.IN_PROGRESS);
                update(epic, id);
            } else if (done > 0 && newSubtask > 0) {
                epic.setStatus(Status.IN_PROGRESS);
                update(epic, id);
            } else if (done > 0 && newSubtask == 0) {
                epic.setStatus(Status.DONE);
                update(epic, id);
            } else {
                epic.setStatus(Status.NEW);
                update(epic, id);

        }
    }
    @Override
    public void addToPrioritizedTasksList(AbstractTask task) throws TimeIntersectException {
        if (task.getStartTime() == null){
            tasksWithoutTime.add(task);
        } else {
            for (AbstractTask abstractTask : prioritizedTasks){
                if (task.getStartTime().isAfter(abstractTask.getStartTime()) &&
                    task.getStartTime().isBefore(abstractTask.getEndTime()) ||
                    task.getEndTime().isAfter(abstractTask.getStartTime()) &&
                    task.getEndTime().isBefore(abstractTask.getEndTime()) &&
                    task.getStartTime().isBefore(abstractTask.getStartTime()) &&
                    task.getEndTime().isAfter(abstractTask.getEndTime())) {

                    if (task.getTaskType().equals(TaskType.SUBTASK)){
                        if (((Subtask) task).getEpic().getId() == abstractTask.getId()) continue;
                    }
                    throw new TimeIntersectException("Задача пересекается по времени с задачей " + abstractTask.getId());
                }
            }
            prioritizedTasks.add(task);
        }
    }

    @Override
    public ArrayList<AbstractTask> getPrioritizedTasks(){
        ArrayList<AbstractTask> pt = new ArrayList<>(prioritizedTasks);
        pt.addAll(tasksWithoutTime);
        return pt;
    }
}
