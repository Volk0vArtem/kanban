package httpServer;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import managers.HttpTaskManager;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;
import tasks.TaskType;
import utils.AbstractTaskSerializer;
import utils.ManagerSerializer;

/**
 * Постман: https://www.getpostman.com/collections/a83b61d9e1c81c10575c
 */
public class KVServer {
    public static final int PORT = 8078;
    private final String apiToken;
    private final HttpServer server;
    private final Map<String, String> data = new HashMap<>();

    public KVServer() throws IOException {
        apiToken = generateApiToken();
        server = HttpServer.create(new InetSocketAddress("localhost", PORT), 0);
        server.createContext("/register", this::register);
        server.createContext("/save", this::save);
        server.createContext("/load", this::load);
    }

    private void load(HttpExchange h) throws IOException {

        try {
            System.out.println("\n/load");
            if (!hasAuth(h)) {
                System.out.println("Запрос неавторизован, нужен параметр в query API_TOKEN со значением апи-ключа");
                h.sendResponseHeaders(403, 0);
                return;
            }
            if ("GET".equals(h.getRequestMethod())) {
                String key = h.getRequestURI().getPath().substring("/load/".length());
                if (key.isEmpty()) {
                    System.out.println("Key для загрузки пустой. key указывается в пути: /load/{key}");
                    h.sendResponseHeaders(400, 0);
                    return;
                }
                if(!data.containsKey(key)){
                    System.out.println("Key не найден");
                    h.sendResponseHeaders(404, 0);
                    return;
                }
                String text = data.get(key);
                sendText(h, text);
            } else {
                System.out.println("/save ждёт GET-запрос, а получил: " + h.getRequestMethod());
                h.sendResponseHeaders(405, 0);
            }

        } finally {
            h.close();
        }

    }

    private void save(HttpExchange h) throws IOException {
        try {
            System.out.println("\n/save");
            if (!hasAuth(h)) {
                System.out.println("Запрос неавторизован, нужен параметр в query API_TOKEN со значением апи-ключа");
                h.sendResponseHeaders(403, 0);
                return;
            }
            if ("POST".equals(h.getRequestMethod())) {
                String key = h.getRequestURI().getPath().substring("/save/".length());
                if (key.isEmpty()) {
                    System.out.println("Key для сохранения пустой. key указывается в пути: /save/{key}");
                    h.sendResponseHeaders(400, 0);
                    return;
                }
                String value = readText(h);
                if (value.isEmpty()) {
                    System.out.println("Value для сохранения пустой. value указывается в теле запроса");
                    h.sendResponseHeaders(400, 0);
                    return;
                }
                data.put(key, value);
                System.out.println("Значение для ключа " + key + " успешно обновлено!");
                h.sendResponseHeaders(200, 0);
            } else {
                System.out.println("/save ждёт POST-запрос, а получил: " + h.getRequestMethod());
                h.sendResponseHeaders(405, 0);
            }
        } finally {
            h.close();
        }
    }

    private void register(HttpExchange h) throws IOException {
        try {
            System.out.println("\n/register");
            if ("GET".equals(h.getRequestMethod())) {
                sendText(h, apiToken);
            } else {
                System.out.println("/register ждёт GET-запрос, а получил " + h.getRequestMethod());
                h.sendResponseHeaders(405, 0);
            }
        } finally {
            h.close();
        }
    }

    public void start() {
        System.out.println("Запускаем сервер на порту " + PORT);
        System.out.println("Открой в браузере http://localhost:" + PORT + "/");
        System.out.println("API_TOKEN: " + apiToken);
        server.start();
    }

    private String generateApiToken() {
        return "" + System.currentTimeMillis();
    }

    protected boolean hasAuth(HttpExchange h) {
        String rawQuery = h.getRequestURI().getRawQuery();
        return rawQuery != null && (rawQuery.contains("API_TOKEN=" + apiToken) || rawQuery.contains("API_TOKEN=DEBUG"));
    }

    protected String readText(HttpExchange h) throws IOException {
        return new String(h.getRequestBody().readAllBytes(), UTF_8);
    }

    protected void sendText(HttpExchange h, String text) throws IOException {
        byte[] resp = text.getBytes(UTF_8);
        h.getResponseHeaders().add("Content-Type", "application/json");
        h.sendResponseHeaders(200, resp.length);
        h.getResponseBody().write(resp);
    }





    public static void main(String[] args) throws IOException, InterruptedException {

        Task task1 = new Task("0", "TaskDescription",
                LocalDateTime.of(2000,01,01,15,00), Duration.ofHours(1));
        Task task2 = new Task("0", "TaskDescription",
                LocalDateTime.of(2222,01,01,15,00), Duration.ofHours(1));
        Epic epic1 = new Epic("1", "EpicDescription");

        Subtask subtask1 = new Subtask("subtask1", "2", epic1,
                LocalDateTime.of(2002,01,01,15,00), Duration.ofHours(1));
        Subtask subtask2 = new Subtask("subtask2", "3", epic1,
                LocalDateTime.of(2002,1,1,18,0), Duration.ofHours(3));

        KVServer kvServer = new KVServer();
        kvServer.start();
        HttpTaskManager manager = new HttpTaskManager("http://localhost:8078/");

        Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .registerTypeAdapter(Epic.class, AbstractTaskSerializer.getEpicSerializer())
                .registerTypeAdapter(Task.class, AbstractTaskSerializer.getTaskSerializer())
                .registerTypeAdapter(Subtask.class, AbstractTaskSerializer.getSubtaskSerializer())
                .registerTypeAdapter(HttpTaskManager.class, new ManagerSerializer())
                .create();


        manager.addObjective(task1, TaskType.TASK);
        manager.addObjective(task2, TaskType.TASK);

        manager.getById(0,TaskType.TASK);
        System.out.println(gson.toJson(manager));


        HttpTaskManager manager1 = manager.load("http://localhost:8078/");
        System.out.println(gson.toJson(manager1));
    }

}


