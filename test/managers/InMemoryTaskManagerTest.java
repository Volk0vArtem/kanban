package managers;

import managers.InMemoryTaskManager;
import managers.Managers;
import managers.TaskManager;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;
import tasks.TaskType;
import static org.junit.jupiter.api.Assertions.*;

public class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {

    @BeforeEach
    public void setUp(){
        taskManager = Managers.getDefault();
        initTasks();
    }

}
