#include <jni.h>

#include <iostream>
#include <thread>
#include <atomic>
#include <cmath>
#include <vector>
#include <random>
#include <queue>
#include <algorithm>

const int N = 2002;

typedef long double ld;

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
        PairXY operator-(const PairXY &another) {
            return {x - another.x, y - another.y};
        }
        PairXY operator+(const PairXY &another) {
            return {x + another.x, y + another.y};
        }
        PairXY operator/(const PairXY &another) {
            return {x / another.x, y / another.y};
        }
        PairXY operator/(const ld another) {
            return {x / another, y / another};
        }
        PairXY operator*(const PairXY &another) {
            return {x * another.x, y * another.y};
        }
        PairXY operator*(const ld another) {
            return {x * another, y * another};
        }
        friend PairXY min(const PairXY &one, const PairXY &another) {
            return {std::min(one.x, another.x), std::min(one.y, another.y)};
        }
        static ld sqr(const ld &x)  {
            return x * x;
        }
        friend ld dist2(const PairXY &v, const PairXY &w)  {
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

        void setNotFree() {
            free = false;
        }

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


extern "C"
JNIEXPORT jdoubleArray JNICALL
Java_com_example_graph_1editor_model_DrawManager_arrangeGraph(__unused JNIEnv *env, jclass clazz,
                                                              jint n,
                                                              jint m,
                                                              jdoubleArray tab_x,
                                                              jdoubleArray tab_y,
                                                              jintArray tab_edge_source,
                                                              jintArray tab_edge_target) {

    ld W = 1, H = 1;
    std::vector<NamespaceForVertex::Vertex> V;
    jint *edge_source = (*env).GetIntArrayElements(tab_edge_source, 0);
    jint *edge_target = (*env).GetIntArrayElements(tab_edge_target, 0);
    jdouble *x = (*env).GetDoubleArrayElements(tab_x, 0);
    jdouble *y = (*env).GetDoubleArrayElements(tab_y, 0);
    // TODO: solve for several connectivity component
    std::vector<std::vector<int>> E(n);
    for (int i = 0; i < n; ++i) {
        V.emplace_back(i);
        //V[i].setPos(x[i]*1000, y[i]*1000);
        //V[i].setPos(float(rand())/RAND_MAX-0.5, float(rand())/RAND_MAX-0.5);
        V[i].setPos(x[i] / 2, y[i] / 2);
    }
    for (int i = 0; i < m; ++i) {
        E[edge_source[i]].push_back(edge_target[i]);
        E[edge_target[i]].push_back(edge_source[i]);
    }

    ld area = W * H;
    ld k = std::sqrt(area / n);
    ld t = W + H;
    ld cooling = 1.1;
    const int iterations = 200;
    for (int i = 0; i < iterations; ++i) {
        ld max_diff = 0;
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

        for (auto &v : V) {
            if (v.free) {
                auto delta = ((v.disp.module() > t) ? v.disp / v.disp.module() * t : v.disp);
                max_diff = std::max(delta.x, max_diff);
                max_diff = std::max(delta.y, max_diff);
                v.pos = v.pos + delta;
            }
            v.pos.x = fmin(W, fmax(-W, v.pos.x));
            v.pos.y = fmin(H, fmax(-H, v.pos.y));
            v.disp.clear();
        }
        t = t / cooling;
        if (max_diff <= 0.001) break;
    }
    jdoubleArray res = env->NewDoubleArray(n*2);
    jdouble res_tab[n*2];
    int j=0;
    double mnx = (*std::min_element(V.begin(), V.end(), [](auto a, auto b) {
        return a.pos.x < b.pos.x;
    })).pos.x;
    double mny = (*std::min_element(V.begin(), V.end(), [](auto a, auto b) {
        return a.pos.y < b.pos.y;
    })).pos.y;
    double mxx = (*std::min_element(V.begin(), V.end(), [](auto a, auto b) {
        return a.pos.x > b.pos.x;
    })).pos.x;
    double mxy = (*std::min_element(V.begin(), V.end(), [](auto a, auto b) {
        return a.pos.y > b.pos.y;
    })).pos.y;
    for (NamespaceForVertex::Vertex &v : V) {
        v.pos.x -= mnx;
        v.pos.y -= mny;
        v.pos.x /= (mxx - mnx);
        v.pos.y /= (mxy - mny);
    }

    for (NamespaceForVertex::Vertex &v : V) {
        res_tab[j] = v.pos.x;
        res_tab[j+n] = v.pos.y;
        ++j;
    };
    env->SetDoubleArrayRegion(res, (jsize)0, (jsize)n*2, res_tab);
    return res;
}


// SECOND FUNCTION
extern "C"
JNIEXPORT jdoubleArray JNICALL
Java_com_example_graph_1editor_model_DrawManager_arrangePlanarGraph(__unused JNIEnv *env, jclass clazz,
                                                              jint n,
                                                              jint m,
                                                              jdoubleArray tab_x,
                                                              jdoubleArray tab_y,
                                                              jintArray tab_edge_source,
                                                              jintArray tab_edge_target) {

    std::vector<NamespaceForVertex::Vertex> V;
    jint *edge_source = (*env).GetIntArrayElements(tab_edge_source, 0);
    jint *edge_target = (*env).GetIntArrayElements(tab_edge_target, 0);
    jdouble *x = (*env).GetDoubleArrayElements(tab_x, 0);
    jdouble *y = (*env).GetDoubleArrayElements(tab_y, 0);
    // TODO: solve for several connectivity component
    std::vector<std::vector<int>> E(n);

    // normalize the vertex positions
    ld max_x = 0, max_y = 0;
    for (int i = 0; i < n; ++i) {
        if (fabs(x[i]) > max_x) max_x = fabs(x[i]);
        if (fabs(y[i]) > max_y) max_y = fabs(y[i]);
    }
    ld W = 2 * max_x, H = 2 * max_y;

    for (int i = 0; i < n; ++i) {
        V.emplace_back(i);
        V[i].setPos(x[i], y[i]);
    }
    for (int i = 0; i < m; ++i) {
        E[edge_source[i]].push_back(edge_target[i]);
        E[edge_target[i]].push_back(edge_source[i]);
    }

    ld area = W * H;
    ld k = std::sqrt(area / n);
    ld t = W + H;
    ld cooling = 1;
    const int iterations = 200;
    for (int i = 0; i < iterations; ++i) {
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

        ld epsilon = smallest_vertex_edge_distance(V, E);
        ld safe_dist = 0.4 * epsilon;   // cannot be more than 0.5 * epsilon, should not be more than 0.45 * epsilon

        for (auto &v : V) {
            if (v.free) {
                ld disp_module = v.disp.module();
                if (disp_module > safe_dist) {
                    v.disp.x /= (disp_module/safe_dist);
                    v.disp.y /= (disp_module/safe_dist);
                }
                v.pos.x += v.disp.x;
                v.pos.y += v.disp.y;
            }
            v.pos.x = fmin(W / 2, fmax(-W / 2, v.pos.x));
            v.pos.y = fmin(H / 2, fmax(-H / 2, v.pos.y));
            v.disp.clear();
        }
        t = t / cooling;
    }
    jdoubleArray res = env->NewDoubleArray(n*2);
    jdouble res_tab[n*2];
    int j=0;
    double mnx = (*std::min_element(V.begin(), V.end(), [](auto a, auto b) {
        return a.pos.x < b.pos.x;
    })).pos.x;
    double mny = (*std::min_element(V.begin(), V.end(), [](auto a, auto b) {
        return a.pos.y < b.pos.y;
    })).pos.y;
    double mxx = (*std::min_element(V.begin(), V.end(), [](auto a, auto b) {
        return a.pos.x > b.pos.x;
    })).pos.x;
    double mxy = (*std::min_element(V.begin(), V.end(), [](auto a, auto b) {
        return a.pos.y > b.pos.y;
    })).pos.y;
    for (NamespaceForVertex::Vertex &v : V) {
        v.pos.x -= mnx;
        v.pos.y -= mny;
        v.pos.x /= (mxx - mnx);
        v.pos.y /= (mxy - mny);
    }

    for (NamespaceForVertex::Vertex &v : V) {
        res_tab[j] = v.pos.x;
        res_tab[j+n] = v.pos.y;
        ++j;
    };
    env->SetDoubleArrayRegion(res, (jsize)0, (jsize)n*2, res_tab);
    return res;
}


// THIRD FUNCTION
#include "planarity.cpp"
extern "C"
JNIEXPORT jdoubleArray JNICALL
Java_com_example_graph_1editor_model_DrawManager_makePlanar(__unused JNIEnv *env, jclass clazz,
                                                                    jint n,
                                                                    jint m,
                                                                    jdoubleArray tab_x,
                                                                    jdoubleArray tab_y,
                                                                    jintArray tab_edge_source,
                                                                    jintArray tab_edge_target) {
    jint *edge_source = (*env).GetIntArrayElements(tab_edge_source, 0);
    jint *edge_target = (*env).GetIntArrayElements(tab_edge_target, 0);
    jdouble *x = (*env).GetDoubleArrayElements(tab_x, 0);
    jdouble *y = (*env).GetDoubleArrayElements(tab_y, 0);
    // TODO: solve for several connectivity component
    std::vector<std::vector<int>> E(n);
    std::vector<std::pair<int, int>> edges_input;
    for (int i = 0; i < m; ++i) {
        edges_input.emplace_back(edge_target[i], edge_source[i]);
    }

    auto G = make_graph(n, edges_input);
    auto faces = triangulate(G);
    auto orig_order_set = compressFaces(faces);
    auto orig_order = std::vector<Vertex*>(orig_order_set.begin(), orig_order_set.end());
    auto res1 = findCanonicalOrder2(faces);
    auto res2 = shiftMethod(res1);
    auto res3 = extractCoordinates(orig_order, res2);

    jdoubleArray res = env->NewDoubleArray(n*2);
    jdouble res_tab[n*2];
    int j=0;
    double mnx = (*std::min_element(res3.begin(), res3.end(), [](auto a, auto b) {
        return a.second.x < b.second.x;
    })).second.x;
    double mny = (*std::min_element(res3.begin(), res3.end(), [](auto a, auto b) {
        return a.second.y < b.second.y;
    })).second.y;
    double mxx = (*std::max_element(res3.begin(), res3.end(), [](auto a, auto b) {
        return a.second.x < b.second.x;
    })).second.x;
    double mxy = (*std::max_element(res3.begin(), res3.end(), [](auto a, auto b) {
        return a.second.y < b.second.y;
    })).second.y;

    for (auto &v : res3) {
        v.second.x -= mnx;
        v.second.y -= mny;
        v.second.x /= (mxx - mnx);
        v.second.y /= (mxy - mny);
    }

    for (auto &v : res3) {
        res_tab[j] = v.second.x;
        res_tab[j+n] = v.second.y;
        ++j;
    };
    env->SetDoubleArrayRegion(res, (jsize)0, (jsize)n*2, res_tab);
    return res;
}