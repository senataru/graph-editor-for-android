// import * as coreApp from 'coreApp';

var option_id = 0;

function activate(context /* :coreApp.CoreContext*/) {
    option_id = context.registerGraphMenuOption("find longest cycle", "foo");
}

function foo(context/* :coreApp.CoreContext*/) {
    context.print("from example.ts");
}

// This method is called when your extension is deactivated
function deactivate(context/* :coreApp.CoreContext*/) {
    context.deregisterGraphMenuOption(option_id);
}
