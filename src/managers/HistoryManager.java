package managers;

import tasks.AbstractTask;

import java.util.ArrayList;

public interface HistoryManager {
    ArrayList<AbstractTask> getHistory();

    void addToHistory(AbstractTask task);
}
