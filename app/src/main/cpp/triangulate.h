//
// Created by kasia on 18.01.23.
//

#ifndef GRAFY_TRIANGULATE_H
#define GRAFY_TRIANGULATE_H


#include <vector>
#include <stack>
#include <set>


class Vertex {
public:
    int id;
    bool embedded = false;
    bool visited = false;
    bool out = false;
    bool temp = false;
    int chords = 0;
    std::vector<std::pair<Vertex*, bool>> edges;
    explicit Vertex(int _id): id(_id){}
    void add_edge(Vertex* x);
    void mark_edge(Vertex* x);
};

class Face {
public:
    std::vector<Vertex*> V;
    Face(std::vector<Vertex*> v);
    bool contain(Vertex* key);
    bool contain_edge(Vertex* key1, Vertex* key2);
    Vertex* vertex_have_additional_edge(Vertex* v);
};

class Fragment {
public:
    std::vector<Vertex*> V;
    std::vector<Face*> available_faces;
    explicit Fragment(std::vector<Vertex*> &v);
    Fragment(Vertex* i, Vertex* j);
    void compute_faces(std::vector<Face>& all_faces);
    std::vector<Vertex *> find_path(Vertex *vertex, Face *face);
    Face add_path();
};

void compute_fragments(std::vector<Vertex> &V, std::vector<Face> faces, std::vector<Fragment> &f);
std::vector<Vertex*> find_cycle(Vertex* cur, Vertex* target);
std::vector<Face> compute_faces(std::vector<Vertex>& V);
std::vector<Face> triangulate_biconnected_component(std::vector<Face>& faces);
void make_graph_biconnected(int i, int& d, bool* visited, int* depth,
                            int* low, std::vector<Vertex> &adj, int* parent,
                            std::vector<std::pair<int, int>>& edges_to_add);
std::vector<Face> triangulate(std::vector<Vertex>& V);
std::vector<Vertex> make_graph(int n, std::vector<std::pair<int, int>> edges_input);
#endif //GRAFY_TRIANGULATE_H