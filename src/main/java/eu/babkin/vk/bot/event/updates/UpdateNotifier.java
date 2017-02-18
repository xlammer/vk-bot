package eu.babkin.vk.bot.event.updates;

public interface UpdateNotifier<T> {

    void onUpdate(T payload);
}
