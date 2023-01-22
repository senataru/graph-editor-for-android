package com.example.graph_editor.file_serialization;

import android.content.Context;
import android.net.Uri;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;

public class Saver {
    public static void save(Context context, String filename, Serializable serialized) {
        FileOutputStream fos;
        try {
            fos = context.openFileOutput(filename, Context.MODE_PRIVATE);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(serialized);
            oos.close();
            fos.close();
        } catch (IOException e) {
            Toast.makeText(context, "Serialization failed due to io", Toast.LENGTH_LONG).show();
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
