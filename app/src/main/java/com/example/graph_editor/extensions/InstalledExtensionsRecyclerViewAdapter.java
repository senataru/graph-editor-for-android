package com.example.graph_editor.extensions;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SwitchCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.graph_editor.model.extensions.Extension;
import com.example.graph_editor.R;

import java.util.Collection;
import java.util.List;

public class InstalledExtensionsRecyclerViewAdapter
        extends RecyclerView.Adapter<InstalledExtensionsRecyclerViewAdapter.Holder> {
    private final Context context;
    private final List<Extension> installedExtensions;

    public InstalledExtensionsRecyclerViewAdapter(
            Context context,
            List<Extension> installedExtensions) {
        this.context = context;
        this.installedExtensions = installedExtensions;
    }

    @NonNull
    @Override
    public InstalledExtensionsRecyclerViewAdapter.Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.switchcompat_row, parent, false);
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull InstalledExtensionsRecyclerViewAdapter.Holder holder, int position) {
        Extension extension = installedExtensions.get(position);
        holder.compat.setText(extension.getName());
        holder.compat.setChecked(extension.isEnabled());
        holder.compat.setOnCheckedChangeListener((b, checked) -> extension.setEnabled(checked));
    }

    @Override
    public int getItemCount() {
        return installedExtensions.size();
    }

    public final static class Holder extends RecyclerView.ViewHolder {
        private final SwitchCompat compat;
        public Holder(@NonNull View itemView) {
            super(itemView);
            this.compat = itemView.findViewById(R.id.extensionSwitch);
        }
    }
}
