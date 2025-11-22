package com.loryp.jgraphtest.mypackage;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.random.RandomGenerator;

import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.Graphs;
import org.jgrapht.alg.connectivity.ConnectivityInspector;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;
import org.jgrapht.traverse.BreadthFirstIterator;
import org.jgrapht.traverse.DepthFirstIterator;

//import com.loryp.jgraphtest.ZacharyTests;

public class GraphTest {

    static String testFolder = "C:\\Users\\loryp\\OneDrive\\Documenti\\Java\\jgraphtest\\src\\main\\resources\\TestsLog\\";

    public static void cleanTestLogFile(String logName){
        String outputFilePath = testFolder + logName + ".txt";
        try(PrintWriter writer = new PrintWriter(new FileWriter(outputFilePath, false))){
            // Pulizia del file
        }catch(IOException e){
            System.err.println("Errore nella pulizia del file di log: " + e.getMessage());
        }
    }

    private static void warmUp(Graph<String, DefaultEdge> graph) {
        // Esegue gli algoritmi alcune volte senza misurare
        for (int i = 0; i < 5; i++) {
            // Warm-up DFS
            DepthFirstIterator<String, DefaultEdge> dfs = new DepthFirstIterator<>(graph, "v0");
            while (dfs.hasNext()) {
                dfs.next(); 
            }
            
            // Warm-up BFS  
            BreadthFirstIterator<String, DefaultEdge> bfs = new BreadthFirstIterator<>(graph, "v0");
            while (bfs.hasNext()) {
                bfs.next(); 
            }
        }
    }


    public static List<Double>  testBFSvsDFS(Graph<String, DefaultEdge> graph, String logName, int tries) {
        String outputFilePath = testFolder + logName + ".txt";
        try (PrintWriter writer = new PrintWriter(new FileWriter(outputFilePath, true))) {
            long startTime, endTime, duration;
            int iterations = tries;
            writer.print("\n--------------DFS-------------\n\n");
            
            //Iterazione DFS
            Set<String> seenVertex = new LinkedHashSet<>();
            ArrayList<Long> dfTimes = new ArrayList<>();
            warmUp(graph);
            for(int i = 0; i<iterations; i++){
                
                DepthFirstIterator<String, DefaultEdge> dfIterator = new DepthFirstIterator<>(graph, "v0");
                
                startTime = System.nanoTime();
                while(dfIterator.hasNext()){
                    
                    String vertex = dfIterator.next();
                    seenVertex.add(vertex);
                    

                }
                endTime = System.nanoTime();
                duration = endTime - startTime;
                dfTimes.add(duration);
                if(i==0){

                    for(String vertex: seenVertex){
                        writer.print(vertex + "->");
                    }
                    writer.println("END");
                    
                }
                
            }

            writer.print("\n--------------BFS-------------\n\n");

            //Iterazione BFS
            seenVertex.clear();
            ArrayList<Long> bfsTimes = new ArrayList<>();
            for(int i = 0; i<iterations; i++){
                
                BreadthFirstIterator<String, DefaultEdge> bfIterator = new BreadthFirstIterator<>(graph, "v0");
                
                startTime = System.nanoTime();
                while(bfIterator.hasNext()){
                    
                    String vertex = bfIterator.next();
                    seenVertex.add(vertex);
    
                }
                endTime = System.nanoTime();
                duration = endTime - startTime;
                bfsTimes.add(duration);
                if(i==0){

                    for(String vertex: seenVertex){
                        writer.print(vertex + "->");
                    }
                    writer.println("END");

                }
                
            }

            writer.print("\n----------STATS----------\n\n");

            double totalDfTime = 0;
            for(Long time : dfTimes){
                totalDfTime += time;
            }
            double averageDfTime = totalDfTime / (double)dfTimes.size() / 1000.0; // Convert to microseconds
            writer.println("Average Depth-First Search (DFS) traversal time over " + iterations +  " runs: " + averageDfTime + " microseconds");


            double totalBfTime = 0; 
            for(Long time : bfsTimes){
                totalBfTime += time;
            }   
            double averageBfTime = totalBfTime / (double)bfsTimes.size() / 1000.0; // Convert to microseconds
            writer.println("Average Breadth-First Search (BFS) traversal time over " + iterations + " runs: " + averageBfTime + " microseconds");

            // CALCOLO VANTAGGIO PERCENTUALE
            if (averageBfTime < averageDfTime) {
                // BFS è più veloce
                double percentFaster = ((averageDfTime - averageBfTime) / averageDfTime) * 100;
                writer.printf("BFS is %.1f%% faster than DFS\n", percentFaster);
            } else {
                // DFS è più veloce
                double percentFaster = ((averageBfTime - averageDfTime) / averageBfTime) * 100;
                writer.printf("DFS is %.1f%% faster than BFS\n", percentFaster);
            }

            writer.print("\n--------------------------\n\n");

            System.out.println("Risultati del test: " + outputFilePath);

            List<Double> results = new ArrayList<>();
            // Aggiungi i risultati alla lista
            results.add(averageBfTime);
            results.add(averageDfTime);

            return results;
            
        } catch (IOException e) {
            System.err.println("Errore nella scrittura del file: " + e.getMessage());
            return null;
        }
    }

