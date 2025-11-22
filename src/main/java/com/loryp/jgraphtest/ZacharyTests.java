package com.loryp.jgraphtest;

import com.loryp.jgraphtest.mypackage.*;

import java.util.HashSet;
import java.util.Set;
import java.util.Arrays;

import org.jgrapht.*;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.*;





public class ZacharyTests {

    static Graph<String, DefaultEdge> zachary = GraphExtension.createZacharyGraph();

    public static void main(String[] args) {
        
        GraphInportExport.setRedTeamFaction(getRedTeam());
        zachary.removeVertex("v23");
        GraphInportExport.exportZacharyToDOT(zachary, "zacharyNo23.dot");

        
    }

    public static Set<Integer> getRedTeam(){
        DijkstraShortestPath<String, DefaultEdge> dijkstra = new DijkstraShortestPath<>(zachary);
        Set<Integer> redTeam = new HashSet<>();
        Set<Integer> preAdded = new HashSet<>(Arrays.asList(12, 16, 14));
        
        for(String node : zachary.vertexSet()){
            if(node.equals("v0") || node.equals("v23")){
                continue;
            }
            double distToMrHi = dijkstra.getPathWeight("v0", node);
            double distToJohnA = dijkstra.getPathWeight("v23", node);
            if(distToMrHi < distToJohnA && distToJohnA != distToMrHi){
                redTeam.add(Integer.parseInt(node.replace("v", "")));
            }
        }
        redTeam.addAll(preAdded);
        System.out.println("Red Team: " + redTeam.size() + " members.");
        return redTeam;

    }


}