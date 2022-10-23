var option_id = 0;
var Paint = android.graphics.Paint;
var Debug = com.example.graph_editor.draw.graph_view.extensions.DebugCanvas
function activate(coreProxy /* com.example.graph_editor.extentions.ScriptProxy*/) {
    option_id = coreProxy.registerGraphMenuOption("Extension's option", "foo");
    coreProxy.customizeVertexDrawingBehaviour("drawVertex");
    coreProxy.customizeEdgeDrawingBehaviour("drawEdge");
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
    var aPaint = new Paint;
    aPaint.setARGB(255, 255, 0, 255);
    aPaint.setStyle(Paint.Style.FILL);
    aPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
    var cx = canvasX(point.getX(), rectangle, canvas);
    var cy = canvasY(point.getY(), rectangle, canvas);

    var strokePaint1 = new Paint;
    strokePaint1.setStyle(Paint.Style.STROKE);
    strokePaint1.setARGB(255, 0, 255, 0);
    strokePaint1.setStrokeWidth(0.005 * canvas.getWidth() / rectangle.getWidth());
    strokePaint1.setStrokeCap(Paint.Cap.ROUND);

    var strokePaint2 = new Paint;
    strokePaint2.setStyle(Paint.Style.STROKE);
    strokePaint2.setARGB(255, 255, 0, 0);
    strokePaint2.setStrokeWidth(0.005 * canvas.getWidth() / rectangle.getWidth());
    strokePaint2.setStrokeCap(Paint.Cap.ROUND);

    canvas.drawCircle(cx, cy, 0.005 * canvas.getWidth() / rectangle.getWidth(), aPaint);
    canvas.drawCircle(cx, cy, 0.005 * canvas.getWidth() / rectangle.getWidth(), strokePaint1);
    canvas.drawCircle(cx, cy, 0.010 * canvas.getWidth() / rectangle.getWidth(), strokePaint2);
}

function drawEdge(p1, p2, rectangle, canvas) {

    var edgePaint = new Paint();
    edgePaint.setARGB(255, 255, 255, 0);
    edgePaint.setStyle(Paint.Style.STROKE);
    edgePaint.setStrokeWidth(0.005 * canvas.getWidth() / rectangle.getWidth());
    edgePaint.setFlags(Paint.ANTI_ALIAS_FLAG);

    var x1 = canvasX(p1.getX(), rectangle, canvas);
    var y1 = canvasY(p1.getY(), rectangle, canvas);
    var x2 = canvasX(p2.getX(), rectangle, canvas);
    var y2 = canvasY(p2.getY(), rectangle, canvas);

//    float dx = x2-x1;
//    float dy = y2-y1;
//    if (dx*dx + dy*dy < 0.1*0.1) return;

    canvas.drawLine(x1, y1, x2, y2, edgePaint);
//    if (type == GraphType.DIRECTED) {
//        drawArrow(edgePaint, canvas, x1, y1, x2, y2);
//    }
}

function foo(context) {
    var Toast = Packages.android.widget.Toast;
    Toast.makeText(context, "You tapped Extension's option!", Toast.LENGTH_LONG).show();
}

// This method is called when your extension is deactivated
function deactivate(coreProxy/* com.example.graph_editor.extentions.ScriptProxy*/) {
    coreProxy.deregisterGraphMenuOption(option_id);
    coreProxy.restoreDefaultVertexDrawingBehaviour();
    coreProxy.restoreDefaultEdgeDrawingBehaviour();
}
