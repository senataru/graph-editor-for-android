//
// Created by dandr on 06.02.2023.
//

#ifndef GRAPH_EDITOR_FORCES_H
#define GRAPH_EDITOR_FORCES_H

typedef double ld;

ld fa(ld x, ld k, int n) {
    return x * x / k / n;
}

ld fr(ld x, ld k, int n) {
    return k * k / x / n / 100.0;
}

namespace NamespaceForVertex {
    class PairXY {
    public:
        ld x, y;
        PairXY() : x(0), y(0) {}
        PairXY(ld _x, ld _y) : x(_x), y(_y) {}
        ld module() {
            return std::fmax(0.001, std::sqrt(x * x + y * y));
        }
        void clear() {
            x = y = 0;
        }
        PairXY operator-(const PairXY &another) const {
            return {x - another.x, y - another.y};
        }
        PairXY operator+(const PairXY &another) const {
            return {x + another.x, y + another.y};
        }
        PairXY operator/(const PairXY &another) const {
            return {x / another.x, y / another.y};
        }
        PairXY operator/(const ld another) const {
            return {x / another, y / another};
        }
        PairXY operator*(const PairXY &another) const {
            return {x * another.x, y * another.y};
        }
        PairXY operator*(const ld another) const {
            return {x * another, y * another};
        }
        static ld sqr(const ld &x)  {
            return x * x;
        }
        friend ld dist2(const PairXY &v, const PairXY &w) {
            return sqr(v.x - w.x) + sqr(v.y - w.y);
        }
        PairXY shortestPointFromSegment(const PairXY &v, const PairXY &w) const {
            ld l2 = dist2(v, w);
            if (l2 == 0) return v;
            auto t = ((x - v.x) * (w.x - v.x) + (y - v.y) * (w.y - v.y)) / l2;
            t = std::max((ld)0, std::min((ld)1, t));
            return {v.x + t * (w.x - v.x), v.y + t * (w.y - v.y)};
        }
    };
    class Vertex {
    public:
        int id;
        PairXY pos, disp;
        bool free;

        explicit Vertex(int _id) : id(_id), disp(), free(true), pos(0, 0) {}

        void setPos(double x, double y) {
            pos.x = x;
            pos.y = y;
        }
    };
}

ld distance_between_points(ld x_0, ld y_0, ld x_1, ld y_1) {
    return sqrt((x_0 - x_1) * (x_0 - x_1) + (y_0 - y_1) * (y_0 - y_1));
}

ld vertex_edge_distance(NamespaceForVertex::Vertex& v, NamespaceForVertex::Vertex& e1, NamespaceForVertex::Vertex& e2) {    // first one is a vertex, two next are ends of an edge
    NamespaceForVertex::PairXY v_coords = v.pos;
    NamespaceForVertex::PairXY ort_proj = v_coords.shortestPointFromSegment(e1.pos, e2.pos);
    return distance_between_points(v_coords.x, v_coords.y, ort_proj.x, ort_proj.y);
}

ld smallest_vertex_edge_distance(const std::vector<NamespaceForVertex::Vertex>& V, const std::vector<std::vector<int>>& E) {
    ld result = 1e18;
    for (int w_id = 0; w_id < V.size(); ++w_id) {
        for (int e1 = 0; e1 < V.size(); ++e1) {
            if (e1 == w_id) continue;
            for (auto &e2: E[e1]) {
                if (e2 == w_id) continue;
                if (e1 < e2) continue;

                auto v = V[e1];
                auto u = V[e2];
                auto w = V[w_id];
                ld dist = vertex_edge_distance(w, v, u);

                if (dist < result) result = dist;
            }
        }
    }

    return result;
}

void forcesIteration(std::vector<NamespaceForVertex::Vertex> &V, const std::vector<std::vector<int>> &E, ld k, int n) {
    // calculate repulsive forces
    for (auto &v : V) {
        // each Vertex has two vectors: .pos and .disp
        for (auto &u : V) {
            if (u.id != v.id) {
                auto delta = v.pos - u.pos;
                v.disp = v.disp + (delta / delta.module()) * fr(delta.module(), k, n);
            }
        }
    }
    // calculate repulsive forces between vertices and edges
    for (auto &p : V) {
        // each Vertex has two vectors: .pos and .disp
        for (auto &v : V) {
            for (auto &e: E[v.id]) {
                auto &u = V[e];
                if (p.id == v.id || p.id == u.id) continue;
                auto delta = (p.pos - p.pos.shortestPointFromSegment(v.pos, u.pos));
                p.disp = p.disp + (delta / delta.module()) * fr(delta.module(), k, n);
            }
        }
    }
    // calculate attractive forces
    for (int v_id = 0; v_id < V.size(); ++v_id) {
        for (auto &e: E[v_id]) {
            auto &v = V[v_id];
            auto &u = V[e];
            auto delta = v.pos - u.pos;
            v.disp = v.disp - (delta / delta.module()) * fa(std::max(delta.module(), (ld)0.001), k, n);
        }
    }
}

#endif //GRAPH_EDITOR_FORCES_H
