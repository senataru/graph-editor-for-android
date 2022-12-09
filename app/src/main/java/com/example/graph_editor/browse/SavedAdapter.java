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
import com.example.graph_editor.database.PropertySave;
import com.example.graph_editor.database.PropertySaveDao;
import com.example.graph_editor.database.Save;
import com.example.graph_editor.database.SaveDao;
import com.example.graph_editor.database.SavesDatabase;
import com.example.graph_editor.draw.action_mode_type.GraphAction;
import com.example.graph_editor.draw.graph_view.GraphView;
import com.example.graph_editor.draw.popups.ShareAsTxtIntent;
import com.example.graph_editor.extensions.CanvasManagerImpl;
import com.example.graph_editor.model.graph_storage.GraphScanner;
import com.example.graph_editor.model.Graph;
import com.example.graph_editor.model.mathematics.Point;
import com.example.graph_editor.model.mathematics.Rectangle;
import com.example.graph_editor.model.state.State;
import com.example.graph_editor.model.state.StateStack;
import com.example.graph_editor.model.state.StateStackImpl;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SavedAdapter extends RecyclerView.Adapter<SavedAdapter.Holder> {
    private final Context context;
    private List<Save> data;
    private final List<PropertySave> propertySaves;
    private final BrowseActivity browseActivity;

    SavedAdapter(Context context, List<Save> data, List<PropertySave> propertySaves,
                 BrowseActivity browseActivity) {
        this.context = context;
        this.data = data;
        this.propertySaves = propertySaves;
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
        List<String> propertySaveStrings = propertySaves.stream()
                .filter(propertySave -> propertySave.graphSaveUid == saveUid)
                .map(propertySave -> propertySave.property)
                .collect(Collectors.toList());
        try {
            Graph graph = GraphScanner.fromExact(graphString);
            for (String propertyString: propertySaveStrings) {
                GraphScanner.addVertexProperty(graph, propertyString);
            }
            StateStack stack = new StateStackImpl(
                () -> {},
                new State(graph, new Rectangle(new Point(0, 0), new Point(1, 1)), new GraphAction.MoveCanvas())
            );
            holder.dataGraph.initialize(new CanvasManagerImpl(), stack, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
        holder.editButton.setOnClickListener(v -> {
            Save s = data.get(position);
            SaveDao dao = SavesDatabase.getDbInstance(context).saveDao();
            PropertySaveDao propertyDao = SavesDatabase.getDbInstance(context).propertySaveDao();
            dao.updateGraph(s.uid, s.graph, System.currentTimeMillis());
            propertySaves.stream()
                    .filter(save -> save.graphSaveUid == s.uid)
                    .forEach(save -> propertyDao.updateProperty(
                            save.uid, save.property, System.currentTimeMillis()));
            browseActivity.changeActivity(graphString, data.get(position).uid, propertySaveStrings);
        });
        holder.deleteButton.setOnClickListener(v ->
                new ConfirmPopup(context, holder.dataGraph.getStateStack().getCurrentState().getGraph(), () -> {
                    Save s = data.get(position);
                    data.remove(position);
                    notifyItemRemoved(position);
                    notifyItemRangeChanged(position, data.size());
                    SavesDatabase.getDbInstance(context).saveDao().delete(s);
                }).show()
        );
        holder.shareButton.setOnClickListener(v -> new ShareAsTxtIntent(context, holder.dataGraph.getStateStack()).show());
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
