//
// Created by dandr on 21.01.23.
//

#ifndef UNTITLED1_PLANARITY_H
#define UNTITLED1_PLANARITY_H

#include <vector>
#include <set>
#include "triangulate.h"


class PairXY {
public:
    double x, y;
    PairXY() = default;
    PairXY(const double &_x, const double &_y) : x(_x), y(_y) {}
};

class ShiftVertex {
public:
    std::set<ShiftVertex*> L;
    int id;
    PairXY pos;
    bool visited;
    std::vector<ShiftVertex*> edges;
    explicit ShiftVertex(int _id): id(_id), visited(false) {}
};

std::vector<Vertex*> findCanonicalOrder(std::vector <Face> faces);
std::set<Vertex*> compressFaces(const std::vector<Face> &faces);
std::vector<ShiftVertex*> shiftMethod(const std::vector<Vertex*> &VInp) ;
std::vector<std::pair<Vertex*, PairXY>> extractCoordinates(std::vector<Vertex*> vertices, const std::vector<ShiftVertex*> v);

#endif //UNTITLED1_PLANARITY_H