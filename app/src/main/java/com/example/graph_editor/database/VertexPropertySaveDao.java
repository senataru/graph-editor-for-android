package com.example.graph_editor.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface VertexPropertySaveDao {

    @Query("SELECT * FROM vertex_property_save ORDER BY date DESC")
    List<VertexPropertySave> getAllPropertySaves();

    @Query("UPDATE vertex_property_save SET property = :property, date = :date WHERE uid = :id")
    void updateProperty(long id, String property, long date);

    @Insert
    long[] insertPropertySave(VertexPropertySave... vertexPropertySaves);

    @Query("DELETE FROM vertex_property_save")
    void deleteAll();

    @Delete
    void delete(VertexPropertySave save);

}