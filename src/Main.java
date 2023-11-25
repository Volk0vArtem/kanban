import managers.*;
import tasks.*;

public class Main {

    public static void main(String[] args) {
        TaskManager inMemoryTaskManager = Managers.getDefault();


        printHistory(inMemoryTaskManager);
        inMemoryTaskManager.addObjective(new Task("Приготовить завтрак",
                "Приготовить яичницу на завтрак"), TaskType.TASK);
        inMemoryTaskManager.addObjective(new Task("Сходить в магазин",
                "Купить хлеб"), TaskType.TASK);
        inMemoryTaskManager.addObjective(new Epic("Переезд",
                        "Переехать в новую квартиру"), TaskType.EPIC);
        inMemoryTaskManager.addObjective(new Subtask("Упаковать вещи", "Собрать вещи в коробки",
                (Epic) inMemoryTaskManager.getById(2, TaskType.EPIC)), TaskType.SUBTASK);
        inMemoryTaskManager.addObjective(new Subtask("Перевезти вещи",
                "Заказать доставку вещей в новую квартиру",
                (Epic) inMemoryTaskManager.getById(2, TaskType.EPIC)), TaskType.SUBTASK);
        inMemoryTaskManager.addObjective(new Subtask("Забрать кота", "Посадить кота в переноску",
                (Epic) inMemoryTaskManager.getById(2, TaskType.EPIC)), TaskType.SUBTASK);
        inMemoryTaskManager.addObjective(new Epic("Купить ноутбук", "Купить новый ноутбук"),
                TaskType.EPIC);

        printHistory(inMemoryTaskManager);

        inMemoryTaskManager.getById(0, TaskType.TASK);
        printHistory(inMemoryTaskManager);
        inMemoryTaskManager.getById(1, TaskType.TASK);
        printHistory(inMemoryTaskManager);
        inMemoryTaskManager.getById(2, TaskType.EPIC);
        printHistory(inMemoryTaskManager);
        inMemoryTaskManager.getById(3, TaskType.SUBTASK);
        printHistory(inMemoryTaskManager);
        inMemoryTaskManager.getById(4, TaskType.SUBTASK);
        printHistory(inMemoryTaskManager);
        inMemoryTaskManager.getById(5, TaskType.SUBTASK);
        printHistory(inMemoryTaskManager);
        inMemoryTaskManager.getById(6, TaskType.EPIC);
        printHistory(inMemoryTaskManager);

        inMemoryTaskManager.getById(3, TaskType.SUBTASK);
        printHistory(inMemoryTaskManager);
        inMemoryTaskManager.getById(1, TaskType.TASK);
        printHistory(inMemoryTaskManager);
        inMemoryTaskManager.getById(2, TaskType.EPIC);
        printHistory(inMemoryTaskManager);
        inMemoryTaskManager.getById(0, TaskType.TASK);
        printHistory(inMemoryTaskManager);

        inMemoryTaskManager.deleteById(0,TaskType.TASK);
        printHistory(inMemoryTaskManager);

        inMemoryTaskManager.deleteById(3,TaskType.SUBTASK);
        printHistory(inMemoryTaskManager);

        inMemoryTaskManager.deleteById(2,TaskType.EPIC);
        printHistory(inMemoryTaskManager);
    }

    public static void printAllObjectives(TaskManager TaskManager) {
        System.out.println("\nСписок задач:");
        for (Task task : TaskManager.getTasks().values()) {
            System.out.println("\t" + task.toString());
        }

        System.out.println("\nСписок эпиков:");
        for (Epic epic : TaskManager.getEpics().values()) {
            System.out.println("\t" + epic.toString());
        }

        System.out.println("\nСписок подзадач:");
        for (Subtask subtask : TaskManager.getSubtasks().values()) {
            System.out.println("\t" + subtask.toString());
        }
    }

    public static void printHistory(TaskManager taskManager) {

        if (taskManager.getHistory().size() == 0) {
            System.out.println("\nИстория пуста");
            return;
        }
        System.out.println("\n История просмотра:");
        for (AbstractTask a : taskManager.getHistory()) {
            System.out.println(a);
        }
        System.out.println("Размер списка просмотренных задач: " + taskManager.getHistory().size());
    }
}
