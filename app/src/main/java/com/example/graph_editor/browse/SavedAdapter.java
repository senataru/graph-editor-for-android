package com.example.graph_editor.browse;

import android.content.Context;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.graph_editor.R;
import com.example.graph_editor.database.Save;
import com.example.graph_editor.draw.GraphView;
import com.example.graph_editor.graphStorage.GraphScanner;
import com.example.graph_editor.model.Graph;

import java.util.List;

public class SavedAdapter extends RecyclerView.Adapter<SavedAdapter.Holder>{
    Context context;
    List<Save> data;
    SavedAdapter(Context context, List<Save> data) {
        this.context = context;
        this.data = data;
    }
    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.saved_row, parent, false);
        ((TextView)view.findViewById(R.id.txtName)).setMovementMethod(new ScrollingMovementMethod());
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        holder.txtName.setText(data.get(position).name);
        String graphString = String.valueOf(data.get(position).graph);
        try {
            Graph graph = GraphScanner.fromExact(graphString);
            holder.dataGraph.initializeGraph(graph.getDrawManager(), false);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public int getItemCount() {
        return data.size();
    }
    static class Holder extends RecyclerView.ViewHolder {
        TextView txtName;
        GraphView dataGraph;

        public Holder(@NonNull View itemView) {
            super(itemView);
            txtName = itemView.findViewById(R.id.txtName);
            dataGraph = itemView.findViewById(R.id.dataGraph);
        }
    }
}
