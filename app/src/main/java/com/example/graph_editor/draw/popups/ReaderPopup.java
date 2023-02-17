package com.example.graph_editor.draw.popups;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

import java.util.List;
import java.util.stream.Collectors;

import graph_editor.extensions.OnPropertyReaderSelection;

public class ReaderPopup {
    private final List<OnPropertyReaderSelection.SettingChoice> choices;
    private final Context context;
    public ReaderPopup(Context context, List<OnPropertyReaderSelection.SettingChoice> choices) {
        this.choices = choices;
        this.context = context;
    }
    public void show() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("choose property name, which will be read from graphs");
        List<String> list = choices.stream().map(OnPropertyReaderSelection.SettingChoice::getName).collect(Collectors.toList());
        String[] names = new String[list.size()];
        builder.setItems(names, (dialog, which) -> choices.get(which).choose());
    }
}
