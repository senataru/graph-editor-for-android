//
// Created by dandr on 21.01.23.
//
#include <vector>
#include "planarity.h"
#include "triangulate.h"
#include <algorithm>
#include <iostream>
#include <set>

std::vector<std::pair<Vertex*, PairXY>> extractCoordinates(std::vector<Vertex*> vertices, const std::vector<ShiftVertex*> v) {
    std::vector<std::pair<Vertex*, PairXY>> res;
    for (auto &x : vertices) {
        for (auto &i : v) {
            if (i->id == x->id) {
                res.push_back({x, i->pos});
                break;
            }
        }
    }
    return res;
}

std::set<Vertex*> compressFaces(const std::vector<Face> &faces) {
    std::set<Vertex*> res;
    for (auto &f : faces)
        for (auto &v : f.V)
            res.insert(v);
    return res;
}

std::vector<Vertex*> findOuterVertices(Vertex* chosenVer, Vertex* orig_pred, Vertex* next, std::vector<Face> &faces, std::vector<Vertex*> toAdd) {
    Vertex* pred = orig_pred;
    std::vector<Vertex*> orderToAdd({pred});
    bool end = false;
    while(!end) {
        for (auto &f : faces) {
            if (f.contain(chosenVer) && f.contain(pred)) {
                if (f.contain(next) && pred == orig_pred) continue;
                Vertex* founded;
                for (auto &x : f.V)
                    if (x != chosenVer && x != pred) {
                        founded = x;
                        break;
                    }
                if (founded == next) {
                    end = true;
                    break;
                }
                if (founded->visited || founded->temp) continue;
                orderToAdd.push_back(founded);
                pred = founded;
            }
        }
    }
    for (auto &v : orderToAdd)
        v->temp = true;
    auto findOuterNeighbours = [](Vertex* v) {
        int ans = 0;
        for (auto &_to : v->edges) {
            auto &to = _to.first;
            if (!to->visited && to->out && !to->temp) {
                ++ans;
            }
        }
        return ans;
    };
    orderToAdd.push_back(next);
    for (int i = 0; i + 1 < orderToAdd.size(); ++i) {
        int goodN1 = findOuterNeighbours(orderToAdd[i]);
        int goodN2 = findOuterNeighbours(orderToAdd[i + 1]);
        if (goodN1 && goodN2) {
            auto res = findOuterVertices(chosenVer, orderToAdd[i], orderToAdd[i + 1], faces, toAdd);
            orderToAdd.insert(orderToAdd.begin() + i + 1, res.begin(), res.end());
            break;
        }
    }
    orderToAdd.pop_back();
    orderToAdd.erase(orderToAdd.begin());
    for (auto &v : orderToAdd)
        v->temp = false;
    return orderToAdd;
}

std::vector<Vertex*> findCanonicalOrder(std::vector<Face> faces) {
    int outerID = 0;
    auto listOfAllVertices = compressFaces(faces);
    for (auto &v : listOfAllVertices)
        v->visited = v->out = v->chords = v->temp = 0;
    int n = listOfAllVertices.size();
    Vertex *a, *b, *t;
    a = faces[outerID].V[0];
    t = faces[outerID].V[1];
    b = faces[outerID].V[2];
    std::vector<Vertex*> res(n);
    a->visited = b->visited = a->out = b->out = t->out = true;
    res[0] = a;
    res[1] = b;
    std::vector<Vertex*> outer({a, t, b});
    for (int k = n - 1; k >= 2; --k) {
        Vertex *chosenVer = nullptr;
        for (auto &v : listOfAllVertices)
            if (v->out && !v->visited && !v->chords) {
                chosenVer = v;
                // TODO: could be the list of candidates instead. same below
                break;
            }
        if (chosenVer == nullptr)
            throw "Not found a vertex";
        chosenVer->out = false;
        chosenVer->visited = true;
        std::vector<Vertex*> toAdd;
        Vertex* firstFound = nullptr;
        int id = std::find(outer.begin(), outer.end(), chosenVer) - outer.begin();

        for (auto &_to : chosenVer->edges) {
            auto &to = _to.first;
            if (!to->visited && !to->out) {
                toAdd.push_back(to);
                to->out = true;
            }
        }
        outer.erase(outer.begin() + id);
        if (toAdd.size()) {
            auto orderToAdd = findOuterVertices(chosenVer, outer[id - 1], outer[id], faces, toAdd);
            outer.insert(outer.begin() + id, orderToAdd.begin(), orderToAdd.end());
        }
        for (auto &v : toAdd)
            v->temp = true;
        for (auto &v : listOfAllVertices) {
            v->chords = 0;
        }
        for (int i1 = 0; i1 < outer.size(); ++i1) {
            for (auto &_to: outer[i1]->edges) {
                auto to = _to.first;
                int i2 = std::find(outer.begin(), outer.end(), to) - outer.begin();
                if (i2 != outer.size() && i2 > i1 && i1 + 1 < i2 && outer.size() > i2 - i1) {
                    outer[i1]->chords++;
                    outer[i2]->chords++;
                }
            }
        }
        //std::cout << "outer: ";
        //for (auto &x : outer)
        //std::cout << x->id << " ";
        //std::cout << std::endl;
        res[k] = chosenVer;
    }
    return res;
}

