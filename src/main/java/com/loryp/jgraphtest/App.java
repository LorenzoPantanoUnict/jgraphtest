package com.loryp.jgraphtest;

import com.loryp.jgraphtest.mypackage.GraphVisualizer;

import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.Graph;
import org.jgrapht.graph.builder.GraphTypeBuilder;
import org.jgrapht.generate.GnpRandomGraphGenerator;

//DijkstraShortestPath
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;

import java.util.function.Supplier;



public class App {

    public static void main(String[] args) {

        Supplier<String> mySupplier = new Supplier<String>() {
            private int id = 0;

            @Override
            public String get() {
                return "v" + id++;
            }
        };

        Graph<String, DefaultEdge> graph = GraphTypeBuilder.<String, DefaultEdge>undirected()
                                                       .allowingMultipleEdges(true)
                                                       .allowingSelfLoops(true)
                                                       .vertexSupplier(mySupplier)
                                                       .edgeClass(DefaultEdge.class)
                                                       .buildGraph();
        
        


        GnpRandomGraphGenerator<String, DefaultEdge> randomGenerator = new GnpRandomGraphGenerator<>(100, 0.075);
        randomGenerator.generateGraph(graph);

        DijkstraShortestPath<String, DefaultEdge> dijkstraAlg = new DijkstraShortestPath<>(graph);
        System.out.println("Shortest path from v2 to v47: " + dijkstraAlg.getPath("v2", "v47"));

        GraphVisualizer graphExt = new GraphVisualizer();    
        graphExt.displayGraphStream(graph);    

        

        
    }

}