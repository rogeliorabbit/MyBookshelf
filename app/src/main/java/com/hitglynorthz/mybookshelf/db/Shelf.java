package com.hitglynorthz.mybookshelf.db;

public class Shelf {

    // nombre de la tabla
    public static final String TABLE_NAME = "Shelfs";

    // elementos de la tabla
    public static final String COLUMN_SHELF_ID = "id_shelf";
    public static final String COLUMN_SHELF_NAME = "shelf_name";
    public static final String COLUM_SHELF_ID_SHELVES = "id_shelves";
    public static final String COLUMN_SHELF_TIMESTAMP = "timestamp";

    // creacion de la tabla
    public static final String CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + "("
                    + COLUMN_SHELF_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + COLUMN_SHELF_NAME + " TEXT, "
                    + COLUM_SHELF_ID_SHELVES + " INTEGER, "
                    + COLUMN_SHELF_TIMESTAMP + " DATETIME DEFAULT CURRENT_TIMESTAMP, "
                    + "FOREIGN KEY(" + COLUM_SHELF_ID_SHELVES +") REFERENCES " + Shelves.TABLE_NAME + "(" + Shelves.COLUMN_SHELVES_ID + ")"
                    + ")";

    private int id_shelf;
    private String shelf_name;
    private int id_shelves;
    private String timestamp;

    public Shelf() {}

    public Shelf(int id_shelf, String shelf_name, int id_shelves, String timestamp) {
        this.id_shelf = id_shelf;
        this.shelf_name = shelf_name;
        this.id_shelves = id_shelves;
        this.timestamp = timestamp;
    }

    public int getId_shelf() {
        return id_shelf;
    }

    public void setId_shelf(int id_shelf) {
        this.id_shelf = id_shelf;
    }

    public String getShelf_name() {
        return shelf_name;
    }

    public void setShelf_name(String shelf_name) {
        this.shelf_name = shelf_name;
    }

    public int getId_shelves() {
        return id_shelves;
    }

    public void setId_shelves(int id_shelves) {
        this.id_shelves = id_shelves;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}
