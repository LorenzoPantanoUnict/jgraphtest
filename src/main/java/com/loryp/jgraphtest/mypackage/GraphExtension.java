package com.loryp.jgraphtest.mypackage;


import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.builder.GraphTypeBuilder;
import org.jgrapht.alg.connectivity.BiconnectivityInspector;
import org.jgrapht.alg.interfaces.ShortestPathAlgorithm;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.generate.BarabasiAlbertGraphGenerator;
import org.jgrapht.generate.GnpRandomGraphGenerator;
import org.jgrapht.generate.NamedGraphGenerator;
import org.jgrapht.GraphPath;

import org.jgrapht.alg.scoring.*;



import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;


public class GraphExtension {

    public GraphExtension(){

    }

    /*
     * =============================================
     *      Metodi per la creazione di Grafi
     * =============================================
     */
    public static Graph<String, DefaultEdge> createEmptySimpleGraph(){
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

        return  graph;                                              
    }
    
    

    public static  List<Graph<String, DefaultEdge>> generateBaGraphs(int numGraphs, int initialNodes, int edgesPerNode, int totalNodes){

        Supplier<String> mySupplier = new Supplier<String>() {
            private int id = 0;

            @Override
            public String get() {
                return "v" + id++;
            }
        };

        List<Graph<String, DefaultEdge>> graphs = new ArrayList<>(numGraphs);

        BarabasiAlbertGraphGenerator<String, DefaultEdge> baGenerator =  
            new BarabasiAlbertGraphGenerator<>(initialNodes,  edgesPerNode, totalNodes);

        for(int i = 0; i<numGraphs; i++){
            Graph<String, DefaultEdge> graph = GraphTypeBuilder.<String, DefaultEdge>undirected()
                                                       .allowingMultipleEdges(false)
                                                       .allowingSelfLoops(false)
                                                       .vertexSupplier(mySupplier)
                                                       .edgeClass(DefaultEdge.class)
                                                       .buildGraph();   
            baGenerator.generateGraph(graph);
            graphs.add(graph);
        }

        return graphs;
    }

    public static Graph<String, DefaultEdge> createRandomGraph(int totalNodes, double probability){
        GnpRandomGraphGenerator<String, DefaultEdge> randomGraphGen = new GnpRandomGraphGenerator<>(totalNodes, probability);


        Supplier<String> mySupplier = new Supplier<String>() {
            private int id = 0;

            @Override
            public String get() {
                return "v" + id++;
            }
        };

        Graph<String, DefaultEdge> randomGraph = GraphTypeBuilder.<String, DefaultEdge>undirected()
                                                       .allowingMultipleEdges(false)
                                                       .allowingSelfLoops(false)
                                                       .vertexSupplier(mySupplier)
                                                       .edgeClass(DefaultEdge.class)
                                                       .buildGraph();

        randomGraphGen.generateGraph(randomGraph);
        return randomGraph;
    }

    public static Graph<String, DefaultEdge> createZacharyGraph(){

        Supplier<String> mySupplier = new Supplier<String>() {
            private int num = 0;

            @Override
            public String get() {
                return "v" + num++;
            }
        };

        NamedGraphGenerator<String, DefaultEdge> myGraphGenerator = new NamedGraphGenerator<>();

        Graph<String, DefaultEdge> graph = GraphTypeBuilder.<String, DefaultEdge>undirected()
                                                       .allowingMultipleEdges(false)
                                                       .allowingSelfLoops(false)
                                                       .vertexSupplier(mySupplier)
                                                       .edgeClass(DefaultEdge.class)
                                                       .buildGraph();

        myGraphGenerator.generateZacharyKarateClubGraph(graph);

        return graph;
    }

    /*
     * =============================================
     *          Metodi di analisi del grafo
     * =============================================
     */

