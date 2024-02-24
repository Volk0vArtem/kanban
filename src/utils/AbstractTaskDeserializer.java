package utils;

import com.google.gson.*;
import tasks.*;
import java.lang.reflect.Type;

public class AbstractTaskDeserializer {

    public static TaskDeserializer getTaskDeserializer(){
        return new TaskDeserializer();
    }

    public static EpicDeserializer getEpicDeserializer(){
        return new EpicDeserializer();
    }

    public static SubtaskDeserializer getSubtaskDeserializer(){
        return new SubtaskDeserializer();
    }


}

class TaskDeserializer implements JsonDeserializer<Task> {
    @Override
    public Task deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {

        JsonObject object = jsonElement.getAsJsonObject();

        String id = object.get("id").getAsString();
        String name = object.get("name").getAsString();
        String description = object.get("description").getAsString();
        String status = object.get("status").getAsString();
        String startTime = object.get("startTime").getAsString();
        String duration = object.get("duration").getAsString();

        return new Task(id, name, status, description, startTime, duration);

    }
}
    class EpicDeserializer implements JsonDeserializer<Epic>{
        @Override
        public Epic deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {

            JsonObject object = jsonElement.getAsJsonObject();

            String id = object.get("id").getAsString();
            String name = object.get("name").getAsString();
            String description = object.get("description").getAsString();
            String status = object.get("status").getAsString();


            return new Epic(id, name, status, description, "null", "null");
        }
}

class SubtaskDeserializer implements JsonDeserializer<Subtask>{

    @Override
    public Subtask deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        JsonObject object = jsonElement.getAsJsonObject();

        String id = object.get("id").getAsString();
        String name = object.get("name").getAsString();
        String description = object.get("description").getAsString();
        String status = object.get("status").getAsString();
        String startTime = object.get("startTime").getAsString();
        String duration = object.get("duration").getAsString();
        String epicId = object.get("epic").getAsString();

        return new Subtask(id, name, status, description, epicId, startTime, duration);
    }


}
