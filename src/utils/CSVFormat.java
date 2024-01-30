package utils;

import managers.HistoryManager;
import tasks.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CSVFormat {

    public static String historyToString(HistoryManager historyManager) {
        if (historyManager.getHistory().isEmpty()) {
            return "";
        }

        StringBuilder sb = new StringBuilder();
        for (AbstractTask task : historyManager.getHistory()) {
            sb.append(task.getId());
            sb.append(",");
        }
        sb.deleteCharAt(sb.lastIndexOf(","));
        return sb.toString();
    }

    public static List<Integer> historyListFromString(String history) {
        ArrayList<Integer> historyList = new ArrayList<>();
        if (history.equals("")) {
            return historyList;
        }
        String[] values = history.split(",");
        for (int i = values.length - 1; i >= 0; i--) {
            historyList.add(Integer.valueOf(values[i]));
        }
        return historyList;

    }

    public static Epic epicFromCsv(String[] values) {
        return new Epic(values[0], values[2], values[3], values[4],values[5], values[6], values[7]);
    }

    public static Task taskFromCsv(String[] values) {
        return new Task(values[0], values[2], values[3], values[4], values[5], values[6], values[7]);
    }

    public static Subtask subtaskFromCsv(String[] values, HashMap<Integer, Epic> epics) {
        return new Subtask(values[0], values[2], values[3], values[4], epics.get(Integer.valueOf(values[8])),
                values[5], values[6], values[7]);
    }
}
