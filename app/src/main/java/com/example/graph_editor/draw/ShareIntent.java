package com.example.graph_editor.draw;

import static android.content.ClipDescription.MIMETYPE_TEXT_PLAIN;

import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.graph_editor.R;
import com.example.graph_editor.graph_storage.GraphScanner;
import com.example.graph_editor.graph_storage.GraphWriter;
import com.example.graph_editor.graph_storage.InvalidGraphStringException;
import com.example.graph_editor.model.DrawManager;
import com.example.graph_editor.model.Graph;
import com.example.graph_editor.model.mathematics.Rectangle;
import com.example.graph_editor.model.state.State;
import com.example.graph_editor.model.state.UndoRedoStack;

public class ShareIntent {
    private final Context context;
    private final UndoRedoStack stateStack;

    public ShareIntent(Context context, UndoRedoStack stateStack) {
        this.context = context;
        this.stateStack = stateStack;
    }

    public void show() {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, GraphWriter.toExact(stateStack.getCurrentState().getGraph()));
        sendIntent.setType("text/plain");

        Intent shareIntent = Intent.createChooser(sendIntent, null);
        context.startActivity(shareIntent);
    }
}
