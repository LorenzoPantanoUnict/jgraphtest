package com.loryp.jgraphtest.mypackage;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Writer;
import java.util.*;

import org.jgrapht.traverse.DepthFirstIterator;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.nio.Attribute;
import org.jgrapht.nio.DefaultAttribute;
import org.jgrapht.nio.dot.DOTExporter;

import org.graphstream.graph.implementations.SingleGraph;
import org.graphstream.graph.Node;

public  class GraphVisualizer {

    private static Integer conversionCount;
    private static Integer edgeId;
    
    String tempDir = System.getProperty("java.io.tmpdir");
    static String dotFilePath = "src/main/resources/dotexported/";
    String commandPath = "src/main/resources/scripts/viewGraph.ps1";

    // Stile specifico per Zachary: due colori distinti + nodi leader più grandi
    protected String zacharyStyleSheet =
            "graph {" +
            "   padding: 15px;" + // Aggiunge spazio ai bordi della finestra
            "}" +
            "node {" +
            "   size: 25px;" + // Nodi molto più grandi
            "   fill-color: #E0E0E0;" + // Grigio chiaro di base
            "   stroke-mode: plain;" + // Bordo nero
            "   stroke-color: black;" +
            "   stroke-width: 2px;" +
            "   text-mode: normal;" +
            "   text-color: black;" +
            "   text-style: bold;" +
            "   text-size: 20px;" + // Testo più grande
            "   text-alignment: center;" + // Testo al centro del nodo
            "}" +
            // Fazione Mr. Hi (Rosso Pastello)
            "node.mrHi {" +
            "   fill-color: #ff0000c8;" + 
            "}" +
            // Fazione John A (Blu/Ciano Pastello)
            "node.johnA {" +
            "   fill-color: #3700ffff;" + 
            "}" +
            // Leader (Ancora più grandi e colore più intenso)
            "node.leader {" +
            "   size: 60px;" +
            "   fill-color: #ff0000ff;" + // Rosso scuro per Mr Hi
            "   stroke-width: 4px;" +
            "}" +
            // Sovrascrittura specifica per il leader John A (per dargli il blu scuro)
            "node.leader.johnA {" +
            "   fill-color: #000d6fff;" + 
            "}" +
            "edge {" +
            "   fill-color: #414141fd;" + // Archi grigio scuro
            "   size: 2px;" + // Archi più spessi
            "}";



    public GraphVisualizer(){
        conversionCount = 0;
        edgeId =0;
    }

    private String getEdgeId(){
        edgeId++;
        return edgeId.toString();
    }

    public void displayGraphStream(org.jgrapht.Graph<String, DefaultEdge> jgrapht ){

        conversionCount++;
        org.graphstream.graph.Graph graph = new SingleGraph(conversionCount.toString());


        Iterator<String> iter = new DepthFirstIterator<>(jgrapht);
        while(iter.hasNext()){
            String vertex = iter.next();
            graph.addNode(vertex);
        }

        Set<DefaultEdge> edges = jgrapht.edgeSet();

        for(DefaultEdge edge: edges){
            graph.addEdge(getEdgeId(), jgrapht.getEdgeSource(edge).toString(), jgrapht.getEdgeTarget(edge).toString());
        }
        

        System.setProperty("org.graphstream.ui", "swing");
        graph.display();
    }
    

    public void displayDOTGraphStream(org.jgrapht.Graph<String, DefaultEdge> graph, String name ){
        DOTExporter<String, DefaultEdge> graphExporter =new DOTExporter<>(v -> v.toString());

        graphExporter.setVertexAttributeProvider((v) -> {
            Map<String, Attribute> map = new LinkedHashMap<>();
            map.put("label", DefaultAttribute.createAttribute(v.toString()));
            return map;
        });

        try(Writer writer = new FileWriter(dotFilePath)){
            graphExporter.exportGraph(graph, writer);
        }catch(IOException e){
            System.err.println("Error: " + e.getMessage());
            return;
        }

        System.out.println("File written and closed, now running command...");
        runCommand(commandPath);
    }

