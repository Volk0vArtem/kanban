package httpServer;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import managers.FileBackedTasksManager;
import tasks.*;

import java.io.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;


public class TasksHandler implements HttpHandler {

    FileBackedTasksManager manager;
    Gson gson;
    public TasksHandler(FileBackedTasksManager manager) {
        this.manager = manager;
        gson = new Gson();
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        Endpoint endpoint = getEndpoint(exchange.getRequestURI().getPath(), exchange.getRequestMethod());
        String[] pathParts = exchange.getRequestURI().getPath().split("/");

        switch (endpoint) {
            case GET_TASK:
                handleGetTask(exchange, pathParts[3]);
            case GET_SUBTASK:
                handleGetSubtask(exchange, pathParts[3]);
            case GET_EPIC:
                handleGetEpic(exchange, pathParts[3]);
            case GET_TASKS:
                handleGetTasks(exchange);
            case GET_SUBTASKS:
                handleGetSubtasks(exchange);
            case GET_EPICS:
                handleGetEpics(exchange);
            case GET_SUBTASKS_BY_EPIC:
                handleGetSubtasksByEpic(exchange, pathParts[4]);
            case GET_HISTORY:
                handleGetHistory(exchange);
            case GET_PRIORITIZED_TASKS:
                handleGetPrioritized(exchange);
            case POST_TASK:
                handlePostTask(exchange);
            case POST_SUBTASK:
                handlePostSubtask(exchange);
            case POST_EPIC:
                handlePostEpic(exchange);
            case DELETE_TASK:
                handleDeleteEpic(exchange, pathParts[3]);
            case DELETE_SUBTASK:
                handleDeleteSubtask(exchange, pathParts[3]);
            case DELETE_EPIC:
                handleDeleteEpic(exchange, pathParts[3]);
            case DELETE_TASKS:
                handleDeleteTasks(exchange);
            case DELETE_SUBTASKS:
                handleDeleteSubtasks(exchange);
            case DELETE_EPICS:
                handleDeleteEpics(exchange);
            default:
                writeResponse(exchange, "Такого эндпоинта не существует", 400);
        }
    }

    private void writeResponse(HttpExchange exchange, String responseString, int responseCode) throws IOException {
        exchange.sendResponseHeaders(responseCode,0);
        OutputStream os = exchange.getResponseBody();
        os.write(responseString.getBytes(StandardCharsets.UTF_8));
        os.close();
    }

    private Endpoint getEndpoint(String requestPath, String requestMethod) {
        String[] pathParts = requestPath.split("/");

        switch (requestMethod){
            case "GET":
                if (pathParts.length == 2){
                    return Endpoint.GET_PRIORITIZED_TASKS;
                } else if (pathParts[2].equals("task")){
                    if (pathParts.length == 3){
                        return Endpoint.GET_TASKS;
                    } else {
                        return Endpoint.GET_TASK;
                    }
                } else if (pathParts[2].equals("subtask")){
                    if (pathParts.length == 3){
                        return Endpoint.GET_SUBTASKS;
                    } else if (pathParts.length == 4){
                        return Endpoint.GET_SUBTASK;
                    } else if (pathParts.length == 5 && pathParts[3].equals("epic")){
                        return Endpoint.GET_SUBTASKS_BY_EPIC;
                    }

                } else if (pathParts[2].equals("epic")){
                    if (pathParts.length == 3){
                        return Endpoint.GET_EPICS;
                    } else if (pathParts.length == 4){
                        return Endpoint.GET_EPIC;
                    }

                } else if (pathParts[2].equals("history")){
                    return Endpoint.GET_HISTORY;
                }
                break;

            case "POST":
                if (pathParts[2].equals("task")){
                    return Endpoint.POST_TASK;
                } else if (pathParts[2].equals("subtask")){
                    return Endpoint.POST_SUBTASK;
                } else if (pathParts[2].equals("epic")){
                    return Endpoint.POST_EPIC;
                }
                break;

            case "DELETE":
                if (pathParts.length == 3){
                    if (pathParts[2].equals("task")){
                        return Endpoint.DELETE_TASKS;
                    } else if (pathParts[2].equals("subtask")){
                        return Endpoint.DELETE_SUBTASKS;
                    } else if (pathParts[2].equals("epic")){
                        return Endpoint.DELETE_EPICS;
                    }
                } else if (pathParts[2].equals("task")){
                    return Endpoint.DELETE_TASK;
                } else if (pathParts[2].equals("subtask")){
                    return Endpoint.DELETE_SUBTASK;
                } else if (pathParts[2].equals("epic")){
                    return Endpoint.DELETE_EPIC;
                }
                break;
        }
        return Endpoint.UNKNOWN;
    }

    private void handleGetTask(HttpExchange exchange, String idString) throws IOException {
        int id = parseId(idString);
        if (id == -1){
            writeResponse(exchange, "Некорректный идентификатор задачи", 400);
            return;
        }
        AbstractTask task = manager.getById(id, TaskType.TASK);
        if (task == null){
            writeResponse(exchange, "Задача не найдена", 404);
            return;
        }
        writeResponse(exchange, gson.toJson(task), 200);
    }
    private void handleGetSubtask(HttpExchange exchange, String idString) throws IOException {
        int id = parseId(idString);
        if (id == -1){
            writeResponse(exchange, "Некорректный идентификатор подзадачи", 400);
            return;
        }
        AbstractTask task = manager.getById(id, TaskType.SUBTASK);
        if (task == null){
            writeResponse(exchange, "Подзадача не найдена", 404);
            return;
        }
        writeResponse(exchange, gson.toJson(task), 200);
    }
    private void handleGetEpic(HttpExchange exchange, String idString) throws IOException {
        int id = parseId(idString);
        if (id == -1){
            writeResponse(exchange, "Некорректный идентификатор эпика", 400);
            return;
        }
        AbstractTask task = manager.getById(id, TaskType.EPIC);
        if (task == null){
            writeResponse(exchange, "Эпик не найден", 404);
            return;
        }
        writeResponse(exchange, gson.toJson(task), 200);
    }

