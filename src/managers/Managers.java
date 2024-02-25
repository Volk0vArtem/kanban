package managers;

import java.io.File;

public class Managers {

    public static InMemoryTaskManager getDefault() {
        return new InMemoryTaskManager();
    }

    public static HistoryManager getDefaultHistory(){
        return new InMemoryHistoryManager();
    }

    public static FileBackedTasksManager detDefaultFileBacked(File file){
        return new FileBackedTasksManager(file);
    }
    public static HttpTaskManager getHttpManager(String url){
        return new HttpTaskManager(url);
    }
}
