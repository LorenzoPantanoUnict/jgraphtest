package com.loryp.jgraphtest.mypackage;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.nio.Attribute;
import org.jgrapht.nio.DefaultAttribute;
import org.jgrapht.nio.dot.DOTExporter;
import org.jgrapht.nio.dot.DOTImporter;
import org.jgrapht.nio.json.JSONExporter;
import org.jgrapht.nio.json.JSONImporter;

public class GraphInportExport {

    static String dotFilePath = "src/main/resources/dotexported/";
    static String jsonFilepath = "src/main/resources/jsonexported/";

    private static Set<Integer> mrHiFaction;

    public static void setRedTeamFaction(Set<Integer> redTeam){
        mrHiFaction = redTeam;
    }

    public static boolean exportDotGraph(org.jgrapht.Graph<String, DefaultEdge> graph, String graphName){

        DOTExporter<String, DefaultEdge> exporter = new  DOTExporter<>();

        exporter.setVertexAttributeProvider((v) -> {
            Map<String, Attribute> map = new LinkedHashMap<>();
            map.put("label", DefaultAttribute.createAttribute(v.toString()));
            return map;
        });

        String fileOutName = dotFilePath + graphName + ".dot";
        try(Writer fileOut = new FileWriter(fileOutName)){

            exporter.exportGraph(graph, fileOut);
            return true;
        }catch(IOException e){
            System.out.println("Eccezzione IO generata");
            return false;
        }
    }

    public static <V, E> boolean importDotGraph(org.jgrapht.Graph<V, E> graph , String graphName){

        DOTImporter<V, E> importer = new DOTImporter<>();

        String fileIn = dotFilePath + graphName + ".dot";
        try(Reader reader = new FileReader(fileIn)){

            importer.importGraph(graph, reader);

            if(!graph.vertexSet().isEmpty()){
                return true;
            }else return false;

        }catch(IOException e){
            System.out.println("Errore di io nell'importare il grafo: " + graphName);
            return false;
        }

    }

    public  static <V, E> void exportJsonGraph(Graph<V, E> graph, String graphName) {
             
        // Creazione dell'exporter
        JSONExporter<V, E> exporter = new JSONExporter<>();
        String fileName = jsonFilepath+graphName+".json";
        try (Writer writer = new FileWriter(fileName)) {
            exporter.exportGraph(graph, writer);
            System.out.println("Grafo esportato con successo in: " + fileName);
        }catch(IOException e){
            System.out.println("Eccezzione di io nell'esportare il grafo: " + graphName);
        }
        
    }

    public static <V, E> Graph<V, E> importJsonGraph(
            Graph<V, E> targetGraph,
            String graphName) {
        
        String fileName = jsonFilepath + graphName + ".json";
        
        JSONImporter<V, E> importer = new JSONImporter<>();
        
        try (Reader reader = new FileReader(fileName)) {
            importer.importGraph(targetGraph, reader);
            System.out.println("Grafo importato con successo da: " + fileName);
            return targetGraph;
        } catch (IOException e) {
            System.out.println("Eccezione di IO nell'importare il grafo: " + graphName);
            System.out.println("Dettaglio errore: " + e.getMessage());
            return null;
        }
    }

    public static void exportZacharyToDOT(org.jgrapht.Graph<String, DefaultEdge> graph, String fileName) {

        DOTExporter<String, DefaultEdge> exporter = new DOTExporter<>(v -> v);

        
        exporter.setGraphAttributeProvider(() -> {
            Map<String, Attribute> map = new LinkedHashMap<>();
            
            map.put("splines", DefaultAttribute.createAttribute("true")); 
            
            map.put("overlap", DefaultAttribute.createAttribute("false"));
            
            map.put("layout", DefaultAttribute.createAttribute("neato"));
            
            map.put("sep", DefaultAttribute.createAttribute("0.4"));
            
            return map;
        });

        exporter.setVertexAttributeProvider(v -> {
            Map<String, Attribute> map = new LinkedHashMap<>();
            map.put("label", DefaultAttribute.createAttribute(v));
            map.put("style", DefaultAttribute.createAttribute("filled"));
            map.put("fixedsize", DefaultAttribute.createAttribute("true")); 
            map.put("shape", DefaultAttribute.createAttribute("circle"));

            int nodeId = -1;
            try { nodeId = Integer.parseInt(v.replace("v", "")); } catch (Exception e) {}

            if (nodeId == 0) { // Mr. Hi
                map.put("fillcolor", DefaultAttribute.createAttribute("#FF0000")); 
                map.put("fontsize", DefaultAttribute.createAttribute("18"));       
                map.put("width", DefaultAttribute.createAttribute("0.6"));         
                map.put("height", DefaultAttribute.createAttribute("0.6"));        
            } 
            else if (nodeId == 23) { // John A
                map.put("fillcolor", DefaultAttribute.createAttribute("#0000FF")); 
                map.put("fontsize", DefaultAttribute.createAttribute("18"));
                map.put("width", DefaultAttribute.createAttribute("0.6"));
                map.put("height", DefaultAttribute.createAttribute("0.6"));
            } 
            else if (mrHiFaction.contains(nodeId)) { // Fazione Mr. Hi
                map.put("fillcolor", DefaultAttribute.createAttribute("#ca2323ff")); 
                map.put("fontsize", DefaultAttribute.createAttribute("15"));       
                map.put("width", DefaultAttribute.createAttribute("0.4"));         
                map.put("height", DefaultAttribute.createAttribute("0.4"));
            } 
            else { // Fazione John A
                map.put("fillcolor", DefaultAttribute.createAttribute("#2222cfff")); 
                map.put("fontsize", DefaultAttribute.createAttribute("15"));
                map.put("width", DefaultAttribute.createAttribute("0.4"));
                map.put("height", DefaultAttribute.createAttribute("0.4"));
            }
            return map;
        });

        // === ESPORTAZIONE ===
        String fullPath = dotFilePath + fileName; 
        try (Writer writer = new FileWriter(fullPath)) {
            exporter.exportGraph(graph, writer);
            System.out.println("Grafo Zachary esportato correttamente in: " + fullPath);
        } catch (IOException e) {
            System.err.println("Errore DOT: " + e.getMessage());
        }
    }


}
