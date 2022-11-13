package com.example.graph_editor.database;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

@Entity(tableName = "property_save",
        foreignKeys = {
            @ForeignKey(
                    entity = Save.class,
                    parentColumns = "uid",
                    childColumns = "graphSaveUid")
        })
public class PropertySave {
    @PrimaryKey(autoGenerate = true)
    public long uid;
    @ColumnInfo(index = true)
    public long graphSaveUid;
    @ColumnInfo(name = "Name")
    public String name;
    @ColumnInfo(name = "Property")
    public String property;
    @ColumnInfo(name = "Date")
    public long date;

    public PropertySave(long graphSaveUid, String name, String property, long date) {
        this.name = name;
        this.property = property;
        this.date = date;
        this.graphSaveUid = graphSaveUid;
    }

}
