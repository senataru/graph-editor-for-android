package com.example.graph_editor.browse;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.graph_editor.R;
import com.example.graph_editor.database.Save;
import com.example.graph_editor.database.SavesDatabase;
import com.example.graph_editor.draw.DrawActivity;
import com.example.graph_editor.draw.graph_view.GraphView;
import com.example.graph_editor.graphStorage.GraphScanner;
import com.example.graph_editor.model.Graph;

import java.util.List;
import java.util.function.Function;

public class SavedAdapter extends RecyclerView.Adapter<SavedAdapter.Holder> {
    Context context;
    List<Save> data;
    BrowseActivity browseActivity;
    SavedAdapter(Context context, List<Save> data, BrowseActivity browseActivity) {
        this.context = context;
        this.data = data;
        this.browseActivity = browseActivity;
    }
    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.saved_row, parent, false);
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        holder.txtName.setText(data.get(position).name);
        String graphString = data.get(position).graph;
        try {
            Graph graph = GraphScanner.fromExact(graphString);
            holder.dataGraph.initializeGraph(graph.getDrawManager(), false);
        } catch (Exception e) {
            e.printStackTrace();
        }
        holder.editButton.setOnClickListener(v -> browseActivity.changeActivity(graphString));
        holder.deleteButton.setOnClickListener(v -> {
            Save s = data.get(position);
            data.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, data.size());
            SavesDatabase.getDbInstance(context).saveDao().delete(s);
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void update(List<Save> data) {
        this.data = data;
        notifyDataSetChanged();
    }

    static class Holder extends RecyclerView.ViewHolder {
        TextView txtName;
        GraphView dataGraph;
        Button editButton;
        Button deleteButton;

        public Holder(@NonNull View itemView) {
            super(itemView);
            txtName = itemView.findViewById(R.id.txtName);
            dataGraph = itemView.findViewById(R.id.dataGraph);
            editButton = itemView.findViewById(R.id.btnEdit);
            deleteButton = itemView.findViewById(R.id.btnDelete);
        }
    }
}
