package eu.babkin.vk.bot.event.updates;

import com.google.gson.*;
import eu.babkin.vk.bot.messages.IncomingMessage;

import java.lang.reflect.Type;

import static eu.babkin.vk.bot.event.updates.UpdateType.MESSAGE;

public class UpdateDeserializer implements JsonDeserializer<Update> {

    @Override
    public Update deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        if (json instanceof JsonArray) {
            JsonArray jArray = (JsonArray) json;

            if (jArray.get(0).getAsInt() == MESSAGE.getId()) {
                IncomingMessage update = new IncomingMessage();
                update.setMessageId(jArray.get(1).getAsLong());

                update.setPeerId(jArray.get(3).getAsInt());


                update.setTimestamp(jArray.get(4).getAsLong());
                update.setChatName(jArray.get(5).getAsString());
                update.setMessage(jArray.get(6).getAsString());
                return update;
            }
        }
        return new Update();
    }
}
