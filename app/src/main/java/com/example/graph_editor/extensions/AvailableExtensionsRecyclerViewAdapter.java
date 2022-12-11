package com.example.graph_editor.extensions;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.graph_editor.R;
import com.example.graph_editor.model.extensions.ExtensionsClient;
import com.example.graph_editor.model.extensions.ExtensionsRepository;

import java.util.List;

public class AvailableExtensionsRecyclerViewAdapter
        extends RecyclerView.Adapter<AvailableExtensionsRecyclerViewAdapter.Holder> {
    private final Context context;
    private final List<String> availableExtensions;
    private final OnExtensionInstallClicked callback;
    private final ExtensionsRepository repository;

    public AvailableExtensionsRecyclerViewAdapter(
            Context context,
            List<String> availableExtensions,
            ExtensionsRepository repository,
            OnExtensionInstallClicked onExtensionInstallClicked) {
        this.context = context;
        this.availableExtensions = availableExtensions;
        this.repository = repository;
        this.callback = onExtensionInstallClicked;
    }

    @NonNull
    @Override
    public AvailableExtensionsRecyclerViewAdapter.Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.available_extension_row, parent, false);
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AvailableExtensionsRecyclerViewAdapter.Holder holder, int position) {
        String extensionName = availableExtensions.get(position);
        holder.name.setText(extensionName);
        if (repository.isPresent(extensionName)) {
            disableButton(holder.install);
        } else {
            enableButton(holder.install);
            holder.install.setOnClickListener(v -> {
                blockButton(holder.install);
                callback.onInstallClicked(extensionName);
                disableButton(holder.install);
            });
        }
    }

    @Override
    public int getItemCount() {
        return availableExtensions.size();
    }

    private void enableButton(Button b) {
        b.setText(R.string.install);
        b.setEnabled(true);
        b.setBackgroundColor(Color.GREEN);
    }
    private void blockButton(Button b) {
        b.setText(R.string.installing);
        b.setEnabled(false);
        b.setBackgroundColor(Color.MAGENTA);
    }
    private void disableButton(Button b) {
        b.setText(R.string.installed);
        b.setEnabled(false);
        b.setBackgroundColor(Color.DKGRAY);
    }

    public final static class Holder extends RecyclerView.ViewHolder {
        private final TextView name;
        private final Button install;

        public Holder(@NonNull View itemView) {
            super(itemView);
            this.name = itemView.findViewById(R.id.availableName);
            this.install = itemView.findViewById(R.id.availableButton);
        }
    }
}
