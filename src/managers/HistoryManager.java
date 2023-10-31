package managers;

import tasks.AbstractTask;

import java.util.ArrayList;
import java.util.List;

public interface HistoryManager {
    List<AbstractTask> getHistory();

    void addToHistory(AbstractTask task);
}
