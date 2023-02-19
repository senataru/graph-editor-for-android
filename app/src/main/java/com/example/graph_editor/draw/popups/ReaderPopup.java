package com.example.graph_editor.draw.popups;

import android.app.AlertDialog;
import android.content.Context;

import java.util.List;
import java.util.stream.Collectors;

import graph_editor.extensions.OnPropertyReaderSelection;

public class ReaderPopup {
    private final List<OnPropertyReaderSelection.SettingChoice> choices;
    private final Context context;
    private final Runnable invalidate;
    public ReaderPopup(Context context, List<OnPropertyReaderSelection.SettingChoice> choices, Runnable invalidate) {
        this.choices = choices;
        this.context = context;
        this.invalidate = invalidate;
    }
    public void show() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("choose property name, which will be read from graphs");
        List<String> list = choices.stream().map(OnPropertyReaderSelection.SettingChoice::getName).collect(Collectors.toList());
        String[] names = new String[list.size()];
        list.toArray(names);
        builder.setItems(names, (dialog, which) -> {
            choices.get(which).choose();
            invalidate.run();
        });
        builder.show();
    }
}