    public static <V> V getMaxDegreeNode(Graph<V, ?> graph){

        Set<V> nodeSet = graph.vertexSet();
        int maxdegree = -1;
        V maxNode = null;

        if(nodeSet.isEmpty()){
            System.out.println("il grafo non contiene nodi");
            return null;
        }

        for(V node : nodeSet){
            int currentdegree = graph.degreeOf(node);
            if( currentdegree > maxdegree){
                maxdegree = currentdegree;
                maxNode = node;
            }
        }

        return maxNode;
    }

    public static <V> double getAverageDegree(Graph<V, ?> graph){
        int vertexCount = graph.vertexSet().size();
        int edgeCount = graph.edgeSet().size();
        if(vertexCount == 0){
            System.out.println("GraphExtendion.getAverageDegree: il grafo non contiene vertici");
        }
        if(edgeCount == 0){
            System.out.println("GraphExtendion.getAverageDegree: il grafo non contiene collegamenti");
            return 0;
        }
        if(graph.getType().isDirected()){
            return (double)(edgeCount)/vertexCount;
        }else{
            return (double)(2*edgeCount)/vertexCount;
        }
    }

    public static <V, E>  void printVertexConnectivityScores(Graph<V, E> graph, V vertex){
        if(!graph.vertexSet().contains(vertex)){
            System.out.println("GraphExtension.printVertexScores: il grafo non contiene il vertice specificato");
            return;
        }
        BetweennessCentrality<V, E> betweenness = new BetweennessCentrality<>(graph);
        ClosenessCentrality<V, E> closeness = new ClosenessCentrality<>(graph);
        ClusteringCoefficient<V, E> clustering = new ClusteringCoefficient<>(graph);
        HarmonicCentrality<V, E> harmonic = new HarmonicCentrality<>(graph);
        KatzCentrality<V, E> katz = new KatzCentrality<>(graph);

        System.out.println("\n===NODE SCORES===\n");
        System.out.printf("Betweenness Centrality: %.4f\n", betweenness.getVertexScore(vertex));
        System.out.printf("Closeness Centrality: %.4f\n", closeness.getVertexScore(vertex));
        System.out.printf("Clustering Coefficient: %.4f\n", clustering.getVertexScore(vertex));
        System.out.printf("Harmonic Centrality: %.4f\n", harmonic.getVertexScore(vertex));
        System.out.printf("Katz Centrality: %.4f\n", katz.getVertexScore(vertex));

    }

    // Restituisce quanti cammini minimi passano per un determianto nodo
    public static int containedInShortestPath(Graph<String, DefaultEdge> graph, String node){
        int count = 0;

        for(String source : graph.vertexSet()){
            if(source.equals(node)) continue;
            DijkstraShortestPath<String, DefaultEdge> dijkstraAlg = new DijkstraShortestPath<>(graph);
            ShortestPathAlgorithm.SingleSourcePaths<String, DefaultEdge> paths = dijkstraAlg.getPaths(source);

            if(paths == null) continue;

            for(String target: graph.vertexSet()){
                if(source.equals(target)) continue;
                GraphPath<String, DefaultEdge> path = paths.getPath(target);
                if(path == null) continue;
                if(path.getVertexList().contains(node)){
                    count++;
                }
            }
        }
        return count;
    }
    
    // Restituisce la lunghezza media dei cammini minimi tra tutte le coppie di nodi (distanza media)

    public static <V, E> double getAverageDistance(Graph<V, E> graph) {
        long totalPairs = 0;
        double totalLength = 0.0;
    
        for (V source : graph.vertexSet()) {
            DijkstraShortestPath<V, E> dijkstra = new DijkstraShortestPath<>(graph);
            ShortestPathAlgorithm.SingleSourcePaths<V, E> paths = dijkstra.getPaths(source);
            
            for (V target : graph.vertexSet()) {
                if (source.equals(target)) continue;
                
                GraphPath<V, E> path = paths.getPath(target);
                if (path != null) {
                    totalLength += path.getLength();
                    totalPairs++;
                }
            }
        }
        return totalPairs > 0 ? totalLength / totalPairs : 0.0;
    }

