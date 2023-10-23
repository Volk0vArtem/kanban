public class Main {

    public static void main(String[] args) {
        Manager manager = new Manager();

        manager.addObjective(new Task("Приготовить завтрак", "Приготовить яичницу на завтрак"),
                TaskType.TASK);

        manager.addObjective(new Epic("Переезд", "Переехать в новую квартиру"), TaskType.EPIC);
        manager.addObjective(new Subtask("Упаковать вещи", "Собрать вещи в коробки",
                (Epic) manager.getById(0, TaskType.EPIC)), TaskType.SUBTASK);
        manager.addObjective(new Subtask("Перевезти вещи", "Заказать доставку вещей в новую квартиру",
                (Epic) manager.getById(0, TaskType.EPIC)), TaskType.SUBTASK);

        manager.addObjective(new Epic("Купить ноутбук", "Купить новый ноутбук"),
                TaskType.EPIC);
        manager.addObjective(new Subtask("Сходить в магазин электроники",
                        "Прийти в магазин и выбрать ноутбук", (Epic) manager.getById(1, TaskType.EPIC)),
                TaskType.SUBTASK);

        printAllObjectives(manager);

        System.out.println("\n------------------------\nИзменение статусов задач\n------------------------");
        manager.update(manager.getTasks().get(0).changeStatus(Status.IN_PROGRESS), 0);
        manager.update(manager.getSubtasks().get(0).changeStatus(Status.IN_PROGRESS), 0);
        manager.update(manager.getSubtasks().get(1).changeStatus(Status.DONE), 1);
        manager.update(manager.getSubtasks().get(2).changeStatus(Status.DONE), 2);

        printAllObjectives(manager);

        System.out.println("\n---------------\nУдаление задач\n---------------");
        manager.deleteById(1, TaskType.SUBTASK);
        manager.clearTasks();
        manager.deleteById(1, TaskType.EPIC);
        printAllObjectives(manager);
    }

    public static void printAllObjectives(Manager manager) {
        System.out.println("\nСписок задач:");
        for (Task task : manager.getTasks().values()) {
            System.out.println("\t" + task.toString());
        }

        System.out.println("\nСписок эпиков:");
        for (Epic epic : manager.getEpics().values()) {
            System.out.println("\t" + epic.toString());
        }

        System.out.println("\nСписок подзадач:");
        for (Subtask subtask : manager.getSubtasks().values()) {
            System.out.println("\t" + subtask.toString());
        }
    }
}
