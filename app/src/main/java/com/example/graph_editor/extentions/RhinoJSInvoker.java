package com.example.graph_editor.extentions;

import java.io.FileNotFoundException;
import java.io.FileReader;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

public class RhinoJSInvoker {
    interface CoreApplicationContext {
        int RegisterGraphMenuOption(String optionName, String functionCalledOnOptionSelected);
        void DeregisterGraphMenuOption(long id);
    }

    private final ScriptEngine scriptEngine;
    private final CoreApplicationContext coreApplicationContext;
    RhinoJSInvoker(String extensionPath, CoreApplicationContext coreApplicationContext) {
        ScriptEngineManager manager = new ScriptEngineManager();
        this.scriptEngine = manager.getEngineByName("rhino");
        this.coreApplicationContext = coreApplicationContext;
        try {
            scriptEngine.eval(new FileReader(extensionPath));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            throw new RuntimeException(e); //TODO display snack-bar instead?
        } catch (ScriptException e) {
            e.printStackTrace();
            throw new RuntimeException(e); //TODO display snack-bar instead?
        }
    }
    public void callJSFunction(String functionName) {
        try {
            ((Invocable)scriptEngine).invokeFunction(functionName, coreApplicationContext);
        } catch (ScriptException e) {
            e.printStackTrace();
            throw new RuntimeException(e); //TODO display snack-bar instead?
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
            throw new RuntimeException(e); //TODO display snack-bar instead?
        }
    }
}
