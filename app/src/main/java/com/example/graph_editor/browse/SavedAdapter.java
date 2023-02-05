package com.example.graph_editor.browse;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.graph_editor.R;
import com.example.graph_editor.draw.graph_action.NewVertex;
import com.example.graph_editor.draw.graph_view.GraphView;
import com.example.graph_editor.draw.popups.ShareAsIntent;
import com.example.graph_editor.extensions.CanvasManagerImpl;
import com.example.graph_editor.file_serialization.Loader;
import com.example.graph_editor.fs.FSDirectories;
import com.example.graph_editor.model.state.State;
import com.example.graph_editor.point_mapping.PointMapperImpl;

import java.io.File;
import java.util.List;

import graph_editor.geometry.Point;
import graph_editor.graph.Graph;
import graph_editor.graph.ObservableStackImpl;
import graph_editor.graph.VersionStack.ObservableStack;
import graph_editor.graph.VersionStackImpl;
import graph_editor.properties.PropertySupportingGraph;
import graph_editor.visual.GraphVisualization;

public class SavedAdapter extends RecyclerView.Adapter<SavedAdapter.Holder> {
    private List<String> data;
//    private final List<VertexPropertySave> vertexPropertySaves;
//    private final List<EdgePropertySave> edgePropertySaves;
    private final BrowseActivity browseActivity;

    SavedAdapter(BrowseActivity browseActivity, List<String> data) {
        this.data = data;
        this.browseActivity = browseActivity;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(browseActivity);
        View view = inflater.inflate(R.layout.saved_row, parent, false);
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        String name = data.get(position);
        holder.txtName.setText(name);
//        String graphString = data.get(position).graph;
//        long saveUid = data.get(position).uid;
//        List<String> vertexPropertySaveStrings = vertexPropertySaves.stream()
//                .filter(propertySave -> propertySave.graphSaveUid == saveUid)
//                .map(propertySave -> propertySave.property)
//                .collect(Collectors.toList());
//        List<String> edgePropertySaveStrings = edgePropertySaves.stream()
//                .filter(propertySave -> propertySave.graphSaveUid == saveUid)
//                .map(propertySave -> propertySave.property)
//                .collect(Collectors.toList());

        GraphVisualization<PropertySupportingGraph> visualization = Loader.load(new File(browseActivity.getFilesDir(), FSDirectories.graphsDirectory), name);

        ObservableStack<GraphVisualization<PropertySupportingGraph>> stack = new ObservableStackImpl<>(new VersionStackImpl<>(visualization));
        State state = new State(
                new NewVertex()
        );
        holder.dataGraph.initialize(new CanvasManagerImpl(), stack, state,false, new PointMapperImpl(holder.dataGraph, new Point(0,0)));

        holder.editButton.setOnClickListener(v -> {
//            Save newSave = data.get(position);
//            SaveDao dao = SavesDatabase.getDbInstance(context).saveDao();
//            dao.updateGraph(newSave.uid, newSave.graph, System.currentTimeMillis());
//            updateAllProperties(newSave);
            browseActivity.changeActivity(name);
        });
        holder.deleteButton.setOnClickListener(v ->
                new ConfirmPopup(browseActivity, holder.dataGraph.getVisualization(), () -> {
                    data.remove(position);
                    notifyItemRemoved(position);
                    notifyItemRangeChanged(position, data.size());
                    File graphsDirectory = new File(browseActivity.getFilesDir(), FSDirectories.graphsDirectory);
                    boolean deleted = new File(graphsDirectory, name).delete();
                    if (!deleted) { throw new RuntimeException("worrisome: delete did not happen"); }
                }).show()
        );
        holder.shareButton.setOnClickListener(
                v -> new ShareAsIntent(browseActivity, holder.dataGraph.getVisualization()).show());
    }
    @Override
    public int getItemCount() {
        return data.size();
    }
    public List<String> getData() {
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
