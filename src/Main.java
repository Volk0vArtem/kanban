import managers.*;
import tasks.*;

public class Main {

    public static void main(String[] args) {
        TaskManager inMemoryTaskManager = Managers.getDefault();

        inMemoryTaskManager.addObjective(new Task("Приготовить завтрак",
                        "Приготовить яичницу на завтрак"), TaskType.TASK);
        printHistory(inMemoryTaskManager);

        inMemoryTaskManager.addObjective(new Epic("Переезд",
                "Переехать в новую квартиру"), TaskType.EPIC);
        printHistory(inMemoryTaskManager);

        inMemoryTaskManager.addObjective(new Subtask("Упаковать вещи", "Собрать вещи в коробки",
                (Epic) inMemoryTaskManager.getById(1, TaskType.EPIC)), TaskType.SUBTASK);
        printHistory(inMemoryTaskManager);

        inMemoryTaskManager.addObjective(new Subtask("Перевезти вещи",
                "Заказать доставку вещей в новую квартиру",
                (Epic) inMemoryTaskManager.getById(1, TaskType.EPIC)), TaskType.SUBTASK);
        printHistory(inMemoryTaskManager);

        inMemoryTaskManager.addObjective(new Epic("Купить ноутбук",
                "Купить новый ноутбук"), TaskType.EPIC);
        printHistory(inMemoryTaskManager);

        inMemoryTaskManager.addObjective(new Subtask("Сходить в магазин электроники",
                        "Прийти в магазин и выбрать ноутбук",
                        (Epic) inMemoryTaskManager.getById(4, TaskType.EPIC)), TaskType.SUBTASK);
        printHistory(inMemoryTaskManager);

        inMemoryTaskManager.update(inMemoryTaskManager.getById(0, TaskType.TASK)
                .changeStatus(Status.IN_PROGRESS), 0);
        printHistory(inMemoryTaskManager);

        inMemoryTaskManager.update(inMemoryTaskManager.getById(2, TaskType.SUBTASK)
                .changeStatus(Status.IN_PROGRESS), 2);
        printHistory(inMemoryTaskManager);

        inMemoryTaskManager.update(inMemoryTaskManager.getById(3,
                TaskType.SUBTASK).changeStatus(Status.IN_PROGRESS), 3);
        printHistory(inMemoryTaskManager);

        inMemoryTaskManager.update(inMemoryTaskManager.getById(5,
                TaskType.SUBTASK).changeStatus(Status.IN_PROGRESS), 5);
        printHistory(inMemoryTaskManager);

        inMemoryTaskManager.update(inMemoryTaskManager.getById(2,
                TaskType.SUBTASK).changeStatus(Status.DONE), 2);
        printHistory(inMemoryTaskManager);

        inMemoryTaskManager.update(inMemoryTaskManager.getById(3,
                TaskType.SUBTASK).changeStatus(Status.DONE), 3);
        printHistory(inMemoryTaskManager);

        inMemoryTaskManager.update(inMemoryTaskManager.getById(5,
                TaskType.SUBTASK).changeStatus(Status.DONE), 5);
        printHistory(inMemoryTaskManager);

        inMemoryTaskManager.update(inMemoryTaskManager.getById(5,
                TaskType.SUBTASK).changeStatus(Status.DONE), 5);
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
