package ch.caro62.kb.model;


import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "years_total")
public class Years {

    @DatabaseField(generatedId = true, columnName = "rowid")
    private long rowId;

    @DatabaseField
    private long year;

    @DatabaseField
    private long rank;

    @DatabaseField(foreign = true)
    private Movie movie;

    @DatabaseField
    private long screens;

    @DatabaseField(columnName = "boxoffice_rur")
    private long boxOfficeRur;

    @DatabaseField(columnName = "boxoffice_usd")
    private long boxOfficeUsd;

    @DatabaseField
    private long viewers;

    @DatabaseField
    private long days;

    public Years() {
    }

    public Years(long rowId, long year, long rank, Movie movie, long screens, long boxOfficeRur, long boxOfficeUsd, long viewers, long days) {
        this.rowId = rowId;
        this.year = year;
        this.rank = rank;
        this.movie = movie;
        this.screens = screens;
        this.boxOfficeRur = boxOfficeRur;
        this.boxOfficeUsd = boxOfficeUsd;
        this.viewers = viewers;
        this.days = days;
    }

    public long getYear() {
        return year;
    }

    public void setYear(long year) {
        this.year = year;
    }

    public long getRank() {
        return rank;
    }

    public void setRank(long rank) {
        this.rank = rank;
    }

    public Movie getMovie() {
        return movie;
    }

    public void setMovie(Movie movie) {
        this.movie = movie;
    }

    public long getBoxOfficeRur() {
        return boxOfficeRur;
    }

    public void setBoxOfficeRur(long boxOfficeRur) {
        this.boxOfficeRur = boxOfficeRur;
    }

    public long getBoxOfficeUsd() {
        return boxOfficeUsd;
    }

    public void setBoxOfficeUsd(long boxOfficeUsd) {
        this.boxOfficeUsd = boxOfficeUsd;
    }

    public long getViewers() {
        return viewers;
    }

    public void setViewers(long viewers) {
        this.viewers = viewers;
    }

    public long getDays() {
        return days;
    }

    public void setDays(long days) {
        this.days = days;
    }

    public long getScreens() {
        return screens;
    }

    public void setScreens(long screens) {
        this.screens = screens;
    }

    public long getRowId() {
        return rowId;
    }

    public void setRowId(long rowId) {
        this.rowId = rowId;
    }
}
