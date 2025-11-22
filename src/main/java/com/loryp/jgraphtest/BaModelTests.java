package com.loryp.jgraphtest;

import com.loryp.jgraphtest.mypackage.*;

import org.jgrapht.Graph;
import org.jgrapht.generate.BarabasiAlbertGraphGenerator;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.builder.GraphTypeBuilder;

import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.ArrayList;


public class BaModelTests {

    

    public static void main(String[] args) {

        int numGraphs = 10;
        // int initialNodes = 2;
        // int edgesPerNode = 2;
        // int totalNodes = 100;
        
        
        //Inport dei grafi
        List<Graph<String, DefaultEdge>> graphs = inportBaGraphs(numGraphs); 
        
        analisiRimozioneMirata(graphs);
         
        
    }

    public static Graph<String, DefaultEdge> createBaGraph(int initialNodes, int edgesPerNode, int totalNodes){
            BarabasiAlbertGraphGenerator<String, DefaultEdge> baGenerator = 
                new BarabasiAlbertGraphGenerator<>(initialNodes,  edgesPerNode, totalNodes - initialNodes);

        Supplier<String> mySupplier = new Supplier<String>() {
            private int id = 0;

            @Override
            public String get() {
                return "v" + id++;
            }
        };
    
        Graph<String, DefaultEdge> graph = GraphTypeBuilder.<String, DefaultEdge>undirected()
                                                       .allowingMultipleEdges(false)
                                                       .allowingSelfLoops(false)
                                                       .vertexSupplier(mySupplier)
                                                       .edgeClass(DefaultEdge.class)
                                                       .buildGraph();

        baGenerator.generateGraph(graph);
        return graph;

    }

    public static List<Graph<String, DefaultEdge>> inportBaGraphs(int numGraph){
        List<Graph<String, DefaultEdge>> graphs = new ArrayList<>(numGraph);

        for(int i = 0; i<numGraph; i++){
            Graph<String, DefaultEdge> graph = GraphExtension.createEmptySimpleGraph();
            GraphInportExport.importJsonGraph(graph, "Ba2Model" + Integer.valueOf(i+1).toString());
            graphs.add(graph);
        }
        return graphs;
    }

    public static void analisiSmallWorld(List<Graph<String, DefaultEdge>> graphs){
        // Analisi small-World
        int numGraphs = graphs.size();
        double[] avgDistances = new double[numGraphs];
        int[] diameters = new int[numGraphs];
        int count = 0;
        for(Graph<String, DefaultEdge> g : graphs){
            avgDistances[count] = GraphExtension.getAverageDistance(g);
            diameters[count] = GraphExtension.getDiameter(g);
            count++;
        }
        Plotter.plotAnalisiSmallWorld(diameters, avgDistances);
    }
    
    public static void analisiGrado(List<Graph<String, DefaultEdge>> graphs){
        
        List<List<Map.Entry<String, Integer>>> topHubsList = new ArrayList<>();
        for(Graph<String, DefaultEdge> g : graphs){
            List<Map.Entry<String, Integer>> topHubs = GraphExtension.getVerticesSortedByDegree(g);
            topHubsList.add(topHubs);
        }
            
        Plotter.plotAnalisiGrado(topHubsList);
    }   

    public static void analisiRimozioneCasuale(List<Graph<String, DefaultEdge>> graphs){
        
        int numGraphs = graphs.size();
        double[] avgNodeRemoves = new double[numGraphs];
        int count = 0;
        for(Graph<String, DefaultEdge> g : graphs){
            avgNodeRemoves[count] = GraphTest.averageNodesRemovedUntilDisconnected(g, 100, false);
            System.out.println("Grafo " + Integer.valueOf(count+1).toString() + "--> nodi rimossi: " + avgNodeRemoves[count]);
            count++;
        }
        Plotter.plotAnalisiRimozioneCasuale(avgNodeRemoves);
    }

    public static void analisiDistribuzioneGrado(List<Graph<String, DefaultEdge>> graphs){
        List<Map<Integer, Double>> distributions = new ArrayList<>();
        for(Graph<String, DefaultEdge> g : graphs){
            Map<Integer, Double> distribution = GraphExtension.getDegreeDistribution(g);
            distributions.add(distribution);
        }
        Plotter.plotDegreeDistributions(distributions, "Distribuzioni di Grado dei Grafi BA", "Grado k", "P(k)");
    }

    public static void testAttraversamento(List<Graph<String, DefaultEdge>> graphs){
        List<List<Double>> results = new ArrayList<>();
        for(Graph<String, DefaultEdge> g : graphs){
           results.add( GraphTest.testBFSvsDFS(g, "Test-Attraversamento-Ba2Model", 100));
        }

        Plotter.plotAnalisiAttraversamento(results);
    }

    public static void analisiRimozioneMirata(List<Graph<String, DefaultEdge>> graphs){
        List<Integer> results = GraphTest.testAnalisiRimozioneMirata(graphs);
        System.out.println("Risultati rimozione mirata: " + results.toString());
    }

    public static void analisiRobustezza(List<Graph<String, DefaultEdge>> graphs){

        List<Integer> hubsRemoved = GraphTest.testAnalisiRimozioneMirata(graphs);
        double[] avgRandomNodeRemoves = new double[graphs.size()];
        int count = 0;

        for(Graph<String, DefaultEdge> g : graphs){
            avgRandomNodeRemoves[count] = GraphTest.averageNodesRemovedUntilDisconnected(g, 20, false);
            count++;
        }

        Plotter.plostConfrontoRImozioneCasualeMirata(avgRandomNodeRemoves, hubsRemoved);
    }

    
}


