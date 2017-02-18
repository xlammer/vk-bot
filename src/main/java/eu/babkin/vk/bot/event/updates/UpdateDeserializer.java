package eu.babkin.vk.bot.event.updates;

import com.google.gson.*;

import java.lang.reflect.Type;

import static eu.babkin.vk.bot.event.updates.UpdateType.MESSAGE;

public class UpdateDeserializer implements JsonDeserializer<Update> {

    private static final int CHAT_ID_BASE = 2000000000;

    @Override
    public Update deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        if (json instanceof JsonArray) {
            JsonArray jArray = (JsonArray) json;

            if (jArray.get(0).getAsInt() == MESSAGE.getId()) {
                MessageUpdate update = new MessageUpdate();
                update.setMessageId(jArray.get(1).getAsLong());

                int chatId = jArray.get(3).getAsInt();
                if (chatId > CHAT_ID_BASE) {
                    update.setChatId(chatId - CHAT_ID_BASE);
                } else {
                    update.setChatId(chatId);
                }

                update.setTimestamp(jArray.get(4).getAsLong());
                update.setChatName(jArray.get(5).getAsString());
                update.setMessage(jArray.get(6).getAsString());
                return update;
            }
        }
        return new Update();
    }
}
