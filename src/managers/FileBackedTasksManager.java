package managers;

import exceptions.ManagerLoadException;
import exceptions.ManagerSaveException;
import tasks.*;

import java.io.*;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class FileBackedTasksManager extends InMemoryTaskManager {

    private File file;

    public FileBackedTasksManager(File file) {
        super();
        this.file = file;
    }

    void save(){
        try(BufferedWriter bw = new BufferedWriter(new FileWriter(file))){

            bw.write("id,type,name,status,description,epic");
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
            bw.write(historyToString(historyManager));

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
                    List<Integer> historyCsv = historyListFromString(lines[i+1]);
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

    public static String historyToString(HistoryManager historyManager){
        if (historyManager.getHistory().isEmpty()){
            return "";
        }

        StringBuilder sb = new StringBuilder();
        for (AbstractTask task : historyManager.getHistory()){
            sb.append(task.getId());
            sb.append(",");
        }
        sb.deleteCharAt(sb.lastIndexOf(","));
        return sb.toString();
    }

    public static List<Integer> historyListFromString(String history){
        ArrayList<Integer> historyList = new ArrayList<>();
        if (history.equals("")){
            return historyList;
        }
        String[] values = history.split(",");
        for (int i = values.length-1; i >= 0; i--){
            historyList.add(Integer.valueOf(values[i]));
        }
        return historyList;
    }

    public Epic epicFromCsv(String[] values) {
        return new Epic(values[0], values[2], values[3], values[4]);
    }

    public Task taskFromCsv(String[] values) {
        return new Task(values[0], values[2], values[3], values[4]);
    }

    public Subtask subtaskFromCsv(String[] values) {
        return new Subtask(values[0], values[2], values[3], values[4], epics.get(Integer.valueOf(values[5])));
    }

    private void objectiveFromCsv(String[] values){
        switch (TaskType.valueOf(values[1])){
            case TASK: tasks.put(Integer.valueOf(values[0]), taskFromCsv(values));
            break;
            case EPIC: epics.put(Integer.valueOf(values[0]), epicFromCsv(values));
            break;
            case SUBTASK: subtasks.put(Integer.valueOf(values[0]), subtaskFromCsv(values));
            break;
        }
    }

    public static void main(String[] args) {

        File file = new File("src/resources/save1.txt");
        FileBackedTasksManager manager1 = Managers.detDefaultFileBacked(file);

        manager1.addObjective(new Task("Task0", "description"), TaskType.TASK);
        manager1.addObjective(new Task("Task1", "description"), TaskType.TASK);
        manager1.addObjective(new Task("Task2", "description"), TaskType.TASK);

        manager1.addObjective(new Epic("Epic3", "description"), TaskType.EPIC);
        manager1.addObjective(new Epic("Epic4", "description"), TaskType.EPIC);

        manager1.addObjective(new Subtask("Subtask5", "description",
                (Epic) manager1.getById(4, TaskType.EPIC)),TaskType.SUBTASK);

        manager1.getById(0,TaskType.TASK);
        manager1.getById(1,TaskType.TASK);
        manager1.getById(2,TaskType.TASK);

        FileBackedTasksManager manager2 = FileBackedTasksManager.loadFromFile(file);
        manager2.getById(3,TaskType.EPIC);
        manager2.getById(4,TaskType.EPIC);
        manager2.getById(5,TaskType.SUBTASK);
        manager2.addObjective(new Task("Task6", "description"), TaskType.TASK);
        manager2.getById(6,TaskType.TASK);
    }
}

