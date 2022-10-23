package com.example.graph_editor.draw;

import android.util.Pair;

import com.example.graph_editor.model.extensions.GraphMenuManager;

import java.util.Map;

public class MenuOptions {
    public static Map<Integer, Pair<String, GraphMenuManager.OnSelection>> extensionsOptions;
//    public static final List<Integer> alwaysList = List.of(R.string.undo, R.string.redo, R.string.save);
//    public static final List<Integer> neverList = List.of(
//            R.string.save_as,
//            R.string.read_graph_from_text,
//            R.string.share_graph_as_text,
//            R.string.import_graph_from_file,
//            R.string.export_graph_as_file,
//            R.string.clear_graph,
//            R.string.recenter_graph,
//            R.string.settings
//    );
//    public static final List<Integer> neverSubList = List.of(
//            R.string.cycle,
//            R.string.clique,
//            R.string.bipartite_clique,
//            R.string.full_binary_tree,
//            R.string.grid,
//            R.string.king_grid
//    );
//
//    public static final List<Pair<Integer, Integer>> mainCoreOptions =
//            Stream.concat(
//                    alwaysList.stream().map(it -> Pair.create(it, SHOW_AS_ACTION_ALWAYS)),
//                    neverList.stream().map(it -> Pair.create(it, SHOW_AS_ACTION_NEVER))
//            ).collect(Collectors.toList());
//    public static final List<Pair<Integer, Integer>> coreOptions =
//            Stream.concat(
//                    mainCoreOptions.stream(),
//                    neverSubList.stream().map(it -> Pair.create(it, SHOW_AS_ACTION_NEVER))
//            ).collect(Collectors.toList());
}
