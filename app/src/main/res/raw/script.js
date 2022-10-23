var option_id = 0;

function activate(coreProxy /* com.example.graph_editor.extentions.ScriptProxy*/) {
    option_id = coreProxy.registerGraphMenuOption("Extension's option", "foo");
    coreProxy.customizeVertexDrawingBehaviour("drawVertex");
}

function addGraphElement(elementType/* string*/, graph) {
//com.example.graph_editor.draw.graph_view.extensions
}

function canvasX(x, rectangle, canvas) {
    return (x - rectangle.getLeft()) * canvas.getWidth() / rectangle.getWidth();
}
function canvasY(y, rectangle, canvas) {
    return (y - rectangle.getTop()) * canvas.getHeight() / rectangle.getHeight();

}

function drawVertex(point, rectangle, canvas) {
    var Paint = android.graphics.Paint;
    var aPaint = new Paint();
    var ColorClass = android.graphics.Color;
    aPaint.setARGB(255, 255, 0, 255);
    aPaint.setStyle(Paint.Style.FILL);
    aPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
    var cx = canvasX(point.getX(), rectangle, canvas);
    var cy = canvasY(point.getY(), rectangle, canvas);

    canvas.drawCircle(cx, cy, 0.007 * canvas.getWidth() / rectangle.getWidth(), aPaint);
}

function foo(context) {
    var Toast = Packages.android.widget.Toast;
    Toast.makeText(context, "You tapped Extension's option!", Toast.LENGTH_LONG).show();
}

// This method is called when your extension is deactivated
function deactivate(coreProxy/* com.example.graph_editor.extentions.ScriptProxy*/) {
    coreProxy.deregisterGraphMenuOption(option_id);
    coreProxy.restoreDefaultVertexDrawingBehaviour();
}
