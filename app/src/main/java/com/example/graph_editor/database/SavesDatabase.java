package com.example.graph_editor.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {Save.class, VertexPropertySave.class, EdgePropertySave.class}, version = 1, exportSchema = false)
public abstract class SavesDatabase extends RoomDatabase {
    public abstract SaveDao saveDao();

    public abstract VertexPropertySaveDao vertexPropertySaveDao();

    public abstract EdgePropertySaveDao edgePropertySaveDao();

    private static SavesDatabase INSTANCE;

    public static SavesDatabase getDbInstance(Context context) {
        if(INSTANCE == null) {
            INSTANCE = Room.databaseBuilder(context.getApplicationContext(), SavesDatabase.class, "DB_SCORES")
                    .allowMainThreadQueries()
                    .build();
        }
        return INSTANCE;
    }
}