    private void handleGetTasks(HttpExchange exchange) throws IOException {
        writeResponse(exchange, gson.toJson(manager.getTasks()), 200);
    }

    private void handleGetSubtasks(HttpExchange exchange) throws IOException {
        writeResponse(exchange, gson.toJson(manager.getSubtasks()), 200);
    }

    private void handleGetEpics(HttpExchange exchange) throws IOException {
        writeResponse(exchange, gson.toJson(manager.getEpics()), 200);
    }

    private void handleGetHistory(HttpExchange exchange) throws IOException {
        writeResponse(exchange, gson.toJson(manager.getHistory()), 200);
    }

    private void handleGetPrioritized(HttpExchange exchange) throws IOException {
        writeResponse(exchange, gson.toJson(manager.getPrioritizedTasks()), 200);
    }

    private void handleGetSubtasksByEpic(HttpExchange exchange, String idString) throws IOException {
        int id = parseId(idString);
        if (id == -1){
            writeResponse(exchange, "Некорректный идентификатор эпика", 400);
        }
        AbstractTask epic = manager.getById(id, TaskType.EPIC);
        if (epic == null){
            writeResponse(exchange, "Эпик не найден", 404);
            return;
        }
        writeResponse(exchange, gson.toJson(manager.getSubtasksByEpic(id)), 200);
    }

    private void handlePostTask(HttpExchange exchange) throws IOException {
        Task task = null;
        try {
            String json = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
            task = gson.fromJson(json, Task.class);
        } catch (JsonSyntaxException e){
            writeResponse(exchange, "Получен некорректный JSON", 400);
            return;
        }
        if (task.getName() == null || task.getDescription() == null){
            writeResponse(exchange, "Название и описание задачи не могут быть пустыми", 400);
            return;
        }
        manager.addObjective(task, TaskType.TASK);
        writeResponse(exchange, "Задача добавлена", 201);
    }

    private void handlePostSubtask(HttpExchange exchange) throws IOException {
        Subtask subtask = null;
        try {
            String json = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
            subtask = gson.fromJson(json, Subtask.class);
        } catch (JsonSyntaxException e){
            writeResponse(exchange, "Получен некорректный JSON", 400);
            return;
        }
        if (subtask.getName() == null || subtask.getDescription() == null){
            writeResponse(exchange, "Название и описание подзадачи не могут быть пустыми", 400);
            return;
        }
        manager.addObjective(subtask, TaskType.SUBTASK);
        writeResponse(exchange, "Подзадача добавлена", 201);
    }

    private void handlePostEpic(HttpExchange exchange) throws IOException {
        Epic epic = null;
        try {
            String json = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
            epic = gson.fromJson(json, Epic.class);
        } catch (JsonSyntaxException e){
            writeResponse(exchange, "Получен некорректный JSON", 400);
            return;
        }
        if (epic.getName() == null || epic.getDescription() == null){
            writeResponse(exchange, "Название и описание эпика не могут быть пустыми", 400);
            return;
        }
        manager.addObjective(epic, TaskType.EPIC);
        writeResponse(exchange, "Эпик добавлен", 201);
    }

    private void handleDeleteTasks(HttpExchange exchange) throws IOException {
        manager.clearTasks();
        writeResponse(exchange, "Все задачи удалены", 200);
    }

    private void handleDeleteSubtasks(HttpExchange exchange) throws IOException {
        manager.clearSubtasks();
        writeResponse(exchange, "Все подзадачи удалены", 200);
    }

    private void handleDeleteEpics(HttpExchange exchange) throws IOException {
        manager.clearEpics();
        writeResponse(exchange, "Все эпики удалены", 200);
    }

    private void handleDeleteTask(HttpExchange exchange, String idString) throws IOException {
        int id = parseId(idString);
        if (id == -1){
            writeResponse(exchange, "Некорректный идентификатор задачи", 400);
            return;
        }
        AbstractTask task = manager.getById(id, TaskType.TASK);
        if (task == null){
            writeResponse(exchange, "Задача не найдена", 404);
        }
        manager.deleteById(id, TaskType.TASK);
        writeResponse(exchange, "Задача id=" + id + " удалена", 200);
    }

    private void handleDeleteSubtask(HttpExchange exchange, String idString) throws IOException {
        int id = parseId(idString);
        if (id == -1){
            writeResponse(exchange, "Некорректный идентификатор подзадачи", 400);
            return;
        }
        AbstractTask subtask = manager.getById(id, TaskType.SUBTASK);
        if (subtask == null){
            writeResponse(exchange, "Подзадача не найдена", 404);
        }
        manager.deleteById(id, TaskType.SUBTASK);
        writeResponse(exchange, "Подзадача id=" + id + " удалена", 200);
    }

    private void handleDeleteEpic(HttpExchange exchange, String idString) throws IOException {
        int id = parseId(idString);
        if (id == -1){
            writeResponse(exchange, "Некорректный идентификатор эпика", 400);
            return;
        }
        AbstractTask epic = manager.getById(id, TaskType.EPIC);
        if (epic == null){
            writeResponse(exchange, "Эпик не найден", 404);
        }

        manager.deleteById(id, TaskType.EPIC);
        writeResponse(exchange, "Эпик id=" + id + " удалён", 200);
    }

    private int parseId(String idString){
        int id = -1;
        try {
            id = Integer.parseInt(idString);
        } catch (NumberFormatException e){
            return -1;
        }
        return id;
    }
}
