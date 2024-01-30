package managers;

import exceptions.ManagerLoadException;
import exceptions.ManagerSaveException;
import tasks.*;
import utils.CSVFormat;

import java.io.*;
import java.nio.file.Files;
import java.time.Duration;
import java.time.LocalDateTime;
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
        FileBackedTasksManager fileManager = new FileBackedTasksManager(new File("saveTasks2.csv"));
        Epic epic = new Epic("epic", "0");
        Task task = new Task("task", "1",
                LocalDateTime.of(2024,1,1,10, 0), Duration.ofHours(1));
        Task task2 = new Task("task", "2",
                LocalDateTime.of(2024,1,2,10, 0), Duration.ofHours(2));
        Subtask subtask = new Subtask("subtask", "3", epic,
                LocalDateTime.of(2024,1,22,15, 0), Duration.ofHours(5));
        Subtask subtask2 = new Subtask("subtask", "3", epic,
                LocalDateTime.of(2024,1,23,15, 0), Duration.ofHours(4));

        fileManager.addObjective(epic, TaskType.EPIC);
        fileManager.addObjective(task, TaskType.TASK);
        fileManager.addObjective(task2, TaskType.TASK);
        fileManager.addObjective(subtask, TaskType.SUBTASK);
        fileManager.addObjective(subtask2, TaskType.SUBTASK);

        fileManager.getById(0, TaskType.EPIC);
        fileManager.getById(1, TaskType.TASK);
        fileManager.getById(2, TaskType.TASK);
        fileManager.getById(3, TaskType.SUBTASK);
        fileManager.getById(4, TaskType.SUBTASK);

        FileBackedTasksManager fileManager2 = FileBackedTasksManager.loadFromFile(new File("saveTasks2.csv"));

        System.out.println(fileManager.getTasks().equals(fileManager2.getTasks()));
        System.out.println(fileManager.getSubtasks().equals(fileManager2.getSubtasks()));
        System.out.println(fileManager.getEpics().equals(fileManager2.getEpics()));
        System.out.println(fileManager.getHistory().equals(fileManager2.getHistory()));

    }
}