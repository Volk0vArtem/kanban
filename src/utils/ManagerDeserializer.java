package utils;

import com.google.gson.*;
import managers.HttpTaskManager;
import tasks.*;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class ManagerDeserializer implements JsonDeserializer<HttpTaskManager> {
    @Override
    public HttpTaskManager deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext context) throws JsonParseException {

        JsonObject jsonObject = jsonElement.getAsJsonObject();
        String url = jsonObject.get("url").getAsString();
        HttpTaskManager manager = new HttpTaskManager(url);

        HashMap<Integer, Task> tasks = new HashMap<>();
        HashMap<Integer, Epic> epics = new HashMap<>();
        HashMap<Integer, Subtask> subtasks = new HashMap<>();

        JsonArray tasksJson = jsonObject.getAsJsonArray("tasks");
        for (JsonElement task : tasksJson){
            Task t = context.deserialize(task, Task.class);
            tasks.put(t.getId(), t);
        }

        JsonArray epicsJson = jsonObject.getAsJsonArray("epics");
        for (JsonElement epic : epicsJson){
            Epic t = context.deserialize(epic, Epic.class);
            epics.put(t.getId(), t);
        }

        JsonArray subtasksJson = jsonObject.getAsJsonArray("subtasks");
        for (JsonElement subtask : subtasksJson){
            Subtask t = context.deserialize(subtask, Subtask.class);
            subtasks.put(t.getId(), t);
        }

        for (Task t : tasks.values()){
            manager.addObjective(t, TaskType.TASK);
        }

        for (Epic e : epics.values()){
            manager.addObjective(e, TaskType.EPIC);
        }

        for (Subtask t : subtasks.values()){
            manager.addObjective(t, TaskType.SUBTASK);
        }


        JsonArray historyJson = jsonObject.getAsJsonArray("history");
        ArrayList<Integer> ids = new ArrayList<>();
        for (JsonElement e : historyJson){
            ids.add(e.getAsInt());
        }
        manager.setHistory(ids);

        return manager;
    }
}
