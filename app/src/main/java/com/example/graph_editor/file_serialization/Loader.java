package com.example.graph_editor.file_serialization;

import android.content.Context;
import android.net.Uri;
import android.widget.Toast;

import com.example.graph_editor.model.GraphType;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.util.Optional;

import graph_editor.properties.PropertySupportingGraph;
import graph_editor.visual.GraphVisualization;

public class Loader {
    public static FileData load(File directory, String fileName) {
        try (FileInputStream fis = new FileInputStream(new File(directory, fileName))) {
            try (ObjectInputStream is = new ObjectInputStream(fis)) {
                GraphVisualization<PropertySupportingGraph> visualization = (GraphVisualization<PropertySupportingGraph>) is.readObject();
                String typeName = (String) is.readObject();
                return new FileData(visualization, GraphType.valueOf(typeName));
            } catch (IOException | ClassNotFoundException exception) {
                throw new RuntimeException(exception);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static Optional<FileData> load(Context context, Uri uri) {
        try (InputStream in = context.getContentResolver().openInputStream(uri)) {
            try (ObjectInputStream is = new ObjectInputStream(in)) {
                GraphVisualization<PropertySupportingGraph> visualization = (GraphVisualization<PropertySupportingGraph>) is.readObject();
                String typeName = (String) is.readObject();
                return Optional.of(new FileData(visualization, GraphType.valueOf(typeName)));
            } catch (IOException | ClassNotFoundException e) {
                Toast.makeText(context, "Loading failed due to" + e.getClass(), Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
        } catch (IOException e) {
            Toast.makeText(context, "Loading failed due to" + e.getClass(), Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
        return Optional.empty();
    }
}
