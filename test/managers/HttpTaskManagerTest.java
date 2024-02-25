package managers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import httpServer.HttpTaskServer;
import httpServer.KVServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.*;
import utils.AbstractTaskDeserializer;
import utils.AbstractTaskSerializer;
import utils.ManagerDeserializer;
import utils.ManagerSerializer;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class HttpTaskManagerTest {

    KVServer kvServer;
    HttpTaskServer server;
    static Gson gson;
    HttpClient client;
    static Task task0;
    static Task task1;
    static Epic epic2;
    static Subtask subtask3;
    static Subtask subtask4;
    String urlString = "http://localhost:8080/tasks";


    static {
        gson = new GsonBuilder()
                .setPrettyPrinting()
                .registerTypeAdapter(Epic.class, AbstractTaskSerializer.getEpicSerializer())
                .registerTypeAdapter(Task.class, AbstractTaskSerializer.getTaskSerializer())
                .registerTypeAdapter(Subtask.class, AbstractTaskSerializer.getSubtaskSerializer())
                .registerTypeAdapter(HttpTaskManager.class, new ManagerSerializer())
                .registerTypeAdapter(Epic.class, AbstractTaskDeserializer.getEpicDeserializer())
                .registerTypeAdapter(Task.class, AbstractTaskDeserializer.getTaskDeserializer())
                .registerTypeAdapter(Subtask.class, AbstractTaskDeserializer.getSubtaskDeserializer())
                .registerTypeAdapter(HttpTaskManager.class, new ManagerDeserializer())
                .create();

    }


    @BeforeEach
    public void setUp() throws IOException, InterruptedException {

        AbstractTask.countReset();
        task0 = new Task("0", "TaskDescription",
                LocalDateTime.of(2000, 1, 1, 15, 0), Duration.ofHours(2));
        task1 = new Task("1", "TaskDescription",
                LocalDateTime.of(2003, 1, 1, 11, 0), Duration.ofHours(1));
        epic2 = new Epic("2", "EpicDescription");
        subtask3 = new Subtask("3", "Description", epic2,
                LocalDateTime.of(2002, 1, 1, 15, 0), Duration.ofHours(1));
        subtask4 = new Subtask("4", "Description", epic2,
                LocalDateTime.of(2002, 1, 1, 18, 0), Duration.ofHours(3));


        kvServer = new KVServer();
        kvServer.start();
        server = new HttpTaskServer();

        client = HttpClient.newHttpClient();

        URI urlTask = URI.create(urlString + "/task");
        HttpRequest requestTask = HttpRequest.newBuilder()
                .uri(urlTask)
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(task0)))
                .build();
        client.send(requestTask, HttpResponse.BodyHandlers.ofString());

        URI urlEpic = URI.create(urlString + "/epic");
        HttpRequest requestEpic = HttpRequest.newBuilder()
                .uri(urlEpic)
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(epic2)))
                .build();
        client.send(requestEpic, HttpResponse.BodyHandlers.ofString());

        URI urlSubtask = URI.create(urlString + "/subtask");
        HttpRequest requestSubtask = HttpRequest.newBuilder()
                .uri(urlSubtask)
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(subtask3)))
                .build();
        client.send(requestSubtask, HttpResponse.BodyHandlers.ofString());

        HttpRequest requestSubtask2 = HttpRequest.newBuilder()
                .uri(urlSubtask)
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(subtask4)))
                .build();
        client.send(requestSubtask2, HttpResponse.BodyHandlers.ofString());

    }

    @AfterEach
    void close() {
        kvServer.stop();
        server.stop();
    }


    @Test
    void getTask() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create(urlString + "/task/?id=0"))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        assertEquals(task0, gson.fromJson(response.body(), Task.class));
    }

    @Test
    void getAllTasks() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create(urlString + "/task/"))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        assertEquals(gson.toJson(server.getManager().getTasks()), response.body());
    }

    @Test
    void getSubtask() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create(urlString + "/subtask/?id=3"))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertEquals(subtask3, gson.fromJson(response.body(), Subtask.class));
    }

    @Test
    void getAllSubtasks() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create(urlString + "/subtask/"))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        assertEquals(gson.toJson(server.getManager().getSubtasks()), response.body());
    }

    @Test
    void getEpic() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create(urlString + "/epic/?id=2"))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        assertEquals(gson.toJson(epic2), response.body());
    }

    @Test
    void getAllEpics() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create(urlString + "/epic/"))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        assertEquals(gson.toJson(server.getManager().getEpics()), response.body());
    }

    @Test
    void addNewTask() throws IOException, InterruptedException {
        String jsonTask = gson.toJson(task1);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(urlString + "/task"))
                .POST(HttpRequest.BodyPublishers.ofString(jsonTask))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode());
        assertEquals(task1, server.getManager().getTasks().get(1));
    }


    @Test
    void addNewEpic() throws IOException, InterruptedException {
        Epic newEpic = new Epic("name", "description");
        String jsonEpic = gson.toJson(newEpic);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(urlString + "/epic"))
                .POST(HttpRequest.BodyPublishers.ofString(jsonEpic))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode());
        assertEquals(newEpic, server.getManager().getEpics().get(newEpic.getId()));
    }

    @Test
    void addNewSubtask() throws IOException, InterruptedException {
        Subtask newSubtask = new Subtask("new", "new", epic2,
                LocalDateTime.of(2002, 1, 3, 10, 0), Duration.ofHours(1));
        String subtaskJson = gson.toJson(newSubtask);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(urlString + "/subtask"))
                .POST(HttpRequest.BodyPublishers.ofString(subtaskJson))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode());
        assertEquals(newSubtask, server.getManager().getSubtasks().get(newSubtask.getId()));
    }

    @Test
    void deleteTask() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .DELETE()
                .uri(URI.create(urlString + "/task/?id=0"))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        assertEquals(0, server.getManager().tasks.size());
        assertEquals(1, server.getManager().epics.size());
        assertEquals(2, server.getManager().subtasks.size());
    }

    @Test
    void deleteAllTasks() throws IOException, InterruptedException {

        String jsonTask = gson.toJson(task1);

        HttpRequest request1 = HttpRequest.newBuilder()
                .uri(URI.create(urlString + "/task"))
                .POST(HttpRequest.BodyPublishers.ofString(jsonTask))
                .build();
        client.send(request1, HttpResponse.BodyHandlers.ofString());


        HttpRequest request = HttpRequest.newBuilder()
                .DELETE()
                .uri(URI.create(urlString + "/task"))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        assertEquals(0, server.getManager().tasks.size());
        assertEquals(1, server.getManager().epics.size());
        assertEquals(2, server.getManager().subtasks.size());
    }

    @Test
    void deleteEpic() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .DELETE()
                .uri(URI.create(urlString + "/epic/?id=2"))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        assertEquals(1, server.getManager().tasks.size());
        assertEquals(0, server.getManager().epics.size());
        assertEquals(0, server.getManager().subtasks.size());
    }

    @Test
    void deleteAllEpics() throws IOException, InterruptedException {

        Epic newEpic = new Epic("name", "description");
        String jsonEpic = gson.toJson(newEpic);

        HttpRequest request1 = HttpRequest.newBuilder()
                .uri(URI.create(urlString + "/epic"))
                .POST(HttpRequest.BodyPublishers.ofString(jsonEpic))
                .build();
        client.send(request1, HttpResponse.BodyHandlers.ofString());

        HttpRequest request = HttpRequest.newBuilder()
                .DELETE()
                .uri(URI.create(urlString + "/epic/"))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        assertEquals(1, server.getManager().tasks.size());
        assertEquals(0, server.getManager().epics.size());
        assertEquals(0, server.getManager().subtasks.size());
    }

    @Test
    void deleteSubtask() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .DELETE()
                .uri(URI.create(urlString + "/subtask/?id=3"))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        assertEquals(1, server.getManager().tasks.size());
        assertEquals(1, server.getManager().epics.size());
        assertEquals(1, server.getManager().subtasks.size());
    }

    @Test
    void deleteAllSubtasks() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .DELETE()
                .uri(URI.create(urlString + "/subtask/"))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        assertEquals(1, server.getManager().tasks.size());
        assertEquals(1, server.getManager().epics.size());
        assertEquals(0, server.getManager().subtasks.size());
    }

    @Test
    void getSubtasksByEpic() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create(urlString + "/subtask/epic/?id=2"))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertEquals(gson.toJson(server.getManager().getSubtasksByEpic(2)), response.body());
    }

    @Test
    void getPrioritized() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create(urlString + ""))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertEquals(gson.toJson(server.getManager().getPrioritizedTasks()), response.body());
    }

    @Test
    void getHistory() throws IOException, InterruptedException {
        server.getManager().getById(0, TaskType.TASK);
        server.getManager().getById(3, TaskType.SUBTASK);

        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create(urlString + "/history"))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertEquals(gson.toJson(server.getManager().getHistory()), response.body());
    }

    @Test
    void saveLoadTest() {
        server.getManager().getById(0, TaskType.TASK);
        server.getManager().getById(3, TaskType.SUBTASK);
        TaskManager oldManager = server.getManager();
        server.stop();

        HttpTaskServer newServer = new HttpTaskServer("http://localhost:8078/");
        TaskManager newManager = newServer.getManager();

        assertEquals(oldManager.getTasks(), newManager.getTasks());
        assertEquals(oldManager.getSubtasks(), newManager.getSubtasks());
        assertEquals(oldManager.getEpics(), newManager.getEpics());
        assertEquals(oldManager.getHistory(), newManager.getHistory());
        assertEquals(oldManager.getPrioritizedTasks(), newManager.getPrioritizedTasks());
        newServer.stop();
    }


}
