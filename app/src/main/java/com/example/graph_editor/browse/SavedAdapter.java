package com.example.graph_editor.browse;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.graph_editor.R;
import com.example.graph_editor.database.EdgePropertySave;
import com.example.graph_editor.database.EdgePropertySaveDao;
import com.example.graph_editor.database.VertexPropertySave;
import com.example.graph_editor.database.VertexPropertySaveDao;
import com.example.graph_editor.database.Save;
import com.example.graph_editor.database.SaveDao;
import com.example.graph_editor.database.SavesDatabase;
import com.example.graph_editor.draw.graph_action.GraphAction;
import com.example.graph_editor.draw.graph_view.GraphView;
import com.example.graph_editor.draw.popups.ShareAsTxtIntent;
import com.example.graph_editor.extensions.CanvasManagerImpl;
import com.example.graph_editor.model.graph_storage.InvalidGraphStringException;
import com.example.graph_editor.model.mathematics.Rectangle;
import com.example.graph_editor.model.state.State;

import java.util.List;
import java.util.stream.Collectors;

import graph_editor.geometry.Point;
import graph_editor.graph.Graph;
import graph_editor.graph.ObservableStackImpl;
import graph_editor.graph.VersionStack.ObservableStack;
import graph_editor.graph.VersionStackImpl;
import graph_editor.visual.GraphVisualization;

public class SavedAdapter extends RecyclerView.Adapter<SavedAdapter.Holder> {
    private final Context context;
    private List<Save> data;
    private final List<VertexPropertySave> vertexPropertySaves;
    private final List<EdgePropertySave> edgePropertySaves;
    private final BrowseActivity browseActivity;

    SavedAdapter(Context context, List<Save> data, List<VertexPropertySave> vertexPropertySaves,
                 List<EdgePropertySave> edgePropertySaves, BrowseActivity browseActivity) {
        this.context = context;
        this.data = data;
        this.vertexPropertySaves = vertexPropertySaves;
        this.edgePropertySaves = edgePropertySaves;
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
        long saveUid = data.get(position).uid;
        List<String> vertexPropertySaveStrings = vertexPropertySaves.stream()
                .filter(propertySave -> propertySave.graphSaveUid == saveUid)
                .map(propertySave -> propertySave.property)
                .collect(Collectors.toList());
        List<String> edgePropertySaveStrings = edgePropertySaves.stream()
                .filter(propertySave -> propertySave.graphSaveUid == saveUid)
                .map(propertySave -> propertySave.property)
                .collect(Collectors.toList());
        try {
            Graph graph = GraphScanner.fromExact(graphString);
            addAllProperties(graph, vertexPropertySaveStrings, edgePropertySaveStrings);

            //TODO initialize
            GraphVisualization visualization = null;
            ObservableStack<GraphVisualization> stack = new ObservableStackImpl<>(new VersionStackImpl<>(visualization));
            State state = new State(
                    new Rectangle(new Point(0, 0), new Point(1, 1)),
                    new GraphAction.MoveCanvas()
            );
            holder.dataGraph.initialize(new CanvasManagerImpl(), stack, state,false);
        } catch (Exception e) {
            e.printStackTrace();
        }
        holder.editButton.setOnClickListener(v -> {
            Save newSave = data.get(position);
            SaveDao dao = SavesDatabase.getDbInstance(context).saveDao();
            dao.updateGraph(newSave.uid, newSave.graph, System.currentTimeMillis());
            updateAllProperties(newSave);

            browseActivity.changeActivity(graphString, data.get(position).uid,
                    vertexPropertySaveStrings, edgePropertySaveStrings);
        });
        holder.deleteButton.setOnClickListener(v ->
                new ConfirmPopup(context, holder.dataGraph.getCurrentGraph(), visualization, () -> {
                    Save s = data.get(position);
                    data.remove(position);
                    notifyItemRemoved(position);
                    notifyItemRangeChanged(position, data.size());
                    SavesDatabase.getDbInstance(context).saveDao().delete(s);
                }).show()
        );
        holder.shareButton.setOnClickListener(
                v -> new ShareAsTxtIntent(context, holder.dataGraph.getCurrentGraph()).show());
    }

    private void addAllProperties(Graph graph, List<String> vertexPropertySaveStrings, List<String> edgePropertySaveStrings)
            throws InvalidGraphStringException {
        for (String vertexPropertyString: vertexPropertySaveStrings) {
            GraphScanner.addVertexProperty(graph, vertexPropertyString);
        }
        for (String edgePropertyString: edgePropertySaveStrings) {
            GraphScanner.addEdgeProperty(graph, edgePropertyString);
        }
    }

    private void updateAllProperties(Save newSave) {
        VertexPropertySaveDao vertexPropertyDao = SavesDatabase.getDbInstance(context).vertexPropertySaveDao();
        EdgePropertySaveDao edgePropertyDao = SavesDatabase.getDbInstance(context).edgePropertySaveDao();
        vertexPropertySaves.stream()
                .filter(save -> save.graphSaveUid == newSave.uid)
                .forEach(save -> vertexPropertyDao.updateProperty(
                        save.uid, save.property, System.currentTimeMillis()));
        edgePropertySaves.stream()
                .filter(save -> save.graphSaveUid == newSave.uid)
                .forEach(save -> edgePropertyDao.updateProperty(
                        save.uid, save.property, System.currentTimeMillis()));
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

    public List<Save> getData() {
        return data;
    }

    public static class Holder extends RecyclerView.ViewHolder {
        TextView txtName;
        GraphView dataGraph;
        ImageButton editButton;
        ImageButton deleteButton;
        ImageButton shareButton;

        public Holder(@NonNull View itemView) {
            super(itemView);
            txtName = itemView.findViewById(R.id.txtName);
            dataGraph = itemView.findViewById(R.id.dataGraph);
            editButton = itemView.findViewById(R.id.btnEdit);
            deleteButton = itemView.findViewById(R.id.btnDelete);
            shareButton = itemView.findViewById(R.id.btnShare);
        }
    }
}
