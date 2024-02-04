package managers;

import exceptions.ManagerLoadException;
import exceptions.ManagerSaveException;
import exceptions.TimeIntersectException;
import tasks.*;
import utils.CSVFormat;

import java.io.*;
import java.nio.file.Files;
import java.sql.Time;
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
            bw.newLine();
            bw.write(CSVFormat.prioritizedTasksToCsv(this));

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
                    List<Integer> historyCsv = CSVFormat.idListFromString(lines[i+1]);
                    HistoryManager historyManager = manager.getHistoryManager();
                    for (Integer id : historyCsv){
                        historyManager.addToHistory(manager.getById(id));
                    }

                    List<Integer> prioritizedCsv = CSVFormat.idListFromString(lines[i+2]);
                    for (int j = prioritizedCsv.size()-1; j >= 0; j--) {
                        try {
                            manager.addToPrioritizedTasksList(manager.getById(prioritizedCsv.get(j)));
                        } catch (TimeIntersectException e){
                            System.out.println(e.getMessage());
                        }
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

        FileBackedTasksManager manager = new FileBackedTasksManager(new File("saveTasks2.csv"));

        Task task1 = new Task("TaskName", "TaskDescription",
                LocalDateTime.of(2000,01,01,15,00), Duration.ofHours(1));
        Epic epic1 = new Epic("EpicName", "EpicDescription");
        Subtask subtask1 = new Subtask("SubtaskName", "SubtaskDescription", epic1,
                LocalDateTime.of(2002,01,01,15,00), Duration.ofHours(1));
        Subtask subtask2 = new Subtask("subtask2", "5", epic1,
                LocalDateTime.of(2001,1,1,11,0), Duration.ofHours(3));
//        Task task2 = new Task("TaskName", "TaskDescription",
//                LocalDateTime.of(2000,01,01,15,30), Duration.ofHours(20));

        manager.addObjective(task1, TaskType.TASK);
        manager.addObjective(epic1, TaskType.EPIC);
        manager.addObjective(subtask1, TaskType.SUBTASK);
        manager.addObjective(subtask2, TaskType.SUBTASK);
        //manager.addObjective(task2, TaskType.TASK);

        manager.getPrioritizedTasks().forEach(System.out::println);

        System.out.println("\n\n");
        TaskManager manager1 = FileBackedTasksManager.loadFromFile(new File("saveTasks2.csv"));
        manager1.getPrioritizedTasks().forEach(System.out::println);

        }
}