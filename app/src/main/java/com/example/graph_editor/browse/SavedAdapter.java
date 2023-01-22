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
import com.example.graph_editor.database.EdgePropertySaveDao;
import com.example.graph_editor.database.VertexPropertySaveDao;
import com.example.graph_editor.database.Save;
import com.example.graph_editor.database.SavesDatabase;
import com.example.graph_editor.draw.graph_action.GraphAction;
import com.example.graph_editor.draw.graph_view.GraphView;
import com.example.graph_editor.draw.popups.ShareAsIntent;
import com.example.graph_editor.extensions.CanvasManagerImpl;
import com.example.graph_editor.file_serialization.Loader;
import com.example.graph_editor.file_serialization.SerializationConstants;
import com.example.graph_editor.model.graph_storage.InvalidGraphStringException;
import com.example.graph_editor.model.mathematics.Rectangle;
import com.example.graph_editor.model.state.State;

import java.util.List;

import graph_editor.geometry.Point;
import graph_editor.graph.Graph;
import graph_editor.graph.ObservableStackImpl;
import graph_editor.graph.VersionStack.ObservableStack;
import graph_editor.graph.VersionStackImpl;
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

        GraphVisualization visualization = Loader.load(browseActivity, SerializationConstants.savesDirectory + name);

        ObservableStack<GraphVisualization> stack = new ObservableStackImpl<>(new VersionStackImpl<>(visualization));
        State state = new State(
                new Rectangle(new Point(0, 0), new Point(1, 1)),
                new GraphAction.MoveCanvas()
        );
        holder.dataGraph.initialize(new CanvasManagerImpl(), stack, state,false);

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
                    boolean deleted = browseActivity.deleteFile(SerializationConstants.savesDirectory + name);
                    if (!deleted) { throw new RuntimeException("worrisome: delete did not happen"); }
                }).show()
        );
        holder.shareButton.setOnClickListener(
                v -> new ShareAsIntent(browseActivity, holder.dataGraph.getVisualization()).show());
    }

    //do not use: disabled functionality, if you want to retrieve it, remember to remove all '/' marks
    private void addAllProperties(Graph graph, List<String> vertexPropertySaveStrings, List<String> edgePropertySaveStrings)
            throws InvalidGraphStringException {
        for (String vertexPropertyString: vertexPropertySaveStrings) {
//            GraphScanner.addVertexProperty(graph, vertexPropertyString);
        }
        for (String edgePropertyString: edgePropertySaveStrings) {
//            GraphScanner.addEdgeProperty(graph, edgePropertyString);
        }
    }
    //do not use: disabled functionality, if you want to retrieve it, remember to remove all '/' marks
    private void updateAllProperties(Save newSave) {
//        VertexPropertySaveDao vertexPropertyDao = SavesDatabase.getDbInstance(context).vertexPropertySaveDao();
//        EdgePropertySaveDao edgePropertyDao = SavesDatabase.getDbInstance(context).edgePropertySaveDao();
//        vertexPropertySaves.stream()
//                .filter(save -> save.graphSaveUid == newSave.uid)
//                .forEach(save -> vertexPropertyDao.updateProperty(
//                        save.uid, save.property, System.currentTimeMillis()));
//        edgePropertySaves.stream()
//                .filter(save -> save.graphSaveUid == newSave.uid)
//                .forEach(save -> edgePropertyDao.updateProperty(
//                        save.uid, save.property, System.currentTimeMillis()));
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    //do not use: disabled functionality, if you want to retrieve it, remember to remove all '/' marks
//    @SuppressLint("NotifyDataSetChanged")
//    public void update(List<Save> data) {
//        this.data = data;
//        notifyDataSetChanged();
//    }
//
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
