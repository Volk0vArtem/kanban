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
    protected Comparator<AbstractTask> comparator = (o1, o2) -> {
        if ((o1.getTaskType().equals(TaskType.EPIC) && o2.getTaskType().equals(TaskType.SUBTASK) ||
                o2.getTaskType().equals(TaskType.EPIC) && o1.getTaskType().equals(TaskType.SUBTASK)) &&
                o1.getStartTime().isEqual(o2.getStartTime())){
            return 1;
        }
        return o1.getStartTime().compareTo(o2.getStartTime());
    };

    public InMemoryTaskManager() {
        tasks = new HashMap<>();
        subtasks = new HashMap<>();
        epics = new HashMap<>();
        historyManager = Managers.getDefaultHistory();
        AbstractTask.countReset();
        prioritizedTasks = new TreeSet<>(comparator);
        tasksWithoutTime = new ArrayList<>();
    }

    public List<AbstractTask> getHistory() {
        return historyManager.getHistory();
    }

    public void setHistory(List<Integer> history) {
        for (int i = history.size()-1; i >= 0; i--){
            historyManager.addToHistory(getByIdWithoutAddingToHistory(history.get(i)));
        }
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
        ArrayList<AbstractTask> taskList = new ArrayList<>();
        for (AbstractTask task : tasks.values()){
            taskList.add(task);
        }
        for (AbstractTask task : taskList){
            deleteById(task.getId(), TaskType.TASK);
        }
    }

    @Override
    public void clearEpics() {
        ArrayList<AbstractTask> taskList = new ArrayList<>();
        for (AbstractTask task : epics.values()){
            taskList.add(task);
        }
        for (AbstractTask task : taskList){
            deleteById(task.getId(), TaskType.EPIC);
        }
    }

    @Override
    public void clearSubtasks() {
        ArrayList<AbstractTask> taskList = new ArrayList<>();
        for (AbstractTask task : subtasks.values()){
            taskList.add(task);
        }
        for (AbstractTask task : taskList){
            deleteById(task.getId(), TaskType.SUBTASK);
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
                epics.put(abstractTask.getId(), (Epic) abstractTask);
                break;
            case SUBTASK:
                Subtask subtask = (Subtask) abstractTask;
                if (subtask.getEpic() == null){
                    subtask.setEpic((Epic) getById(subtask.getEpicId(), TaskType.EPIC));
                }
                try {
                    addToPrioritizedTasksList(abstractTask);
                } catch (TimeIntersectException e){
                    System.out.println(e.getMessage());
                    ((Subtask) abstractTask).getEpic().getSubtasks().remove(abstractTask);
                    return;
                }
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

    protected AbstractTask getByIdWithoutAddingToHistory(int id) {
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
                removeFromPrioritizedList(id);
                abstractTask.setId(id);
                try{
                    addToPrioritizedTasksList(abstractTask);
                } catch (TimeIntersectException e){
                    System.out.println(e.getMessage());
                }
                tasks.put(id, (Task) abstractTask);
                break;
            case EPIC:
                if (!epics.containsKey(id)) {
                    System.out.println("Эпик не найден");
                    return;
                }
                removeFromPrioritizedList(id);
                abstractTask.setId(id);
                try{
                    addToPrioritizedTasksList(abstractTask);
                } catch (TimeIntersectException e){
                    System.out.println(e.getMessage());
                }
                epics.put(id, (Epic) abstractTask);
                ((Epic) abstractTask).findEndTime();
                break;
            case SUBTASK:
                if (!subtasks.containsKey(id)) {
                    System.out.println("Подзадача не найдена");
                    return;
                }
                removeFromPrioritizedList(id);
                abstractTask.setId(id);
                try{
                    addToPrioritizedTasksList(abstractTask);
                } catch (TimeIntersectException e){
                    System.out.println(e.getMessage());
                }
                Subtask subtask = (Subtask) abstractTask;
                subtasks.put(id, subtask);
                checkEpicStatus(subtask.getEpic().getId());
                subtask.getEpic().findEndTime();
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
                removeFromPrioritizedList(id);
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
                            removeFromPrioritizedList(subId);
                            subs.add(subId);
                        }
                    }
                }
                for (Integer i : subs) {
                    subtasks.remove(i);
                }
                removeFromPrioritizedList(id);
                historyManager.remove(id);
                epics.remove(id);
                break;
            case SUBTASK:
                if (!subtasks.containsKey(id)) {
                    System.out.println("Подзадача не найдена");
                    return;
                }
                Epic epic1 = subtasks.get(id).getEpic();
                removeFromPrioritizedList(id);
                historyManager.remove(id);
                epic1.getSubtasks().remove(subtasks.get(id));
                subtasks.remove(id);
                checkEpicStatus(epic1.getId());
                epic1.findEndTime();
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

    private void checkEpicStatus(int id) {
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
                if (
                        task.getStartTime().isAfter(abstractTask.getStartTime()) &&
                        task.getStartTime().isBefore(abstractTask.getEndTime()) ||
                        task.getEndTime().isBefore(abstractTask.getEndTime()) &&
                        task.getEndTime().isAfter(abstractTask.getStartTime()) ||
                        task.getStartTime().isBefore(abstractTask.getStartTime()) &&
                        task.getEndTime().isAfter((abstractTask.getEndTime()))
                ) {

                    if (task.getTaskType().equals(TaskType.SUBTASK)){
                        if (((Subtask) task).getEpic().getId() == abstractTask.getId()) continue;
                    }
                    throw new TimeIntersectException("\nЗадача пересекается по времени с задачей " +
                            abstractTask.getId() +"\n");
                }
            }
            prioritizedTasks.add(task);
        }
    }
//
//    private void checkEpicPriority(Epic epic){
//        prioritizedTasks.remove(epic);
//        tasksWithoutTime.remove(epic);
//        try {
//            addToPrioritizedTasksList(epic);
//        } catch (TimeIntersectException e){
//
//        }
//    }

    @Override
    public ArrayList<AbstractTask> getPrioritizedTasks(){
        ArrayList<AbstractTask> pt = new ArrayList<>(prioritizedTasks);
        pt.addAll(tasksWithoutTime);
        return pt;
    }
    @Override
    public void removeFromPrioritizedList(int id){
        AbstractTask task = this.getByIdWithoutAddingToHistory(id);
        if (!getPrioritizedTasks().contains(task)) return;
        if (!tasksWithoutTime.remove(task)) {
            prioritizedTasks.remove(task);
        }
    }
}
