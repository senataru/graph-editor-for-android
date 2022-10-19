package com.example.graph_editor.extentions;

import android.content.res.Resources;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

import com.example.graph_editor.R;
import com.example.graph_editor.extentions.model.GraphMenuManager;
import com.example.graph_editor.extentions.model.JSInvoker;

import java.io.FileNotFoundException;
import java.io.FileReader;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

public class RhinoJSInvoker implements JSInvoker, JSInvoker.CoreApplicationContext {
    public static JSInvoker createInstance(String extensionPath, GraphMenuManager graphMenuManager) {
        RhinoJSInvoker invoker = new RhinoJSInvoker(graphMenuManager);
        invoker.initialize(extensionPath);
        return invoker;
    }
    private ScriptEngine scriptEngine;
    private final CoreApplicationContext coreApplicationContext = this;
    private final GraphMenuManager graphMenuManager;
    private RhinoJSInvoker(GraphMenuManager graphMenuManager) {
//        coreApplicationContext = new Context();
        this.graphMenuManager = graphMenuManager;
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
    private static String defaultScript = "// import * as coreApp from 'coreApp';\n" +
            "\n" +
            "var option_id = 0;\n" +
            "\n" +
            "function activate(context /* :coreApp.CoreContext*/) {\n" +
            "    option_id = context.registerGraphMenuOption(\"find longest cycle\", \"foo\");\n" +
            "}\n" +
            "\n" +
            "function foo(context/* :coreApp.CoreContext*/) {\n" +
            "    context.print(\"from example.ts\")\n" +
            "}\n" +
            "\n" +
            "// This method is called when your extension is deactivated\n" +
            "function deactivate(context/* :coreApp.CoreContext*/) {\n" +
            "    context.deregisterGraphMenuOption(option_id);\n" +
            "}\n";
    private void initialize(String extensionPath) {
        ScriptEngineManager manager = new ScriptEngineManager();
        this.scriptEngine = manager.getEngineByName("rhino");

        try {
            if (extensionPath != null) scriptEngine.eval(new FileReader(extensionPath));
            //TODO change
            else scriptEngine.eval(defaultScript);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            throw new RuntimeException(e); //TODO display snack-bar instead?
        } catch (ScriptException e) {
            e.printStackTrace();
            throw new RuntimeException(e); //TODO display snack-bar instead?
        }
    }
    //TODO instead of inner class create casual concrete class
//    public final class Context implements JSInvoker.CoreApplicationContext {
        @Override
        public int registerGraphMenuOption(String optionName, String functionCalledOnOptionSelected) {
            return graphMenuManager.registerOption(
                    optionName,
                    () -> callJSFunction(functionCalledOnOptionSelected)
            );
        }

        @Override
        public void deregisterGraphMenuOption(int id) {
            graphMenuManager.deregisterOption(id);
        }

        @Override
        public void print(String message) {
            System.out.println(message);

        }
//    }
}
