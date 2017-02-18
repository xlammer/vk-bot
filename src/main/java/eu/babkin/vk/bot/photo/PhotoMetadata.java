package eu.babkin.vk.bot.photo;

import com.google.common.base.MoreObjects;

public class PhotoMetadata {
    private int server;
    private String photo;
    private String hash;


    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public int getServer() {
        return server;
    }

    public void setServer(int server) {
        this.server = server;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("server", server)
                .add("photo", photo)
                .add("hash", hash)
                .toString();
    }
}
