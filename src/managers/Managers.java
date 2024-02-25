package managers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;
import utils.AbstractTaskDeserializer;
import utils.ManagerDeserializer;

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


//    public static HttpTaskManager loadFromServer(){
//        Gson gson = new GsonBuilder()
//                .setPrettyPrinting()
//                .registerTypeAdapter(Epic.class, AbstractTaskDeserializer.getEpicDeserializer())
//                .registerTypeAdapter(Task.class, AbstractTaskDeserializer.getTaskDeserializer())
//                .registerTypeAdapter(Subtask.class, AbstractTaskDeserializer.getSubtaskDeserializer())
//                .registerTypeAdapter(HttpTaskManager.class, new ManagerDeserializer())
//                .create();
//        try {
//            String json = client.load("save");
//            HttpTaskManager manager = gson.fromJson(json, HttpTaskManager.class);
//            return manager;
//        } catch (Exception e){
//            System.out.println("Во время загрузки возникла ошибка");
//            return null;
//        }
//
//}
}
