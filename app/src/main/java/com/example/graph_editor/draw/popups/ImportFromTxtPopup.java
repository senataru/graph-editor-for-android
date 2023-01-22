package com.example.graph_editor.draw.popups;

import static android.content.ClipDescription.MIMETYPE_TEXT_PLAIN;

import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.graph_editor.R;
import com.example.graph_editor.model.graph_storage.InvalidGraphStringException;
import com.example.graph_editor.model.DrawManager;
import com.example.graph_editor.model.mathematics.Rectangle;
import com.example.graph_editor.model.state.State;

import graph_editor.graph.Graph;
import graph_editor.graph.VersionStack;
import graph_editor.properties.PropertySupportingGraph;
import graph_editor.visual.GraphVisualization;

//do not use: disabled functionality, if you want to retrieve it, remember to remove all '/' marks
class ImportFromTxtPopup {
    private final Context context;
    private final VersionStack<GraphVisualization<PropertySupportingGraph>> stack;
    private final State state;

    private AlertDialog dialog;

    public ImportFromTxtPopup(Context context, VersionStack<GraphVisualization<PropertySupportingGraph>> stack, State state) {
        this.context = context;
        this.stack = stack;
        this.state = state;
    }

    public void show() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = (LayoutInflater)context.getSystemService( Context.LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.import_popup, null);

        EditText input = popupView.findViewById(R.id.import_popup_input);
        ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        String pasteData = "";

        // If it does contain data, decide if you can handle the data.
        if (clipboard.hasPrimaryClip() && clipboard.getPrimaryClipDescription().hasMimeType(MIMETYPE_TEXT_PLAIN)) {
            ClipData.Item item = clipboard.getPrimaryClip().getItemAt(0);
            pasteData = item.getText().toString();
        }
        input.setText(pasteData);


        popupView.findViewById(R.id.import_popup_confirm).setOnClickListener(v -> {
//            Graph g;
//            try {
//                g = GraphScanner.fromExact(input.getText().toString());
//            } catch (InvalidGraphStringException e) {
//                Toast.makeText(context, "Invalid graph", Toast.LENGTH_SHORT).show();
//                return;
//            }
//            stack.backup();
//            Rectangle oldRec = state.getRectangle();
//            Rectangle optimalRec = DrawManager.getOptimalRectangle(g, 0.1, oldRec);
//
//            currentState.setGraph(g);
//            state.setRectangle(optimalRec);
//            stack.invalidateView();
//
//            Toast.makeText(context, "Import complete", Toast.LENGTH_SHORT).show();
//            dialog.dismiss();
        });

        builder.setView(popupView);
        dialog = builder.create();
        dialog.show();
    }
}
