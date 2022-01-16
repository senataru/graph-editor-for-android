package com.example.graph_editor.draw;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

public enum ActionModeType {
    NEW_VERTEX, NEW_EDGE, MOVE_OBJECT, MOVE_CANVAS, ZOOM_CANVAS, REMOVE_OBJECT;

    private static final List<ActionModeTypeObserver> observers = new ArrayList<>();
    private static volatile ActionModeType currentType = ActionModeType.MOVE_CANVAS;

    public static synchronized void addObserver(ActionModeTypeObserver observer) {
        observers.add(observer);
    }

    public static synchronized void removeObserver(ActionModeTypeObserver observer) {
        observers.remove(observer);
    }

    private static synchronized void tellObservers() {
        for (ActionModeTypeObserver o : observers) {
            o.update(currentType);
        }
    }

    public static synchronized ActionModeType getCurrentModeType() {
        return currentType;
    }

    public static synchronized void setCurrentModeType(ActionModeType modeType) {
        currentType = modeType;
        tellObservers();
    }

    public static synchronized void resetCurrentModeType() {
        setCurrentModeType(ActionModeType.MOVE_CANVAS);
    }
}