    public static <V, E> int getDiameter(Graph<V, E> graph) {
        int maxDistance = 0;
    
        for (V source : graph.vertexSet()) {
            DijkstraShortestPath<V, E> dijkstra = new DijkstraShortestPath<>(graph);
            ShortestPathAlgorithm.SingleSourcePaths<V, E> paths = dijkstra.getPaths(source);
            
            for (V target : graph.vertexSet()) {
                if (source.equals(target)) continue;
                
                GraphPath<V, E> path = paths.getPath(target);
                if (path != null) {
                    int pathLength = path.getLength();
                    if (pathLength > maxDistance) {
                        maxDistance = pathLength;
                    }
                }
            }
        }
        return maxDistance;
    }

    // Restituisce il K-esimo cammino minimo più lungo tra tutte le coppie di nodi

    public static GraphPath<String, DefaultEdge> getKLongestPath(Graph<String, DefaultEdge> graph, int K){

        List<GraphPath<String, DefaultEdge>> allPaths = new ArrayList<>();
        for (String source : graph.vertexSet()) {
            DijkstraShortestPath<String, DefaultEdge> dijkstra = new DijkstraShortestPath<>(graph);
            ShortestPathAlgorithm.SingleSourcePaths<String, DefaultEdge> paths = dijkstra.getPaths(source);
            
            for (String target : graph.vertexSet()) {
                if (source.equals(target)) continue;
                
                GraphPath<String, DefaultEdge> path = paths.getPath(target);
                if (path != null) {
                   allPaths.add(path);
                }
            }
        }
        allPaths.sort((p1, p2) -> Integer.compare(p2.getLength(), p1.getLength()));
        return allPaths.size() >= K ? allPaths.get(K - 1) : allPaths.get(allPaths.size() - 1);
    }

    // Restituisce il vertice con il coefficiente di clustering più alto

    public static String mostClusteredVertex(Graph<String, DefaultEdge> graph){
        String maxClusterVartex = null;
        ClusteringCoefficient<String, DefaultEdge> cc = new ClusteringCoefficient<>(graph);
        for( String vertex  : graph.vertexSet() ){
            double max = Double.MIN_NORMAL;
            double clusteringScore = cc.getVertexScore(vertex);
            if( clusteringScore > max ){
                max = clusteringScore;
            }
            maxClusterVartex = vertex;
        }
        System.out.println("Most clustered vertex is " + maxClusterVartex + " with clustering score: " + cc.getVertexScore(maxClusterVartex));
        return maxClusterVartex;
    }

    public static void biconnectivityReport(Graph<String, DefaultEdge> graph){
        BiconnectivityInspector<String, DefaultEdge> biconnInspector = new BiconnectivityInspector<>(graph);
        if(biconnInspector.isBiconnected()){
            System.out.println("The graph is biconnected.");
        } else {
            System.out.println("The graph is not biconnected.");

            Set<String> articulationNodes = biconnInspector.getCutpoints();
            System.out.println("Critical nodes: " + articulationNodes);

            System.out.println( "\n========================================" + 
                                "\n========================================" + 
                                "\n========================================\n");

            Set<DefaultEdge> articulationEdges = biconnInspector.getBridges();
            System.out.println("Critical edges: " + articulationEdges);
        }
    }

    public static <V, E> void printInfos(Graph<V, E> graph){


        ClusteringCoefficient<V, E> cc = new ClusteringCoefficient<>(graph);

        System.out.println("\n===GRAPH INFO===\n");
        
        V node = graph.vertexSet().iterator().next();
        System.out.println("Vertex: " +  node.getClass().getSimpleName());
        E edge = graph.edgeSet().iterator().next();
        System.out.println("Edges: " + edge.getClass().getSimpleName());

        // Tipo di grafo
        System.out.println("Type: " + graph.getType().getClass().getSimpleName());


        System.out.println("\nNumber of Nodes: " + (graph.vertexSet().size()+1));
        System.out.println("Numbers of Edges: " + (graph.edgeSet().size()+1)); 

        System.out.printf("Average degree: %.2f\n" , getAverageDegree(graph));
        System.out.printf("Average distance: %.2f\n", getAverageDistance(graph));
        System.out.printf("Global clustering Coefficient: %.2f\n", cc.getGlobalClusteringCoefficient() );
    }


