package com.example.graph_editor.draw;

import static android.content.ClipDescription.MIMETYPE_TEXT_PLAIN;

import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Checkable;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import androidx.appcompat.widget.SwitchCompat;

import com.example.graph_editor.R;

public class SettingsPopup {
    private final Context context;
    private AlertDialog dialog;

    SettingsPopup(Context context) {
        this.context = context;
    }

    public void show() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = (LayoutInflater)context.getSystemService( Context.LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.settings_popup, null);

        ((SwitchCompat)popupView.findViewById(R.id.toggle_stylus)).setOnCheckedChangeListener((b, checked) ->
                Settings.setStylus(context, checked)
        );
        ((Checkable)popupView.findViewById(R.id.toggle_stylus)).setChecked(Settings.getStylus(context));

        ((SwitchCompat)popupView.findViewById(R.id.toggle_buttons)).setOnCheckedChangeListener((b, checked) ->
                Settings.setButtons(context, checked)
        );
        ((Checkable)popupView.findViewById(R.id.toggle_buttons)).setChecked(Settings.getButtons(context));

        popupView.findViewById(R.id.settings_popup_close).setOnClickListener(v ->
                dialog.dismiss()
        );

        builder.setView(popupView);
        dialog = builder.create();
        dialog.show();
    }
}
