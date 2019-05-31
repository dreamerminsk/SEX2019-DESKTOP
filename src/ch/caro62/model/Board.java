package ch.caro62.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.text.MessageFormat;

@DatabaseTable(tableName = "sexBoards")
public class Board {

    @DatabaseField(id = true)
    private String ref;

    @DatabaseField
    private String user;

    @DatabaseField
    private String title;

    @DatabaseField
    private int pinCount = 0;

    @DatabaseField
    private int followerCount = 0;

    public Board() {

    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getRef() {
        return ref;
    }

    public void setRef(String ref) {
        this.ref = ref;
    }

    public int getPinCount() {
        return pinCount;
    }

    public void setPinCount(int pinCount) {
        this.pinCount = pinCount;
    }

    public int getFollowerCount() {
        return followerCount;
    }

    public void setFollowerCount(int followerCount) {
        this.followerCount = followerCount;
    }

    public String toString() {
        return MessageFormat.format("{0} - {1}", title, ref);
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }
}