// the order is already shuffled
std::vector<ShiftVertex*> convertToShiftVertex(const std::vector<Vertex*> &V) {
    std::vector<ShiftVertex*> ans;
    for (int i = 0; i < V.size(); ++i) {
        ans.push_back(new ShiftVertex(V[i]->id));
    }
    for (int i = 0; i < ans.size(); ++i) {
        for (auto &to : V[i]->edges) {
            for (int k = 0; k < ans.size(); ++k)
                if (V[k]->id == to.first->id) {
                    ans[i]->edges.push_back(ans[k]);
                    break;
                }
        }
    }
    return std::move(ans);
}

PairXY calculatePos(ShiftVertex *ver1, ShiftVertex *ver2) {
    int mxy = std::max(ver1->pos.y, ver2->pos.y);
    int resX, resY, difx;
    if (ver1->pos.y == mxy) {
        difx = (ver2->pos.x - (mxy - ver2->pos.y)) - ver1->pos.x;
        resX = ver1->pos.x + difx / 2;
        resY = ver1->pos.y + difx / 2;
    } else {
        difx = ver2->pos.x - (ver1->pos.x + (mxy - ver1->pos.y));
        resX = ver2->pos.x - difx / 2;
        resY = ver2->pos.y + difx / 2;
    }
    if (difx % 2)
        throw "Internal error";
    return PairXY(resX, resY);
}

std::vector<ShiftVertex*> shiftMethod(const std::vector<Vertex*> &VInp) {
    auto V = convertToShiftVertex(VInp);
    for (int i = 0; i < V.size(); ++i)
        V[i]->L = {V[i]};
    V[0]->pos = {0, 0};
    V[1]->pos = {2, 0};
    V[2]->pos = {1, 1};
    V[0]->visited = true;
    V[1]->visited = true;
    V[2]->visited = true;
    std::vector<ShiftVertex*> outer({V[0], V[2], V[1]});
    for (int i = 3; i < V.size(); ++i) {
        //if (i == 8) break;
        std::vector<ShiftVertex*> neighbours;
        int mn = 1e9, mx = -1e9;
        ShiftVertex *ver1, *ver2;
        for (auto &to : V[i]->edges) {
            //std::cout << to->id << " " <<
            int x = to->pos.x;
            int id = std::find(outer.begin(), outer.end(), to) - outer.begin();
            if (id != outer.size()) {
                if (mn > x && to->visited) {
                    mn = x;
                    ver1 = to;
                }
                if (mx < x && to->visited) {
                    mx = x;
                    ver2 = to;
                }
            }
        }

        int id1 = std::find(outer.begin(), outer.end(), ver1) - outer.begin();
        int id2 = std::find(outer.begin(), outer.end(), ver2) - outer.begin();

        //std::cout << "DEBUG: " << id1 << " " << id2 << " " << outer.size() << '\n';
        std::set<ShiftVertex*> unionL1, unionL2;
        for (int j = id1 + 1; j < id2; ++j) {
            unionL1.insert(outer[j]->L.begin(), outer[j]->L.end());
        }
        for (int j = id2; j < outer.size(); ++j) {
            unionL2.insert(outer[j]->L.begin(), outer[j]->L.end());
        }

        //std::cout << "L1: ";
        for (auto x : unionL1) std::cout << x->pos.x << "|" << x->pos.y << " ";
        //std::cout << '\n';
        //std::cout << "L2: ";
        for (auto x : unionL2) std::cout << x->pos.x << "|" << x->pos.y << " ";
        //std::cout << '\n';

        for (auto &x : unionL1)
            x->pos.x += 1;
        for (auto &x : unionL2)
            x->pos.x += 2;
        V[i]->pos = calculatePos(ver1, ver2);
        //std::cout << V[i]->pos.x << "|||" << V[i]->pos.y << "\n\n";
        for (int j = id2 - 1; j > id1; --j) {
            outer.erase(outer.begin() + j);
        }
        V[i]->L.insert(unionL1.begin(), unionL1.end());
        outer.push_back(V[i]);
        V[i]->visited = true;
        std::sort(outer.begin(), outer.end(), [&](auto &f, auto &s) {
            return f->pos.x < s->pos.x;
        });
    }
    outer.clear();
    for (auto &i : V)
        if (i->visited)
            outer.push_back(i);
    std::sort(outer.begin(), outer.end(), [&](auto &f, auto &s) {
        return f->pos.x < s->pos.x;
    });
    //std::vector<PairXY> answer;
    //for (auto &v : outer) answer.push_back(v->pos); // TODO: change with some stl function
    //for (auto &v : outer) std::cout << v->id << " ";
    // TODO: mark not visited
    //std::cout << '\n';
    //for (auto &v : outer)
    //    delete v;
    //return answer;
    return outer;
}