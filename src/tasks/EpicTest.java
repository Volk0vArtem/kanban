package tasks;

import managers.InMemoryTaskManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class EpicTest {
    Epic epic;
    InMemoryTaskManager manager;

    @BeforeEach
    public void setEpic(){
        manager = new InMemoryTaskManager();
        epic = new Epic("name", "description");
        manager.addObjective(epic, TaskType.EPIC);
    }

    @Test
    public void emptySubtasks(){
        assertTrue(epic.getSubtasks().isEmpty());
    }

    @Test
    public void newSubtasks(){
        Subtask subtask1 = new Subtask("name","description",epic);
        Subtask subtask2 = new Subtask("name","description",epic);
        manager.addObjective(subtask1, TaskType.SUBTASK);
        manager.addObjective(subtask2, TaskType.SUBTASK);
        assertEquals(Status.NEW, epic.getStatus());
    }

    @Test
    public void doneSubtasks(){
        Subtask subtask1 = new Subtask("name","description",epic);
        Subtask subtask2 = new Subtask("name","description",epic);
        manager.addObjective(subtask1, TaskType.SUBTASK);
        manager.addObjective(subtask2, TaskType.SUBTASK);
        subtask1.setStatus(Status.DONE);
        manager.update(subtask1, subtask1.getId());
        subtask2.setStatus(Status.DONE);
        manager.update(subtask2, subtask2.getId());
        assertEquals(Status.DONE, epic.getStatus());
    }

    @Test
    public void newAndDoneSubtasks(){
        Subtask subtask1 = new Subtask("name","description",epic);
        Subtask subtask2 = new Subtask("name","description",epic);
        manager.addObjective(subtask1, TaskType.SUBTASK);
        manager.addObjective(subtask2, TaskType.SUBTASK);
        subtask1.setStatus(Status.DONE);
        manager.update(subtask2, subtask2.getId());
        assertEquals(Status.IN_PROGRESS, epic.getStatus());
    }

    @Test
    public void inProgressSubtasks(){
        Subtask subtask1 = new Subtask("name","description",epic);
        Subtask subtask2 = new Subtask("name","description",epic);
        manager.addObjective(subtask1, TaskType.SUBTASK);
        manager.addObjective(subtask2, TaskType.SUBTASK);
        subtask1.setStatus(Status.IN_PROGRESS);
        manager.update(subtask1, subtask1.getId());
        subtask1.setStatus(Status.IN_PROGRESS);
        manager.update(subtask2, subtask2.getId());
        assertEquals(Status.IN_PROGRESS, epic.getStatus());
    }
}