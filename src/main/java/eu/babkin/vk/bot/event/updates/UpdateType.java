package eu.babkin.vk.bot.event.updates;

public enum UpdateType {
    MESSAGE(4);

    private final int id;

    UpdateType(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }
}
