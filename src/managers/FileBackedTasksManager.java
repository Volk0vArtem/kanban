package managers;

import exceptions.ManagerLoadException;
import exceptions.ManagerSaveException;
import tasks.*;
import utils.CSVFormat;

import java.io.*;
import java.nio.file.Files;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class FileBackedTasksManager extends InMemoryTaskManager {

    private final File file;

    public FileBackedTasksManager(File file) {
        super();
        this.file = file;
    }

    private void save(){
        try(BufferedWriter bw = new BufferedWriter(new FileWriter(file))){

            bw.write("id,type,name,status,description,startTime,endTime,duration,epic");
            bw.newLine();

            for (Task task : tasks.values()){
                String csv = task.toCSV();
                bw.write(csv);
                bw.newLine();
            }

            for (Epic epic : epics.values()){
                String csv = epic.toCSV();
                bw.write(csv);
                bw.newLine();
            }

            for (Subtask subtask : subtasks.values()){
                String csv = subtask.toCSV();
                bw.write(csv);
                bw.newLine();
            }

            bw.newLine();
            bw.write(CSVFormat.historyToString(historyManager));

        } catch (IOException e){
            throw new ManagerSaveException("Can't save to file: " + file.getName());
        }
    }

    public static FileBackedTasksManager loadFromFile(File file){
        FileBackedTasksManager manager = new FileBackedTasksManager(file);
        try{
            String csv = Files.readString(file.toPath());
            String[] lines = csv.split(System.lineSeparator());
            int maxId = 0;

            for (int i = 1; i < lines.length; i++){
                if (lines[i].isEmpty()){
                    List<Integer> historyCsv = CSVFormat.historyListFromString(lines[i+1]);
                    HistoryManager historyManager = manager.getHistoryManager();
                    for (Integer id : historyCsv){
                        historyManager.addToHistory(manager.getById(id));
                    }
                    break;
                }

                String[] scvData = lines[i].split(",");
                if (Integer.parseInt(scvData[0]) >= maxId){
                    maxId = Integer.parseInt(scvData[0]) + 1;
                    AbstractTask.setCount(maxId);
                }
                manager.objectiveFromCsv(scvData);
            }
        } catch (IOException e){
            throw new ManagerLoadException("Can't load from file: " + file.getName());
        }
        return manager;
    }

    @Override
    public void addObjective(AbstractTask abstractTask, TaskType taskType) {
        super.addObjective(abstractTask, taskType);
        save();
    }

    @Override
    public void deleteById(int id, TaskType taskType) {
        super.deleteById(id, taskType);
        save();
    }

    @Override
    public void clearTasks() {
        super.clearTasks();
        save();
    }

    @Override
    public void clearEpics() {
        super.clearEpics();
        save();
    }

    @Override
    public void clearSubtasks() {
        super.clearSubtasks();
        save();
    }

    @Override
    public AbstractTask getById(int id, TaskType type) {
        AbstractTask abstractTask = super.getById(id, type);
        save();
        return abstractTask;

    }

    private void objectiveFromCsv(String[] values){
        switch (TaskType.valueOf(values[1])){
            case TASK: tasks.put(Integer.valueOf(values[0]), CSVFormat.taskFromCsv(values));
            break;
            case EPIC: epics.put(Integer.valueOf(values[0]), CSVFormat.epicFromCsv(values));
            break;
            case SUBTASK: {
                Subtask subtask = CSVFormat.subtaskFromCsv(values,epics);
                subtasks.put(Integer.valueOf(values[0]), subtask);
                subtask.getEpic().addSubtask(subtask);
            }
            break;
        }
    }

    public static void main(String[] args) {

        /*
        Epic epic = new Epic("new Epic", "Новый Эпик");
        FileBackedTasksManager fileManager = new FileBackedTasksManager(new File("saveTasks2.csv"));
        fileManager.addObjective(epic, TaskType.EPIC);
        fileManager.addObjective(new Task("task1", "Купить автомобиль"), TaskType.TASK);
        fileManager.addObjective(new Epic("new Epic2", "Новый Эпик2"), TaskType.EPIC);
        fileManager.addObjective(new Subtask("New Subtask", "Подзадача", (Epic) fileManager.getById(0, TaskType.EPIC)), TaskType.SUBTASK);
        fileManager.addObjective(new Subtask("New Subtask2", "Подзадача2", (Epic) fileManager.getById(0, TaskType.EPIC)), TaskType.SUBTASK);
        fileManager.getById(1, TaskType.TASK);
        fileManager.getById(2, TaskType.EPIC);
        fileManager.getById(3, TaskType.SUBTASK);
        System.out.println(fileManager.getTasks());
        System.out.println(fileManager.getEpics());
        System.out.println(fileManager.getSubtasks());
        System.out.println(fileManager.getHistory());
        System.out.println("\n\n" + "new" + "\n\n");
        FileBackedTasksManager fileBackedTasksManager = loadFromFile(new File("saveTasks2.csv"));
        System.out.println(fileBackedTasksManager.getTasks());
        System.out.println(fileBackedTasksManager.getEpics());
        System.out.println(fileBackedTasksManager.getSubtasks());
        System.out.println(fileBackedTasksManager.getHistory());


         */
        FileBackedTasksManager manager = new FileBackedTasksManager(new File("saveTasks2.csv"));

        Task task1 = new Task("task1", "0",
                LocalDateTime.of(2001,1,1,10,0),Duration.ofHours(2));
        Task task2 = new Task("task2", "1",
                LocalDateTime.of(2000, 3,14,7,0), Duration.ofHours(3));
        Epic epic1 = new Epic("epic1", "2");
        Task task3 = new Task("task3", "3");
        Subtask subtask1 = new Subtask("subtask1", "4", epic1,
                LocalDateTime.of(1990,9,10,11,0), Duration.ofHours(3));
        Subtask subtask2 = new Subtask("subtask2", "5", epic1,
                LocalDateTime.of(1980,9,10,11,0), Duration.ofHours(3));
        Subtask subtask3 = new Subtask("subtask3", "6", epic1,
                LocalDateTime.of(1992,9,10,11,0), Duration.ofHours(3));


        //System.out.println(manager.getPrioritizedTasks().size() + "\n");

        manager.addObjective(task1, TaskType.TASK);
        //System.out.println(manager.getPrioritizedTasks().size() + "\n");
        manager.addObjective(task3, TaskType.TASK);
        manager.addObjective(epic1, TaskType.EPIC);
        manager.addObjective(task2, TaskType.TASK);
        manager.addObjective(subtask1, TaskType.SUBTASK);
        manager.addObjective(subtask2, TaskType.SUBTASK);
        manager.addObjective(subtask3, TaskType.SUBTASK);

        manager.getPrioritizedTasks().forEach(System.out::println);

        }
}