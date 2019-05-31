package ch.caro62.model;


import ch.caro62.model.dao.impl.UserDaoImpl;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "sexUsers", daoClass = UserDaoImpl.class)
public class User {

    @DatabaseField(id = true)
    private String ref;

    @DatabaseField
    private String name;

    @DatabaseField
    private String avatar;

    @DatabaseField
    private String description;

    @DatabaseField(columnName = "boards")
    private int boardCount;

    @DatabaseField(columnName = "followers")
    private int followerCount;

    @DatabaseField(columnName = "pins")
    private int pinCount;

    @DatabaseField(columnName = "repins")
    private int repinCount;

    @DatabaseField(columnName = "likes")
    private int likeCount;

    public User() {

    }

    public String getRef() {
        return ref;
    }

    public void setRef(String ref) {
        this.ref = ref;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public int getBoardCount() {
        return boardCount;
    }

    public void setBoardCount(int boardCount) {
        this.boardCount = boardCount;
    }

    public int getFollowerCount() {
        return followerCount;
    }

    public void setFollowerCount(int followerCount) {
        this.followerCount = followerCount;
    }

    public int getPinCount() {
        return pinCount;
    }

    public void setPinCount(int pinCount) {
        this.pinCount = pinCount;
    }

    public int getRepinCount() {
        return repinCount;
    }

    public void setRepinCount(int repinCount) {
        this.repinCount = repinCount;
    }

    public int getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(int likeCount) {
        this.likeCount = likeCount;
    }
}
