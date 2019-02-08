package net.harimurti.joylive.JsonData;

public class JoyData {
    private JoyUser[] rooms;

    public JoyData(JoyUser[] rooms) {
        this.rooms = rooms;
    }

    public JoyUser[] getRooms() {
        return this.rooms;
    }
}