    /*
     * =============================================
     *      Metodi di ordinamento dei nodi
     * =============================================
     */

    public static List<Map.Entry<String, Integer>> getVerticesSortedByDegree(Graph<String, ?> graph){
        
        Map<String, Integer> vertexMap = new LinkedHashMap<>();
        for(String vertex : graph.vertexSet()){
            vertexMap.put(vertex, graph.degreeOf(vertex));
        }
        List<Map.Entry<String, Integer>> list = vertexMap.entrySet().stream().sorted(Map.Entry.comparingByValue((a, b) -> b.compareTo(a))).collect(Collectors.toList());
        return list;
    }

    public static List<Map.Entry<String, Double>> getVerticesSortedByClusteringCoefficient(Graph<String, DefaultEdge> graph){
        ClusteringCoefficient<String, DefaultEdge> cc = new ClusteringCoefficient<>(graph);
        Map<String, Double> vertexMap = new LinkedHashMap<>();
        for(String vertex : graph.vertexSet()){
            vertexMap.put(vertex, cc.getVertexScore(vertex));
        }
        List<Map.Entry<String, Double>> list = vertexMap.entrySet().stream().sorted(Map.Entry.comparingByValue((a, b) -> b.compareTo(a))).collect(Collectors.toList());
        return list;
    }

    public static List<Map.Entry<String, Double>> getVerticesSortedByBetweennessCentrality(Graph<String, DefaultEdge> graph){
        BetweennessCentrality<String, DefaultEdge> bc = new BetweennessCentrality<>(graph);
        Map<String, Double> vertexMap = new LinkedHashMap<>();
        for(String vertex: graph.vertexSet()){
            vertexMap.put(vertex, bc.getVertexScore(vertex));
        }
        List<Map.Entry<String, Double>> list = vertexMap.entrySet().stream().sorted(Map.Entry.comparingByValue((a, b) -> b.compareTo(a))).collect(Collectors.toList());
        return list;
    }

    /**
     * Calcola la distribuzione del grado P(k) per un dato grafo.
     * La distribuzione del grado descrive la probabilità che un nodo scelto casualmente
     * abbia un grado k.
     *
     * @param graph Il grafo da analizzare.
     * @param <V>   Il tipo dei vertici.
     * @param <E>   Il tipo degli archi.
     * @return      Una Map<Integer, Double> dove la chiave è il grado (k) e
     * il valore è la probabilità P(k), ovvero la frazione di nodi
     * con quel grado (Nk / N).
     */
    public static <V, E> Map<Integer, Double> getDegreeDistribution(Graph<V, E> graph) {
        // Mappa per contare i nodi per ogni grado (Nk)
        Map<Integer, Integer> degreeCounts = new HashMap<>();
        
        int totalNodes = graph.vertexSet().size();

        // Se il grafo è vuoto, restituisce una mappa vuota
        if (totalNodes == 0) {
            return new TreeMap<>(); // TreeMap per restituire i risultati ordinati per grado
        }

        // 1. Itera su tutti i vertici e conta le occorrenze di ogni grado
        for (V vertex : graph.vertexSet()) {
            int degree = graph.degreeOf(vertex);
            degreeCounts.put(degree, degreeCounts.getOrDefault(degree, 0) + 1);
        }

        // Mappa per memorizzare la distribuzione di probabilità P(k)
        Map<Integer, Double> degreeDistribution = new TreeMap<>(); // TreeMap per l'ordinamento

        // 2. Calcola la probabilità P(k) = Nk / N per ogni grado k
        for (Map.Entry<Integer, Integer> entry : degreeCounts.entrySet()) {
            int degree = entry.getKey();
            int count = entry.getValue(); // Numero di nodi con grado k (Nk)
            
            // Calcola la frazione/probabilità
            double probability = (double) count / totalNodes;
            
            degreeDistribution.put(degree, probability);
        }

        return degreeDistribution;
    }




}
