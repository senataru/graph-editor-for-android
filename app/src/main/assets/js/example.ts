import * as coreApp from 'coreApp';

let option_id : number

export function activate(context : coreApp.CoreContext) {
    option_id = context.registerGraphMenuOption("find longest cycle", "foo")
}

function foo(context : coreApp.CoreContext) {

}

// This method is called when your extension is deactivated
export function deactivate(context : coreApp.CoreContext) {
    context.deregisterGraphMenuOption(option_id)
}