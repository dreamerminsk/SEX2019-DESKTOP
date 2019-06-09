package ch.caro62.kb.model;


import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "movies")
public class Movie {

    @DatabaseField(id = true)
    private long id;

    @DatabaseField(index = true)
    private String ref;

    @DatabaseField(index = true)
    private String title;

    @DatabaseField
    private String original;

    public Movie() {

    }

    public Movie(int id, String ref, String title, String original) {
        this.id = id;
        this.ref = ref;
        this.title = title;
        this.original = original;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getRef() {
        return ref;
    }

    public void setRef(String ref) {
        this.ref = ref;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getOriginal() {
        return original;
    }

    public void setOriginal(String original) {
        this.original = original;
    }
}
