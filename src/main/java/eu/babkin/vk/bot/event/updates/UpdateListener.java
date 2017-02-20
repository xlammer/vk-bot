package eu.babkin.vk.bot.event.updates;

public interface UpdateListener<T> {

    void onUpdate(T update);
}