    public void runCommand(String commandPath){
        try{
            ProcessBuilder processBuilder = new ProcessBuilder( 
                                                            "powershell.exe", 
                                                            "-ExecutionPolicy", "Bypass",
                                                            "-File", commandPath,
                                                            "-dotFilePath", dotFilePath);

            processBuilder.directory(new File("."));
            processBuilder.redirectErrorStream(true);

            Process process = processBuilder.start();

            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream())
            );
            
            String line;
            while((line = reader.readLine()) != null){
                System.out.println("PS Output: " + line);
            }
            int exitCode = process.waitFor();
            System.out.println("Exit code: "+ exitCode);

        }catch(IOException | InterruptedException e){
            e.printStackTrace();
        }
    }
    
    /**
     * Visualizza il grafo Zachary Karate Club colorando le due fazioni.
     * I dati sulla divisione sono basati sul dataset originale di W. Zachary (1977).
     */
    public void displayZacharyGraph(org.jgrapht.Graph<String, DefaultEdge> jgrapht) {
        
        conversionCount++;
        org.graphstream.graph.Graph graph = new SingleGraph("Zachary-" + conversionCount);

        // 1. Stile (Usa quello migliorato che ti ho dato prima)
        graph.setAttribute("ui.stylesheet", zacharyStyleSheet);
        graph.setAttribute("ui.quality");
        graph.setAttribute("ui.antialias");

        // Fisica un po' più forte per spingere via i nodi
        graph.setAttribute("layout.force", 1.0); 
        graph.setAttribute("layout.quality", 4); 

        Set<Integer> mrHiFaction = new HashSet<>(Arrays.asList(
            0, 1, 2, 3, 4, 5, 6, 7, 8, 10, 11, 12, 13, 16, 17, 19, 21
        ));

        Iterator<String> iter = new DepthFirstIterator<>(jgrapht);
        while(iter.hasNext()){
            String vertexLabel = iter.next(); 
            Node n = graph.addNode(vertexLabel);
            n.setAttribute("ui.label", vertexLabel);

            int nodeId;
            try {
                nodeId = Integer.parseInt(vertexLabel.replace("v", ""));
            } catch (NumberFormatException e) { nodeId = -1; }

            // --- LOGICA COLORI ---
            if (mrHiFaction.contains(nodeId)) {
                n.setAttribute("ui.class", "mrHi");
            } else {
                n.setAttribute("ui.class", "johnA");
            }

            // --- LOGICA POSIZIONAMENTO FORZATO (TRUCCO PER LA TESI) ---
            // Se è il leader Mr. Hi (v0), lo mettiamo a SINISTRA e lo blocchiamo
            if (nodeId == 0) {
                n.setAttribute("ui.class", "mrHi leader");
                n.setAttribute("x", -3); // Coordinate arbitrarie (Sinistra)
                n.setAttribute("y", 0);
                n.setAttribute("z", 0);
                n.setAttribute("layout.frozen"); // LO BLOCCA LÌ: La fisica non può muoverlo
            }
            // Se è il leader John A (v33), lo mettiamo a DESTRA e lo blocchiamo
            else if (nodeId == 33) {
                n.setAttribute("ui.class", "johnA leader");
                n.setAttribute("x", 3);  // Coordinate arbitrarie (Destra)
                n.setAttribute("y", 0);
                n.setAttribute("z", 0);
                n.setAttribute("layout.frozen"); // LO BLOCCA LÌ
            }
        }

        Set<DefaultEdge> edges = jgrapht.edgeSet();
        for(DefaultEdge edge: edges){
            graph.addEdge(getEdgeId(), 
                          jgrapht.getEdgeSource(edge).toString(), 
                          jgrapht.getEdgeTarget(edge).toString());
        }

        System.setProperty("org.graphstream.ui", "swing");
        
        // Avvia in un thread separato
        org.graphstream.ui.view.Viewer viewer = graph.display(false);
        
        // Attiva la fisica: i nodi v0 e v33 rimarranno fermi agli opposti,
        // gli altri si sistemeranno automaticamente intorno al loro leader.
        viewer.enableAutoLayout(); 
    }

    
    
}




