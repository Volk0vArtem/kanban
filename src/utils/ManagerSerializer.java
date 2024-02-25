package utils;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import managers.HttpTaskManager;
import tasks.AbstractTask;
import tasks.Task;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class ManagerSerializer implements JsonSerializer<HttpTaskManager> {
    @Override
    public JsonElement serialize(HttpTaskManager manager, Type type, JsonSerializationContext context) {
        JsonObject json = new JsonObject();

        json.add("tasks", context.serialize(manager.getTasks().values()));
        json.add("epics", context.serialize(manager.getEpics().values()));
        json.add("subtasks", context.serialize(manager.getSubtasks().values()));
        ArrayList<Integer> history = new ArrayList<>();
        for (AbstractTask a : manager.getHistory()){
            history.add(a.getId());
        }
        json.add("history", context.serialize(history));
        json.add("prioritized", context.serialize(manager.getPrioritizedTasks()));
        json.addProperty("url", manager.getUrl());

        return json;
    }
}
