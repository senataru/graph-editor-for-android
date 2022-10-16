package com.example.graph_editor.extentions;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SwitchCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.graph_editor.R;
import com.example.graph_editor.draw.Settings;
import com.example.graph_editor.extentions.model.Extension;

import java.util.List;

public class ExtensionsRecyclerViewAdapter
        extends RecyclerView.Adapter<ExtensionsRecyclerViewAdapter.Holder> {
    private final Context context;
    private final List<Extension> data;

    public ExtensionsRecyclerViewAdapter(Context context, List<Extension> data) {
        this.context = context;
        this.data = data;
    }

    @NonNull
    @Override
    public ExtensionsRecyclerViewAdapter.Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.switchcompat_row, parent, false);
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ExtensionsRecyclerViewAdapter.Holder holder, int position) {
        Extension extension = data.get(position);
        holder.compat.setText(extension.getName());
        holder.compat.setChecked(extension.isEnabled());
        holder.compat.setOnCheckedChangeListener((b, checked) -> extension.setEnabled(checked));
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public final static class Holder extends RecyclerView.ViewHolder {
        private final SwitchCompat compat;
        public Holder(@NonNull View itemView) {
            super(itemView);
            this.compat = itemView.findViewById(R.id.extensionSwitch);

        }
    }
}
