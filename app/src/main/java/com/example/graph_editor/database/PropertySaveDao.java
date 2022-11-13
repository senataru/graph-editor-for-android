package com.example.graph_editor.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface PropertySaveDao {

    @Query("SELECT * FROM property_save ORDER BY date DESC")
    List<PropertySave> getAllPropertySaves();

    @Query("UPDATE property_save SET property = :property, date = :date WHERE uid = :id")
    void updateProperty(long id, String property, long date);

    @Insert
    long[] insertPropertySave(PropertySave ... propertySaves);

    @Query("DELETE FROM property_save")
    void deleteAll();

    @Delete
    void delete(PropertySave save);

}