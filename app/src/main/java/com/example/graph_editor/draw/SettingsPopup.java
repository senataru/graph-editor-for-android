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
import com.example.graph_editor.draw.graph_view.GraphView;

public class SettingsPopup {
    private final Context context;
    private final Runnable invalidateFunction;
    private AlertDialog dialog;

    SettingsPopup(Context context, Runnable invalidateFunction) {
        this.context = context;
        this.invalidateFunction = invalidateFunction;
    }

    public void show() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = (LayoutInflater)context.getSystemService( Context.LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.settings_popup, null);

        SwitchCompat btnStylus = popupView.findViewById(R.id.toggle_stylus);
        btnStylus.setOnCheckedChangeListener((b, checked) ->
                Settings.setStylus(context, checked)
        );
        ((Checkable)btnStylus).setChecked(Settings.getStylus(context));

        SwitchCompat btnButtons = popupView.findViewById(R.id.toggle_buttons);
        btnButtons.setOnCheckedChangeListener((b, checked) ->
                Settings.setButtons(context, checked)
        );
        ((Checkable)btnButtons).setChecked(Settings.getButtons(context));

        SwitchCompat btnFixedWidth = popupView.findViewById(R.id.toggle_fixed_width);
        btnFixedWidth.setOnCheckedChangeListener((buttonView, checked) -> {
            Settings.setFixedWidth(context, checked);
            invalidateFunction.run();
        });
        ((Checkable)btnFixedWidth).setChecked(Settings.getFixedWidth(context));


        popupView.findViewById(R.id.settings_popup_close).setOnClickListener(v ->
                dialog.dismiss()
        );

        builder.setView(popupView);
        dialog = builder.create();
        dialog.show();
    }
}
