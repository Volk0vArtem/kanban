package managers;

import exceptions.ManagerLoadException;
import exceptions.ManagerSaveException;
import exceptions.TimeIntersectException;
import tasks.*;
import utils.CSVFormat;

import java.io.*;
import java.nio.file.Files;
import java.util.List;

public class FileBackedTasksManager extends InMemoryTaskManager {

    private File file;

    public FileBackedTasksManager() {
        super();
    }

    public FileBackedTasksManager(File file) {
        super();
        this.file = file;
    }

    protected void save() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(file))) {

            bw.write("id,type,name,status,description,startTime,endTime,duration,epic");
            bw.newLine();

            for (Task task : tasks.values()) {
                String csv = task.toCSV();
                bw.write(csv);
                bw.newLine();
            }

            for (Epic epic : epics.values()) {
                String csv = epic.toCSV();
                bw.write(csv);
                bw.newLine();
            }

            for (Subtask subtask : subtasks.values()) {
                String csv = subtask.toCSV();
                bw.write(csv);
                bw.newLine();
            }

            bw.newLine();
            bw.write(CSVFormat.historyToString(historyManager));
            bw.newLine();
            bw.write(CSVFormat.prioritizedTasksToCsv(this));

        } catch (IOException e) {
            throw new ManagerSaveException("Can't save to file: " + file.getName());
        }
    }

    public static FileBackedTasksManager loadFromFile(File file) {
        FileBackedTasksManager manager = new FileBackedTasksManager(file);
        try {
            String csv = Files.readString(file.toPath());
            String[] lines = csv.split(System.lineSeparator());
            int maxId = 0;

            for (int i = 1; i < lines.length; i++) {
                if (lines[i].isEmpty()) {
                    List<Integer> historyCsv = CSVFormat.idListFromString(lines[i + 1]);
                    HistoryManager historyManager = manager.getHistoryManager();
                    for (Integer id : historyCsv) {
                        historyManager.addToHistory(manager.getByIdWithoutAddingToHistory(id));
                    }

                    List<Integer> prioritizedCsv = CSVFormat.idListFromString(lines[i + 2]);
                    for (int j = prioritizedCsv.size() - 1; j >= 0; j--) {
                        try {
                            manager.addToPrioritizedTasksList(manager.getByIdWithoutAddingToHistory(prioritizedCsv.get(j)));
                        } catch (TimeIntersectException e) {
                            System.out.println(e.getMessage());
                        }
                    }
                    break;
                }

                String[] scvData = lines[i].split(",");
                if (Integer.parseInt(scvData[0]) >= maxId) {
                    maxId = Integer.parseInt(scvData[0]) + 1;
                    AbstractTask.setCount(maxId);
                }
                manager.objectiveFromCsv(scvData);
            }
        } catch (IOException e) {
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

    private void objectiveFromCsv(String[] values) {
        switch (TaskType.valueOf(values[1])) {
            case TASK:
                tasks.put(Integer.valueOf(values[0]), CSVFormat.taskFromCsv(values));
                break;
            case EPIC:
                epics.put(Integer.valueOf(values[0]), CSVFormat.epicFromCsv(values));
                break;
            case SUBTASK: {
                Subtask subtask = CSVFormat.subtaskFromCsv(values, epics);
                subtasks.put(Integer.valueOf(values[0]), subtask);
                subtask.getEpic().addSubtask(subtask);
            }
            break;
        }
    }
}