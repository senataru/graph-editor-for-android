package com.example.graph_editor.file_serialization;

import android.content.Context;
import android.net.Uri;
import android.widget.Toast;

import com.example.graph_editor.model.graph_storage.InvalidGraphStringException;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class Loader {
    public static <T> T load(File directory, String fileName) {
        try (FileInputStream fis = new FileInputStream(new File(directory, fileName))) {
            try (ObjectInputStream is = new ObjectInputStream(fis)) {
                T deserialized = (T) is.readObject();
                return deserialized;
            } catch (IOException | ClassNotFoundException exception) {
                throw new RuntimeException(exception);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> T load(Context context, Uri uri) {
        try (InputStream in = context.getContentResolver().openInputStream(uri)) {
            try (ObjectInputStream is = new ObjectInputStream(in)) {
                T deserialized = (T) is.readObject();
                return deserialized;
            } catch (IOException | ClassNotFoundException exception) {
                throw new RuntimeException(exception);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
