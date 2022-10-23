package com.example.graph_editor.extentions;

import com.example.graph_editor.model.extensions.ExtensionInvoker;

import java.io.Reader;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

public class RhinoJSInvoker implements ExtensionInvoker {
    public static ExtensionInvoker createInstance(Reader scriptReader) {
        RhinoJSInvoker invoker = new RhinoJSInvoker();
        invoker.initialize(scriptReader);
        return invoker;
    }
    private ScriptEngine scriptEngine;
    public void callFunction(String functionName, Object... args) {
        try {
            ((Invocable)scriptEngine).invokeFunction(functionName, args);
        } catch (ScriptException e) {
            e.printStackTrace();
            throw new RuntimeException(e); //TODO display snack-bar instead?
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
            throw new RuntimeException(e); //TODO display snack-bar instead?
        }
    }

    private void initialize(Reader scriptReader) {
        ScriptEngineManager manager = new ScriptEngineManager();
        this.scriptEngine = manager.getEngineByName("rhino");

        try {
            scriptEngine.eval(scriptReader);
        } catch (ScriptException e) {
            e.printStackTrace();
            throw new RuntimeException(e); //TODO display snack-bar instead?
        }
    }
}
