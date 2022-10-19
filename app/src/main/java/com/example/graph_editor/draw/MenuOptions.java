package com.example.graph_editor.draw;

import static android.view.MenuItem.SHOW_AS_ACTION_ALWAYS;
import static android.view.MenuItem.SHOW_AS_ACTION_NEVER;

import android.util.Pair;

import com.example.graph_editor.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MenuOptions {
    public enum Options {
        Undo(1),
        Redo(2);
        public final int id;
        Options(int id) { this.id = id; }
    }
    //TODO change all to enum?
    public static final List<Integer> alwaysList = List.of(R.string.undo, R.string.redo, R.string.save);
    public static final List<Integer> neverList = List.of(
            R.string.save_as,
            R.string.read_graph_from_text,
            R.string.share_graph_as_text,
            R.string.import_graph_from_file,
            R.string.export_graph_as_file,
            R.string.clear_graph,
            R.string.recenter_graph,
            R.string.settings
    );
    public static final List<Integer> neverSubList = List.of(
            R.string.cycle,
            R.string.clique,
            R.string.bipartite_clique,
            R.string.full_binary_tree,
            R.string.grid,
            R.string.king_grid
    );

    public static final List<Pair<Integer, Integer>> mainCoreOptions =
            Stream.concat(
                    alwaysList.stream().map(it -> Pair.create(it, SHOW_AS_ACTION_ALWAYS)),
                    neverList.stream().map(it -> Pair.create(it, SHOW_AS_ACTION_NEVER))
            ).collect(Collectors.toList());
    public static final List<Pair<Integer, Integer>> coreOptions =
            Stream.concat(
                    mainCoreOptions.stream(),
                    neverSubList.stream().map(it -> Pair.create(it, SHOW_AS_ACTION_NEVER))
            ).collect(Collectors.toList());

    public static Map<Integer, Pair<String, Runnable>> extensionsOptions;
}
