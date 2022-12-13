package com.example.graph_editor.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface EdgePropertySaveDao {

    @Query("SELECT * FROM edge_property_save ORDER BY date DESC")
    List<EdgePropertySave> getAllPropertySaves();

    @Query("UPDATE edge_property_save SET property = :property, date = :date WHERE uid = :id")
    void updateProperty(long id, String property, long date);

    @Insert
    long[] insertPropertySave(EdgePropertySave... edgePropertySaves);

    @Query("DELETE FROM edge_property_save")
    void deleteAll();

    @Delete
    void delete(EdgePropertySave save);

}