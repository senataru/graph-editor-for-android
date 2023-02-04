package com.example.graph_editor.file_serialization;

import android.content.Context;
import android.net.Uri;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;

public class Saver {
    public static void save(Context context, File directory, String filename, Serializable serialized) {
        try {
            directory.mkdirs();
            try (FileOutputStream fos = new FileOutputStream(new File(directory, filename))) {
                try (ObjectOutputStream oos = new ObjectOutputStream(fos)) {
                    oos.writeObject(serialized);
                }
            }
        } catch (Exception e) {
            Toast.makeText(context, "Serialization failed due to" + e.getClass(), Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }

    public static void save(Context context, Uri uri, Serializable serialized) {
        try (OutputStream os = context.getContentResolver().openOutputStream(uri)) {
            ObjectOutputStream oos = new ObjectOutputStream(os);
            oos.writeObject(serialized);
            Toast.makeText(context, "Export complete", Toast.LENGTH_SHORT).show();
        } catch (FileNotFoundException e) {
            Toast.makeText(context, "Serialization failed due to file not found", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        } catch (IOException e) {
            Toast.makeText(context, "Serialization failed due to io", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }
}