    public static GraphPath<String, DefaultEdge> testDijkstraShortestPath(Graph<String, DefaultEdge> graph, String source, String target){
        long startTime, endTime;
        DijkstraShortestPath<String, DefaultEdge> dijkstraAlg = new DijkstraShortestPath<>(graph);

        startTime = System.nanoTime();
        GraphPath<String, DefaultEdge> path = dijkstraAlg.getPath(source, target);
        endTime = System.nanoTime();

        System.out.println("Shortest path from " + source + " to " + target + ": "+ path);
        System.out.println("Path length: " + path.getLength());
        System.out.println("Computation time: " + (endTime - startTime)/1000.0 + " microseconds");
        return path;
    }

    public static void removeRandomNodes(Graph<String, DefaultEdge> graph, int n, boolean verbose) {
    // 1. Ottieni la lista dei vertici ATTUALMENTE presenti nel grafo
    List<String> vertexList = new ArrayList<>(graph.vertexSet());

    if (vertexList.isEmpty()) {
        return;
    }

    for (int i = 0; i < n; i++) {
        if (vertexList.isEmpty()) {
            break;
        }
        
        // 2. Scegli un indice casuale basato sulla dimensione reale della lista
        int randomIndex = RandomGenerator.getDefault().nextInt(0, vertexList.size());
        
        // 3. Ottieni l'ID del vertice a quell'indice (es. "1", "55", "v9" - funziona con tutto)
        String vertexToRemove = vertexList.get(randomIndex);
        
        // 4. Rimuovi il vertice dal grafo
        boolean removed = graph.removeVertex(vertexToRemove);
        
        // Rimuovilo anche dalla lista locale per evitare di riselezionarlo nel ciclo for attuale
        vertexList.remove(randomIndex);

        if (verbose && removed) {
            System.out.println("Removed node: " + vertexToRemove);
        }
    }
}

    // Restituisce il numero medio di nodi rimossi per disconnettere il grafo dopo un certo numero di test
    public static double averageNodesRemovedUntilDisconnected(Graph<String, DefaultEdge> graph, int trials, boolean verbose){

        List<Integer> removedCounts = new ArrayList<>();
        for(int i=0; i<trials; i++){
            int removed = 0;
            Graph<String, DefaultEdge> graphCopy = new SimpleGraph<>(DefaultEdge.class);
            Graphs.addGraph(graphCopy, graph);
            
            ConnectivityInspector<String, DefaultEdge> connInspector = new ConnectivityInspector<>(graphCopy);
            while(connInspector.isConnected()){
                removeRandomNodes(graphCopy, 1, false);
                connInspector = new ConnectivityInspector<>(graphCopy);
                removed++;
            }
            
            if(i==0 && verbose){
                GraphInportExport.exportDotGraph(graphCopy, "Graphdisconnected.dot");

            }
            removedCounts.add(removed);
        }
        double average = removedCounts.stream().mapToInt(Integer::intValue).average().orElse(0.0);
        if(verbose){
            System.out.println("Numero medio di nodi rimossi per disconnettere il grafo con "+ trials + " test: " + average);
        }
        return average;
    }

    public static List<Integer> testAnalisiRimozioneMirata(List<Graph<String, DefaultEdge>> graphs){
    
    if(graphs.isEmpty()){
        return null;
    }

    List<Integer> results = new ArrayList<>();

    for(Graph<String, DefaultEdge> g : graphs){
        if(g.vertexSet().isEmpty()){
            System.out.println("Grafo vuoto, nessun nodo da rimuovere.");
            results.add(0); // Aggiungi 0 per mantenere l'allineamento degli indici
            continue;
        }

        // --- CORREZIONE: Creiamo una COPIA del grafo ---
        Graph<String, DefaultEdge> graphCopy = new SimpleGraph<>(DefaultEdge.class);
        Graphs.addGraph(graphCopy, g); 
        // -----------------------------------------------

        int removed = 0;
        ConnectivityInspector<String, DefaultEdge> connInspector = new ConnectivityInspector<>(graphCopy);
        
        // Lavoriamo sulla COPIA (graphCopy), non su g
        while(connInspector.isConnected()){
            // Assicurati che getMaxDegreeNode accetti la copia o sia un metodo statico
            String highestDegreeVertex = GraphExtension.getMaxDegreeNode(graphCopy); 
            
            if(highestDegreeVertex != null) {
                graphCopy.removeVertex(highestDegreeVertex);
                removed++;
                connInspector = new ConnectivityInspector<>(graphCopy);
            } else {
                break; // Evita loop infiniti se qualcosa va storto
            }
        }
        results.add(removed);
    }

    return results;
}
}