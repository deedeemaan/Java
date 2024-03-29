package com.example.map_toysocialnetwork_gui.utils.graph;

import java.util.*;

public class Graph {
    int numVertices;
    private Map<Integer, List<Long>> adjList;

    public Graph(int numVertices) {
        this.numVertices = numVertices;
        this.adjList = new HashMap<>();
        for (int i = 0; i < numVertices; i++) {
            adjList.put(i, new ArrayList<>());
        }
    }

    public void addEdge(Long v, Long w) {
        adjList.get(v.intValue()).add(w);
        adjList.get(w.intValue()).add(v);
    }

    public List<Integer> findLongestPathComponent() {
        List<Integer> longestPath = new ArrayList<>();
        boolean[] visited = new boolean[numVertices];

        for (int v = 0; v < numVertices; v++) {
            if (!visited[v]) {
                List<Integer> currentPath = new ArrayList<>();
                dfs(v, visited, currentPath, true);

                if (currentPath.size() > longestPath.size()) {
                    longestPath = currentPath;
                }
            }
        }

        return longestPath;
    }

    public int countConnectedComponents() {
        int count = 0;
        boolean[] visited = new boolean[numVertices];

        for(int v = 0; v < numVertices; v++) {
            if(!visited[v]) {
                dfs(v,visited,new ArrayList<>(), false);
                count++;
            }
        }
        return count;
    }

    private void dfs(int v, boolean[] visited, List<Integer> currentPath, boolean forLongestPath) {
        visited[v] = true;
        if(forLongestPath)
            currentPath.add(v+1);

        adjList.get(v).forEach(neighbor->{
            if (!visited[neighbor.intValue()]) {
                dfs(neighbor.intValue(), visited, currentPath, forLongestPath);
            }
        });
    }
}