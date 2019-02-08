package net.harimurti.joylive.JsonData;

public class Data {
    User[] rooms;

    public Data(User[] rooms) {
        this.rooms = rooms;
    }

    public User[] getRooms() {
        return this.rooms;
    }
}
