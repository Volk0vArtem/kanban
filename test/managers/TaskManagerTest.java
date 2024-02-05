package managers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.*;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

abstract class TaskManagerTest<T extends TaskManager> {

    protected T taskManager;
    protected Task task;
    protected Epic epic;
    protected Subtask subtask;

    protected void initTasks(){
        task = new Task("TaskName", "TaskDescription",
                LocalDateTime.of(1982,1,1,15,0), Duration.ofHours(1));
        epic = new Epic("EpicName", "EpicDescription");
        subtask = new Subtask("SubtaskName", "SubtaskDescription", epic,
                LocalDateTime.of(2002,1,1,15,0), Duration.ofHours(12));
        taskManager.addObjective(task, TaskType.TASK);
        taskManager.addObjective(epic, TaskType.EPIC);
        taskManager.addObjective(subtask, TaskType.SUBTASK);
    }


    @BeforeEach
    public void setUp(){

    }

    @Test
    void addNewEpic() {
        Epic newEpic = new Epic("name", "description");
        taskManager.addObjective(newEpic, TaskType.EPIC);
        assertEquals(newEpic, taskManager.getById(3));
    }

    @Test
    void addNewSubtask() {
        Subtask newSubtask = new Subtask("name", "description", epic);
        taskManager.addObjective(newSubtask, TaskType.SUBTASK);
        assertEquals(newSubtask, taskManager.getById(3));
    }

    @Test
    void addNewTask() {
        Task task = new Task("name", "id3");
        taskManager.addObjective(task, TaskType.TASK);
        assertEquals(task, taskManager.getById(3, TaskType.TASK));
    }

    @Test
    void deleteEpic() {
        taskManager.deleteById(1, TaskType.EPIC);
        assertEquals(0, taskManager.getEpics().size());
        assertEquals(0, taskManager.getSubtasks().size());
        assertEquals(1, taskManager.getTasks().size());
        assertEquals(1, taskManager.getPrioritizedTasks().size());
    }

    @Test
    void deleteSubtask() {
        taskManager.deleteById(2, TaskType.SUBTASK);
        assertEquals(1, taskManager.getEpics().size());
        assertEquals(0, taskManager.getSubtasks().size());
        assertEquals(1, taskManager.getTasks().size());
        assertEquals(2, taskManager.getPrioritizedTasks().size());
    }

    @Test
    void deleteTask() {
        taskManager.deleteById(0, TaskType.TASK);
        assertEquals(1, taskManager.getEpics().size());
        assertEquals(1, taskManager.getSubtasks().size());
        assertEquals(0, taskManager.getTasks().size());
        assertEquals(2, taskManager.getPrioritizedTasks().size());
    }

    @Test
    void updatePrioritizedList(){
        Task newTask = new Task("updatedTask", "2030",
                LocalDateTime.of(2030, 9,12, 10,0), Duration.ofHours(10));
        taskManager.update(newTask,0);
        ArrayList<AbstractTask> result = new ArrayList<>();
        result.add(epic);
        result.add(subtask);
        result.add(newTask);
        assertEquals(result, taskManager.getPrioritizedTasks());
    }

    @Test
    void checkEpicStatus(){
        Epic newEpic = new Epic("name", "description");
        taskManager.addObjective(newEpic, TaskType.EPIC);
        assertEquals(Status.NEW, epic.getStatus());

        Subtask newSubtask1 = new Subtask("newSubtask1", "new", newEpic);
        Subtask newSubtask2 = new Subtask("newSubtask1", "new", newEpic);

        taskManager.addObjective(newSubtask1, TaskType.SUBTASK);
        taskManager.addObjective(newSubtask2, TaskType.SUBTASK);
        assertEquals(Status.NEW, newEpic.getStatus());

        taskManager.update(newSubtask1.changeStatus(Status.IN_PROGRESS), 4);
        assertEquals(Status.IN_PROGRESS, newEpic.getStatus());

        taskManager.update(newSubtask2.changeStatus(Status.IN_PROGRESS), 5);
        assertEquals(Status.IN_PROGRESS, newEpic.getStatus());

        taskManager.update(newSubtask1.changeStatus(Status.DONE), 4);
        assertEquals(Status.IN_PROGRESS, newEpic.getStatus());

        taskManager.update(newSubtask2.changeStatus(Status.DONE), 5);
        assertEquals(Status.DONE, newEpic.getStatus());
    }

    @Test
    void getEpic() {
        assertEquals(epic, taskManager.getById(1));
    }

