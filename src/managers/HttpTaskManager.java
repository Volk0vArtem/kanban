package managers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import httpServer.KVTaskClient;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;
import utils.AbstractTaskDeserializer;
import utils.AbstractTaskSerializer;
import utils.ManagerDeserializer;
import utils.ManagerSerializer;

import java.io.IOException;

public class HttpTaskManager extends FileBackedTasksManager {

    KVTaskClient client;

    public void setClient(KVTaskClient client) {
        this.client = client;
    }

    public HttpTaskManager(String url) {
        super();
        try {
            client = new KVTaskClient(url);
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public String getUrl() {
        return client.getUrl();
    }

    @Override
    protected void save() {
        Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .registerTypeAdapter(Epic.class, AbstractTaskSerializer.getEpicSerializer())
                .registerTypeAdapter(Task.class, AbstractTaskSerializer.getTaskSerializer())
                .registerTypeAdapter(Subtask.class, AbstractTaskSerializer.getSubtaskSerializer())
                .registerTypeAdapter(HttpTaskManager.class, new ManagerSerializer())
                .create();
        String json = gson.toJson(this);
        try {
            client.put("save", json);
        } catch (Exception e) {
            System.out.println("Во время сохранения возникла ошибка");
        }
    }

    public static HttpTaskManager load(String url) {
        Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .registerTypeAdapter(Epic.class, AbstractTaskDeserializer.getEpicDeserializer())
                .registerTypeAdapter(Task.class, AbstractTaskDeserializer.getTaskDeserializer())
                .registerTypeAdapter(Subtask.class, AbstractTaskDeserializer.getSubtaskDeserializer())
                .registerTypeAdapter(HttpTaskManager.class, new ManagerDeserializer())
                .create();
        try {
            KVTaskClient client1 = new KVTaskClient(url);
            String json = client1.load("save");
            HttpTaskManager manager = gson.fromJson(json, HttpTaskManager.class);
            manager.setClient(client1);
            return manager;
        } catch (Exception e) {
            System.out.println("Во время загрузки возникла ошибка");
            return null;
        }
    }
}
