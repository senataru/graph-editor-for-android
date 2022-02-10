package com.example.graph_editor.draw.popups;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.example.graph_editor.R;

public class MessagePopup {
    Context context;
    String value;

    static AlertDialog dialog;

    public MessagePopup(Context context, String value) {
        this.context = context;
        this.value = value;
    }

    public void show() {
//        if (dialog != null) return;

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = (LayoutInflater)context.getSystemService( Context.LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.message_popup, null);

        TextView tv = popupView.findViewById(R.id.txt_message);
        tv.setText(value);

        builder.setView(popupView);
        dialog = builder.create();
        dialog.show();
    }
}
