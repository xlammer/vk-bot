package eu.babkin.vk.bot.event;

import com.google.common.base.MoreObjects;
import eu.babkin.vk.bot.event.updates.Update;

import java.util.List;

public class Event {

    private Integer ts;

    private List<Update> updates;

    public Integer getTs() {
        return ts;
    }

    public void setTs(Integer ts) {
        this.ts = ts;
    }

    public List<Update> getUpdates() {
        return updates;
    }

    public void setUpdates(List<Update> updates) {
        this.updates = updates;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("ts", ts)
                .add("updates", updates)
                .toString();
    }
}
