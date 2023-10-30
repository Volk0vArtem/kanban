package managers;

import tasks.AbstractTask;

import java.util.ArrayList;

public class InMemoryHistoryManager implements HistoryManager{
    private ArrayList<AbstractTask> history;

    public InMemoryHistoryManager() {
        history = new ArrayList<>();
    }

    @Override
    public ArrayList<AbstractTask> getHistory() {
        return history;
    }

    @Override
    public void addToHistory(AbstractTask task) {
        history.add(task);
        if (history.size() > 10){
            history.remove(0);
        }
    }
}
