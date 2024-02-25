package httpServer;

import com.sun.net.httpserver.HttpServer;
import managers.FileBackedTasksManager;
import managers.HttpTaskManager;
import managers.Managers;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class HttpTaskServer {
    FileBackedTasksManager manager;
    private static final int PORT = 8080;
    private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;

    private HttpServer httpServer;

    public HttpTaskServer() {
        this.manager = Managers.getHttpManager("http://localhost:8078/");
        try {
            httpServer = HttpServer.create();
            httpServer.bind(new InetSocketAddress(8080), 0);
            httpServer.createContext("/tasks", new TasksHandler(manager));
            httpServer.start();
            System.out.println("HTTP-сервер запущен на " + PORT + " порту!");
        } catch (IOException e) {
            System.out.println("При создании сервера произошла ошибка");
        }
    }


    public HttpTaskServer(String url) {
        this.manager = HttpTaskManager.load(url);
        try {
            httpServer = HttpServer.create();
            httpServer.bind(new InetSocketAddress(PORT), 0);
            httpServer.createContext("/tasks", new TasksHandler(manager));
            httpServer.start();
            System.out.println("HTTP-сервер запущен на " + PORT + " порту!");
        } catch (IOException e) {
            System.out.println("При создании сервера произошла ошибка");
        }
    }

    public void stop() {
        httpServer.stop(0);
    }

    public FileBackedTasksManager getManager() {
        return manager;
    }

}
