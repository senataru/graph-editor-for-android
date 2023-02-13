package com.example.graph_editor.extensions;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.Button;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.SwitchCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.graph_editor.R;

import java.io.File;
import java.util.List;

import graph_editor.extensions.Extension;

public class InstalledExtensionsRecyclerViewAdapter
        extends RecyclerView.Adapter<InstalledExtensionsRecyclerViewAdapter.Holder> implements ExtensionPositionGetter{
    private final Context context;
    private final List<Extension> installedExtensions;

    private final OnExtensionDeleteClicked callback;

    public InstalledExtensionsRecyclerViewAdapter(
            Context context,
            List<Extension> installedExtensions,
            OnExtensionDeleteClicked callback) {
        this.context = context;
        this.installedExtensions = installedExtensions;
        this.callback = callback;
    }

    @NonNull
    @Override
    public InstalledExtensionsRecyclerViewAdapter.Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.installed_extension_row, parent, false);
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull InstalledExtensionsRecyclerViewAdapter.Holder holder, int position) {
        Extension extension = installedExtensions.get(position);
        holder.compat.setText(extension.getName());
        holder.compat.setChecked(extension.isEnabled());
        holder.compat.setOnCheckedChangeListener((b, checked) -> extension.setEnabled(checked));
        enableButton(holder.button);
        holder.button.setOnClickListener(v -> {
            disableButton(holder.button);
            callback.onDeleteClicked(extension.getName());
            System.out.println(position);
            System.out.println(extension.getName());
        });
    }

    @Override
    public int getItemCount() {
        return installedExtensions.size();
    }

    public int getExtensionPos(String extensionName){
        int i=0;
        for(Extension e: installedExtensions){
            if(e.getName().equals(extensionName))
                return i;
            i++;
        }
        return -1;
    }

    public void addExtension(Extension e){
        installedExtensions.add(e);
    }

    private void enableButton(Button b) {
        b.setText(R.string.delete);
        b.setEnabled(true);
        b.setBackgroundColor(Color.GREEN);
    }

    private void disableButton(Button b) {
        b.setText(R.string.deleted);
        b.setEnabled(false);
        b.setBackgroundColor(Color.DKGRAY);
    }

    public final static class Holder extends RecyclerView.ViewHolder {
        private final SwitchCompat compat;
        private final Button button;
        public Holder(@NonNull View itemView) {
            super(itemView);
            this.compat = itemView.findViewById(R.id.extensionSwitch);
            this.button = itemView.findViewById(R.id.extensionButton);
        }
    }
}
