package com.example.graph_editor.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface SaveDao {

    @Query("SELECT * FROM save ORDER BY date DESC")
    List<Save> getAllScores();

    @Insert
    void insertScore(Save... scores);

    @Query("DELETE FROM save")
    void deleteAll();

}