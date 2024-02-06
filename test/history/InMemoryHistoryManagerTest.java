package history;

import managers.HistoryManager;
import managers.InMemoryHistoryManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.AbstractTask;
import tasks.Epic;
import tasks.Task;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {
    HistoryManager historyManager;
    Task task1;
    Task task2;
    Epic epic;

    @BeforeEach
    void setUp(){
        AbstractTask.countReset();
        historyManager = new InMemoryHistoryManager();
        epic = new Epic("task", "0");
        task1 = new Task("task", "1");
        task2 = new Task("task", "2");
    }

    @Test
    void emptyHistory(){
        assertEquals(0, historyManager.getHistory().size());
    }

    @Test
    void addToHistory(){
        historyManager.addToHistory(task1);
        assertEquals(1, historyManager.getHistory().size(),"Задача не добавилась");
        assertEquals(task1, historyManager.getHistory().get(0),"Задачи не совпадают");

        historyManager.addToHistory(epic);
        assertEquals(2, historyManager.getHistory().size(),"Вторая задача не добавилась");
        assertEquals(epic, historyManager.getHistory().get(0),"Неверный порядок задач");
        assertEquals(task1, historyManager.getHistory().get(1),"Неверный порядок задач");
    }

    @Test
    void duplicatedTask(){
        historyManager.addToHistory(task1);
        historyManager.addToHistory(task1);
        assertEquals(1, historyManager.getHistory().size(),"Задача дублировалась в историю");
        assertEquals(task1, historyManager.getHistory().get(0),"Задачи не совпадают");
    }

    @Test
    void remove(){
        historyManager.addToHistory(epic);
        historyManager.addToHistory(task1);
        historyManager.addToHistory(task2);

        historyManager.remove(2);
        assertEquals(2, historyManager.getHistory().size(), "Задача не удалилась из начала");
        assertEquals(epic, historyManager.getHistory().get(1), "Задачи не совпадают");
        assertEquals(task1, historyManager.getHistory().get(0), "Задачи не совпадают");

        historyManager.addToHistory(task2);
        historyManager.remove(1);
        assertEquals(2, historyManager.getHistory().size(), "Задача не удалилась из середины");
        assertEquals(epic, historyManager.getHistory().get(1), "Задачи не совпадают");
        assertEquals(task2, historyManager.getHistory().get(0), "Задачи не совпадают");

        historyManager.addToHistory(task1);
        historyManager.remove(0);
        assertEquals(2, historyManager.getHistory().size(), "Задача не удалилась из конца");
        assertEquals(task1, historyManager.getHistory().get(0), "Задачи не совпадают");
        assertEquals(task2, historyManager.getHistory().get(1), "Задачи не совпадают");
    }
}