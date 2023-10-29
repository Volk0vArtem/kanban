package Manager;

import Tasks.*;

import java.util.ArrayList;
import java.util.HashMap;

public interface TaskManager {
    public HashMap<Integer, Task> getTasks();

    public HashMap<Integer, Subtask> getSubtasks();
    public HashMap<Integer, Epic> getEpics();
    public void clearTasks();
    public void clearEpics();
    public void clearSubtasks();

    void addObjective(AbstractTask abstractTask, TaskType taskType);

    AbstractTask getById(int id, TaskType type);

    void update(AbstractTask abstractTask, int id);

    void deleteById(int id, TaskType taskType);

    ArrayList<Subtask> getSubtasksByEpic(int epicId);
}
