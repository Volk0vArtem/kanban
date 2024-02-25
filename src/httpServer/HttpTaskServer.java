package httpServer;

import com.sun.net.httpserver.HttpServer;
import managers.FileBackedTasksManager;
import managers.Managers;


import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class HttpTaskServer {
    FileBackedTasksManager manager;
    private static final int PORT = 8080;
    private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;

    public HttpTaskServer() {
        //this.manager = Managers.detDefault(new File("save1.csv"));
        //this.manager = FileBackedTasksManager.loadFromFile(new File("save1.csv"));
        this.manager = Managers.getHttpManager("http://localhost:8078/");
        try {
            HttpServer httpServer = HttpServer.create();
            httpServer.bind(new InetSocketAddress(8080), 0);
            httpServer.createContext("/tasks", new TasksHandler(manager));
            httpServer.start();
            System.out.println("HTTP-сервер запущен на " + PORT + " порту!");
        } catch (IOException e) {
            System.out.println("При создании сервера произошла ошибка");
        }
    }

    public FileBackedTasksManager getManager(){
        return manager;
    }


    public static void main(String[] args) {

        HttpTaskServer server = new HttpTaskServer();

    }



}
