package com.example.graph_editor;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.example.graph_editor.model.UndoRedoStack;
import com.example.graph_editor.model.UndoRedoStackImpl;

import org.junit.Test;

public class UndoRedoStackTest {
    @Test
    public void testUndo() {
        UndoRedoStack stack = new UndoRedoStackImpl("1");
        stack.put("2");
        stack.put("3");

        String undo = stack.undo();

        assertEquals("2", undo);
    }

    @Test
    public void testRedo() {
        UndoRedoStack stack = new UndoRedoStackImpl("1");
        stack.put("2");
        stack.put("3");
        stack.undo();
        stack.undo();

        String redo = stack.redo();

        assertEquals("2", redo);
    }

    @Test
    public void testGet() {
        UndoRedoStack stack = new UndoRedoStackImpl("1");
        stack.put("2");
        stack.put("3");

        stack.undo();
        stack.undo();
        String get1 = stack.get();
        stack.redo();
        String get2 = stack.get();

        assertEquals("1", get1);
        assertEquals("2", get2);
    }

    @Test
    public void testUndoUnderflow() {
        UndoRedoStack stack = new UndoRedoStackImpl("1");
        stack.put("2");

        String undo1 = stack.undo();
        String undo2 = stack.undo();

        assertEquals("1", undo1);
        assertEquals("1", undo2);
    }

    @Test
    public void testRedoOverflow() {
        UndoRedoStack stack = new UndoRedoStackImpl("1");
        stack.put("2");
        stack.undo();

        String redo1 = stack.redo();
        String redo2 = stack.redo();

        assertEquals("2", redo1);
        assertEquals("2", redo2);
    }

    @Test
    public void testIsUndoPossible() {
        UndoRedoStack stack = new UndoRedoStackImpl("1");
        stack.put("2");

        boolean possible1 = stack.isUndoPossible();
        stack.undo();
        boolean possible2 = stack.isUndoPossible();

        assertTrue(possible1);
        assertFalse(possible2);
    }


    @Test
    public void testIsRedoPossible() {
        UndoRedoStack stack = new UndoRedoStackImpl("1");
        stack.put("2");
        stack.undo();

        boolean possible1 = stack.isRedoPossible();
        stack.redo();
        boolean possible2 = stack.isRedoPossible();

        assertTrue(possible1);
        assertFalse(possible2);
    }

    @Test
    public void testOverride() {
        UndoRedoStack stack = new UndoRedoStackImpl("1");

        stack.put("2");
        stack.put("3");
        stack.undo();
        stack.undo();
        stack.put("4");
        stack.undo();
        String get1 = stack.get();
        stack.redo();
        String get2 = stack.get();
        boolean possible = stack.isRedoPossible();

        assertEquals("1", get1);
        assertEquals("4", get2);
        assertFalse(possible);
    }
}
