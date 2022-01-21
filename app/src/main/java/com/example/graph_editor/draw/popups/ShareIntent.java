package com.example.graph_editor.draw.popups;

import android.content.Context;
import android.content.Intent;

import com.example.graph_editor.graph_storage.GraphWriter;
import com.example.graph_editor.model.state.StateStack;

public class ShareIntent {
    private final Context context;
    private final StateStack stateStack;

    public ShareIntent(Context context, StateStack stateStack) {
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