    @Test
    void getEpics() {
        Epic newEpic = new Epic("newEpic", "-");
        taskManager.addObjective(newEpic, TaskType.EPIC);
        HashMap<Integer, Epic> epics = new HashMap<>();
        epics.put(1,epic);
        epics.put(3, newEpic);
        assertEquals(epics, taskManager.getEpics());
    }

    @Test
    void getSubtasksByEpic() {
        Subtask newSubtask = new Subtask("newSubtask", "-", epic);
        taskManager.addObjective(newSubtask, TaskType.SUBTASK);
        ArrayList<Subtask> subtasks = new ArrayList<>();
        subtasks.add(subtask);
        subtasks.add(newSubtask);
        assertEquals(subtasks, taskManager.getSubtasksByEpic(1));
    }

    @Test
    void getHistory() {
        taskManager.getById(0, TaskType.TASK);
        taskManager.getById(1, TaskType.EPIC);
        taskManager.getById(2, TaskType.SUBTASK);

        ArrayList<AbstractTask> history = new ArrayList<>();
        history.add(subtask);
        history.add(epic);
        history.add(task);
        assertEquals(history, taskManager.getHistory());
    }

    @Test
    void getPrioritizedTasks(){
        ArrayList<AbstractTask> result = new ArrayList<>();
        result.add(task);
        result.add(epic);
        result.add(subtask);
        assertEquals(result, taskManager.getPrioritizedTasks());
        Task task1 = new Task("TaskName", "TaskDescription",
                LocalDateTime.of(2002,1,1,16,30), Duration.ofHours(20));
        taskManager.addObjective(task1, TaskType.TASK);
        assertEquals(result, taskManager.getPrioritizedTasks());
        Task task2 = new Task("name", "description");
        taskManager.addObjective(task2, TaskType.TASK);
        result.add(task2);
        assertEquals(result, taskManager.getPrioritizedTasks());
    }

    @Test
    void getEpicEndTime(){
        Subtask subtask1 = new Subtask("name", "description", epic,
                LocalDateTime.of(1990, 9,20,11,0), Duration.ofHours(14));
        Subtask subtask2 = new Subtask("name", "description", epic,
                LocalDateTime.of(2003, 11,17,22,0), Duration.ofHours(100));
        taskManager.addObjective(subtask1, TaskType.SUBTASK);
        taskManager.addObjective(subtask2, TaskType.SUBTASK);
        ArrayList<AbstractTask> result = new ArrayList<>();
        result.add(task);
        result.add(epic);
        result.add(subtask1);
        result.add(subtask);
        result.add(subtask2);
        assertEquals(result, taskManager.getPrioritizedTasks());
        assertEquals(subtask1.getStartTime(), epic.getStartTime());
        assertEquals(subtask2.getEndTime(), epic.getEndTime());

    }

    @Test
    void getSubtask() {
        assertEquals(subtask, taskManager.getById(2));
    }

    @Test
    void getSubtasks() {
        Subtask newSubtask = new Subtask("newSubtask", "-", epic);
        taskManager.addObjective(newSubtask, TaskType.SUBTASK);
        HashMap<Integer, Subtask> subtasks = new HashMap<>();
        subtasks.put(2, subtask);
        subtasks.put(3, newSubtask);
        assertEquals(subtasks, taskManager.getSubtasks());
    }

    @Test
    void getTask() {
        assertEquals(task, taskManager.getById(0));
    }

    @Test
    void getTasks() {
        Task newTask = new Task("newTask", "-");
        taskManager.addObjective(newTask, TaskType.TASK);
        HashMap<Integer, Task> tasks = new HashMap<>();
        tasks.put(0, task);
        tasks.put(3, newTask);
        assertEquals(tasks, taskManager.getTasks());
    }

    @Test
    void updateEpic() {
        Epic updatedEpic = new Epic("newEpic", "updatedEpic");
        taskManager.update(updatedEpic, 1);
        assertEquals(updatedEpic, taskManager.getById(1));
    }

    @Test
    void updateSubtask() {
        Subtask updatedSubtask = new Subtask("newSubtask", "updatedSubtask", epic);
        taskManager.addObjective(updatedSubtask, TaskType.SUBTASK);
        assertEquals(updatedSubtask, taskManager.getById(3));
    }

    @Test
    void updateTask() {
        Task updatedTask = new Task("newTask", "updatedTask");
        taskManager.addObjective(updatedTask, TaskType.TASK);
        assertEquals(updatedTask, taskManager.getById(3));
    }

}
