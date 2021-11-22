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
        ((TextView)view.findViewById(R.id.data2Text)).setMovementMethod(new ScrollingMovementMethod());
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        holder.data1Text.setText(data.get(position).name);
        holder.data2Text.setText(String.valueOf(data.get(position).graph));
    }

    @Override
    public int getItemCount() {
        return data.size();
    }
    static class Holder extends RecyclerView.ViewHolder {
        TextView data1Text;
        TextView data2Text;

        public Holder(@NonNull View itemView) {
            super(itemView);
            data1Text = itemView.findViewById(R.id.data1Text);
            data2Text = itemView.findViewById(R.id.data2Text);
        }
    }
}
