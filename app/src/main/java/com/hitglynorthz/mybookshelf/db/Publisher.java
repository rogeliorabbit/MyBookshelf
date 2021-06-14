package com.hitglynorthz.mybookshelf.db;

public class Publisher {

    // nombre de la tabla
    public static final String TABLE_NAME = "Publishers";

    // elementos de la tabla
    public static final String COLUMN_PUBLISHER_ID = "id_publisher";
    public static final String COLUMN_PUBLISHER_NAME = "publisher_name";
    public static final String COLUMN_PUBLISHER_TIMESTAMP = "timestamp";

    // creacion de la tabla
    public static final String CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + "("
                    + COLUMN_PUBLISHER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + COLUMN_PUBLISHER_NAME + " TEXT, "
                    + COLUMN_PUBLISHER_TIMESTAMP + " DATETIME DEFAULT CURRENT_TIMESTAMP"
                    + ")";

    private int id_publisher;
    private String publisher_name;
    private String timestamp;

    public Publisher() {}

    public Publisher(int id_publisher, String publisher_name, String timestamp) {
        this.id_publisher = id_publisher;
        this.publisher_name = publisher_name;
        this.timestamp = timestamp;
    }

    public int getId_publisher() {
        return id_publisher;
    }

    public void setId_publisher(int id_publisher) {
        this.id_publisher = id_publisher;
    }

    public String getPublisher_name() {
        return publisher_name;
    }

    public void setPublisher_name(String publisher_name) {
        this.publisher_name = publisher_name;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}
