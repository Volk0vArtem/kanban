import Manager.InMemoryTaskManager;
import Tasks.*;

public class Main {

    public static void main(String[] args) {
        InMemoryTaskManager inMemoryTaskManager = new InMemoryTaskManager();

        inMemoryTaskManager.addObjective(new Task("Приготовить завтрак", "Приготовить яичницу на завтрак"),
                TaskType.TASK);

        inMemoryTaskManager.addObjective(new Epic("Переезд", "Переехать в новую квартиру"), TaskType.EPIC);
        inMemoryTaskManager.addObjective(new Subtask("Упаковать вещи", "Собрать вещи в коробки",
                (Epic) inMemoryTaskManager.getById(1, TaskType.EPIC)), TaskType.SUBTASK);
        inMemoryTaskManager.addObjective(new Subtask("Перевезти вещи", "Заказать доставку вещей в новую квартиру",
                (Epic) inMemoryTaskManager.getById(1, TaskType.EPIC)), TaskType.SUBTASK);

        inMemoryTaskManager.addObjective(new Epic("Купить ноутбук", "Купить новый ноутбук"),
                TaskType.EPIC);
        inMemoryTaskManager.addObjective(new Subtask("Сходить в магазин электроники",
                        "Прийти в магазин и выбрать ноутбук", (Epic) inMemoryTaskManager.getById(4, TaskType.EPIC)),
                TaskType.SUBTASK);

        printAllObjectives(inMemoryTaskManager);

        System.out.println("\n------------------------\nИзменение статусов задач\n------------------------");
        inMemoryTaskManager.update(inMemoryTaskManager.getTasks().get(0).changeStatus(Status.IN_PROGRESS), 0);
        inMemoryTaskManager.update(inMemoryTaskManager.getSubtasks().get(2).changeStatus(Status.IN_PROGRESS), 2);
        inMemoryTaskManager.update(inMemoryTaskManager.getSubtasks().get(3).changeStatus(Status.DONE), 3);
        inMemoryTaskManager.update(inMemoryTaskManager.getSubtasks().get(5).changeStatus(Status.DONE), 5);

        printAllObjectives(inMemoryTaskManager);

        System.out.println("\n---------------\nУдаление задач\n---------------");
        inMemoryTaskManager.deleteById(2, TaskType.SUBTASK);
        inMemoryTaskManager.clearTasks();
        inMemoryTaskManager.deleteById(4, TaskType.EPIC);
        printAllObjectives(inMemoryTaskManager);
    }

    public static void printAllObjectives(InMemoryTaskManager inMemoryTaskManager) {
        System.out.println("\nСписок задач:");
        for (Task task : inMemoryTaskManager.getTasks().values()) {
            System.out.println("\t" + task.toString());
        }

        System.out.println("\nСписок эпиков:");
        for (Epic epic : inMemoryTaskManager.getEpics().values()) {
            System.out.println("\t" + epic.toString());
        }

        System.out.println("\nСписок подзадач:");
        for (Subtask subtask : inMemoryTaskManager.getSubtasks().values()) {
            System.out.println("\t" + subtask.toString());
        }
    }
}
