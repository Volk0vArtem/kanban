package managers;

import exceptions.ManagerLoadException;
import managers.FileBackedTasksManager;
import managers.Managers;
import managers.TaskManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Task;
import tasks.TaskType;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.lang.reflect.Executable;

public class FileBackedTasksManagerTest extends TaskManagerTest<FileBackedTasksManager> {

    @BeforeEach
    public void setUp(){
        taskManager = new FileBackedTasksManager(new File("test.csv"));
        initTasks();
    }

    @Test
    void saveLoad(){
        TaskManager newManager = FileBackedTasksManager.loadFromFile(new File("test.csv"));
        Assertions.assertEquals(taskManager.getTasks(), newManager.getTasks());
        Assertions.assertEquals(taskManager.getEpics(), newManager.getEpics());
        Assertions.assertEquals(taskManager.getSubtasks(), newManager.getSubtasks());
        Assertions.assertEquals(taskManager.getHistory(), newManager.getHistory());
    }

    @Test
    void saveLoadEmpty(){
        TaskManager newManager = FileBackedTasksManager.loadFromFile(new File("testEmpty.csv"));
        assertEquals(0, newManager.getTasks().size());
        assertEquals(0, newManager.getEpics().size());
        assertEquals(0, newManager.getSubtasks().size());
    }

    @Test
    void epicWithEmptySubtasks(){
        TaskManager newTaskManager = new FileBackedTasksManager(new File("test.csv"));
        newTaskManager.addObjective(new Epic("newEpic","emptySubtasks"), TaskType.EPIC);
        TaskManager load = FileBackedTasksManager.loadFromFile(new File("test.csv"));
        assertEquals(newTaskManager.getTasks(), load.getTasks());
        assertEquals(newTaskManager.getEpics(), load.getEpics());
        assertEquals(newTaskManager.getSubtasks(), load.getSubtasks());
        assertEquals(newTaskManager.getHistory(), load.getHistory());
    }

}
