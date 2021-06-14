package com.hitglynorthz.mybookshelf.db;

public class Shelves {

    // nombre de la tabla
    public static final String TABLE_NAME = "Shelves";

    // elementos de la tabla
    public static final String COLUMN_SHELVES_ID = "id_shelves";
    public static final String COLUMN_SHELVES_NAME = "shelves_name";
    public static final String COLUMN_SHELVES_TIMESTAMP = "timestamp";

    // creacion de la tabla
    public static final String CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + "("
                    + COLUMN_SHELVES_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + COLUMN_SHELVES_NAME + " TEXT, "
                    + COLUMN_SHELVES_TIMESTAMP + " DATETIME DEFAULT CURRENT_TIMESTAMP"
                    + ")";

    private int id_shelves;
    private String shelves_name;
    private String timestamp;

    public Shelves() {}

    public Shelves(int id_shelves, String shelves_name, String timestamp) {
        this.id_shelves = id_shelves;
        this.shelves_name = shelves_name;
        this.timestamp = timestamp;
    }

    public int getId_shelves() {
        return id_shelves;
    }

    public void setId_shelves(int id_shelves) {
        this.id_shelves = id_shelves;
    }

    public String getShelves_name() {
        return shelves_name;
    }

    public void setShelves_name(String shelves_name) {
        this.shelves_name = shelves_name;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}
