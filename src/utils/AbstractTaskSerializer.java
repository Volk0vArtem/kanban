package utils;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import tasks.*;

import java.lang.reflect.Type;
import java.time.format.DateTimeFormatter;

public class AbstractTaskSerializer {
    static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd_MM_yyyy|HH:mm");

    public static TaskSerializer getTaskSerializer(){
        return new TaskSerializer();
    }

    public static SubtaskSerializer getSubtaskSerializer(){
        return new SubtaskSerializer();
    }

    public static EpicSerializer getEpicSerializer(){
        return new EpicSerializer();
    }
}

class TaskSerializer implements JsonSerializer<Task> {
    @Override
    public JsonElement serialize(Task task, Type type, JsonSerializationContext jsonSerializationContext) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd_MM_yyyy|HH:mm");
        JsonObject result = new JsonObject();

        result.addProperty("id", task.getId());
        result.addProperty("type", TaskType.TASK.toString());
        result.addProperty("name", task.getName());
        result.addProperty("status", task.getStatus().toString());
        result.addProperty("description", task.getDescription());
        result.addProperty("startTime", task.getStartTime().format(formatter));
        result.addProperty("endTime", task.getEndTime().format(formatter));
        result.addProperty("duration", task.getDuration().toString());

        return result;
    }
}
    class SubtaskSerializer implements JsonSerializer<Subtask> {
        @Override
        public JsonElement serialize(Subtask subtask, Type type, JsonSerializationContext jsonSerializationContext) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd_MM_yyyy|HH:mm");
            JsonObject result = new JsonObject();

            result.addProperty("id", subtask.getId());
            result.addProperty("type", TaskType.SUBTASK.toString());
            result.addProperty("name", subtask.getName());
            result.addProperty("status", subtask.getStatus().toString());
            result.addProperty("description", subtask.getDescription());
            result.addProperty("startTime", subtask.getStartTime().format(formatter));
            result.addProperty("endTime", subtask.getEndTime().format(formatter));
            result.addProperty("duration", subtask.getDuration().toString());
            result.addProperty("epic", subtask.getEpic().getId());

            return result;
        }
    }
        class EpicSerializer implements JsonSerializer<Epic>{

            @Override
            public JsonElement serialize(Epic epic, Type type, JsonSerializationContext jsonSerializationContext) {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd_MM_yyyy|HH:mm");
                JsonObject result = new JsonObject();

                result.addProperty("id", epic.getId());
                result.addProperty("type", TaskType.EPIC.toString());
                result.addProperty("name", epic.getName());
                result.addProperty("status", epic.getStatus().toString());
                result.addProperty("description", epic.getDescription());
                if (epic.getStartTime() != null) {
                    result.addProperty("startTime", epic.getStartTime().format(formatter));
                    result.addProperty("endTime", epic.getEndTime().format(formatter));
                    result.addProperty("duration", epic.getDuration().toString());
                }

                return result;
            }
        }





