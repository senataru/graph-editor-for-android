var Utils = com.example.graph_editor.draw.Utils
var Paint = android.graphics.Paint;
var Toast = Packages.android.widget.Toast;

var option_id = 0;

function activate(proxy /* com.example.graph_editor.extensions.ScriptProxy*/) {
    option_id = proxy.registerGraphMenuOption("Draw Matching", "matchingRequested");
    proxy.customizeVertexDrawingBehaviour("drawVertex");
    proxy.customizeEdgeDrawingBehaviour("drawEdge");
}

function deactivate(coreProxy/* com.example.graph_editor.extensions.ScriptProxy*/) {
    coreProxy.deregisterGraphMenuOption(option_id);
    coreProxy.restoreDefaultVertexDrawingBehaviour();
    coreProxy.restoreDefaultEdgeDrawingBehaviour();
}

var verticesMap = {}

//class Vertex {
//    constructor(adjacentIds) {
//        this.adjacentIds = adjacentIds
//        this.color = -1
//        this.match = null
//        this.visited = false
//    }
//}

function createVertex(ids) {
    return {adjacentIds:ids, color:-1, match:null, visited:false}
}


function buildVertices(coreGraph) {
    var vertices = {}
    for (coreVertex in Iterator(coreGraph.getVertices())) {
        var adjacentIds = []
        for (coreEdge in Iterator(coreVertex.getEdges())) {
            adjacentIds.push(coreEdge.getTarget().getIndex())
        }
        vertices[coreVertex.getIndex()] = createVertex(adjacentIds)
    }
    return vertices
}

function visit(vertices, vertex) {
    vertex.visited = true
    for (i in vertex.adjacentIds) {
        var id = vertex.adjacentIds[i]
        var adjacent = vertices[id]
        if (adjacent.visited == false) {
            adjacent.color = 1 - vertex.color
            if (!visit(vertices, adjacent)) {
                return false
            }
        } else if (adjacent.color == vertex.color) {
            return false
        }
    }
    return true
}

function bipartite(vertices) {
    for (id in vertices) {
        var vertex = vertices[id]
        if (vertex.visited == false) {
            vertex.color = 0

            if (!visit(vertices, vertex)) {
                return false
            }
        }
    }
    return true
}

function resetColoring() {
    verticesMap = {}
}

function alternating_path(vertices, vertex) {
    if (vertex.visited) return false;
    vertex.visited = true;
    for (i in vertex.adjacentIds) {
        var id = vertex.adjacentIds[i]
        var adjacent = vertices[id]
        if (adjacent.match == null || alternating_path(vertices, adjacent.match) == 1) {
            vertex.match = adjacent;
            adjacent.match = vertex;
            return true;
        }
    }
    return false;
}
//turbo matching
function evaluateMatching(vertices) {
    while (true) {
        for (id in vertices) {
            vertices[id].visited = false
        }
        var improvement = 0 // = false
        for (id in vertices) {
            var vertex = vertices[id]
            if (vertex.match == null) {
                improvement |= alternating_path(vertices, vertex)
            }
        }
        if (!improvement) return
    }
}

function matchingRequested(coreGraph, view) {
    verticesMap = buildVertices(coreGraph)
    if (bipartite(verticesMap)) {
        Toast.makeText(view.getContext(), "Computing matching...!", Toast.LENGTH_SHORT).show();
        evaluateMatching(verticesMap);
        Toast.makeText(view.getContext(), "Computed matching!", Toast.LENGTH_SHORT).show();
        view.invalidate();
    } else {
        resetColoring();
        Toast.makeText(view.getContext(), "Your graph is not bipartite!", Toast.LENGTH_SHORT).show();
    }
}

function drawVertex(id, point, rectangle, canvas) {
    var aPaint = new Paint;
    var vertex = verticesMap[id]
    if (vertex == null) {
        aPaint.setARGB(255, 0, 0, 0);
    } else if (vertex.color == 0) {
        aPaint.setARGB(255, 255, 0, 0)
    } else if (vertex.color == 1) {
        aPaint.setARGB(255, 0, 255, 0);
    } else { // should not happen...
        aPaint.setARGB(255, 255, 255, 0);
    }

    aPaint.setStyle(Paint.Style.FILL);
    aPaint.setFlags(Paint.ANTI_ALIAS_FLAG);

    var cp = Utils.canvasPoint(point, rectangle, canvas)

    canvas.drawCircle(cp.getX(), cp.getY(), 0.005 * canvas.getWidth() / rectangle.getWidth(), aPaint);
}

function drawEdge(id1, id2, p1, p2, rectangle, canvas) {
    var edgePaint = new Paint();
    var source = verticesMap[id1]

    if (source == null || source.match != verticesMap[id2]) {
        edgePaint.setARGB(255, 127, 127, 127);
    } else {
        edgePaint.setARGB(255, 0, 0, 255);
    }
    edgePaint.setStyle(Paint.Style.STROKE);
    edgePaint.setStrokeWidth(0.005 * canvas.getWidth() / rectangle.getWidth());
    edgePaint.setFlags(Paint.ANTI_ALIAS_FLAG);

    var cp1 = Utils.canvasPoint(p1, rectangle, canvas)
    var cp2 = Utils.canvasPoint(p2, rectangle, canvas)

    canvas.drawLine(cp1.getX(), cp1.getY(), cp2.getX(), cp2.getY(), edgePaint);
}
