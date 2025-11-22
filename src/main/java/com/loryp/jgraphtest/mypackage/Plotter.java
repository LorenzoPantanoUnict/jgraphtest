package com.loryp.jgraphtest.mypackage;

// Import per XChart (libreria di plotting)
import org.knowm.xchart.XYChart;
import org.knowm.xchart.XYChartBuilder;
import org.knowm.xchart.XYSeries;
import org.knowm.xchart.style.Styler;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

//jtablesaw
import tech.tablesaw.api.DoubleColumn;
import tech.tablesaw.api.StringColumn;
import tech.tablesaw.api.Table;
import tech.tablesaw.plotly.Plot;
import tech.tablesaw.plotly.components.Figure;
import tech.tablesaw.plotly.components.Layout;
import tech.tablesaw.plotly.traces.BarTrace;
import tech.tablesaw.plotly.components.Axis;
import tech.tablesaw.plotly.components.Marker;


import org.knowm.xchart.SwingWrapper; // Per mostrare il grafico

public class Plotter {


    /**
     * Plotta una o più distribuzioni di grado (P(k) vs k) su un unico grafico.
     * Ogni distribuzione nella lista sarà una serie diversa con un colore diverso.
     *
     * ATTENZIONE: Questo metodo richiede la libreria XChart.
     * Assicurati di averla aggiunta alle dipendenze del tuo progetto.
     *
     * @param distributions Una lista di distribuzioni di grado. Ogni mappa
     * rappresenta una distribuzione (k -> P(k)).
     * @param title         Il titolo del grafico (es. "Distribuzione del Grado").
     * @param xAxisTitle    Il titolo per l'asse X (es. "Grado (k)").
     * @param yAxisTitle    Il titolo per l'asse Y (es. "Probabilità P(k)").
     */
    public static void plotDegreeDistributions(
            List<Map<Integer, Double>> distributions, 
            String title, 
            String xAxisTitle, 
            String yAxisTitle) {

        XYChart chart = new XYChartBuilder()
                .width(800)
                .height(600)
                .title(title)
                .xAxisTitle(xAxisTitle)
                .yAxisTitle(yAxisTitle)
                .build();

        chart.getStyler().setLegendPosition(Styler.LegendPosition.InsideNE);
        chart.getStyler().setDefaultSeriesRenderStyle(XYSeries.XYSeriesRenderStyle.Line);
        chart.getStyler().setMarkerSize(5);
        chart.getStyler().setPlotGridLinesVisible(true);

        int seriesCounter = 1;
        for (Map<Integer, Double> distribution : distributions) {
            
            Map<Integer, Double> sortedDistribution = new TreeMap<>(distribution);

            List<Integer> xData = new ArrayList<>(sortedDistribution.keySet());
            List<Double> yData = new ArrayList<>(sortedDistribution.values());

            chart.addSeries("Grafo " + seriesCounter, xData, yData);
            seriesCounter++;
        }

        new SwingWrapper<>(chart).displayChart();
    }

    /**
     * Plotta un grafico a barre (ID vs Valore) partendo da una lista di Entry.
     * L'ordine delle barre nel grafico seguirà l'ordine della lista passata.
     */
    public static void plotBetweennessCentralityDistribution(List<Map.Entry<String, Double>> entryList) {

        // 1. Controllo difensivo
        if (entryList == null || entryList.isEmpty()) {
            System.out.println("Lista vuota, nessun grafico da generare.");
            return;
        }

        int size = entryList.size();
        List<String> ids = new ArrayList<>(size);
        List<Double> values = new ArrayList<>(size);
        
        String[] colors = new String[size];

        for (int i = 0; i < size; i++) {
            Map.Entry<String, Double> entry = entryList.get(i);
            ids.add(entry.getKey());
            values.add(entry.getValue());

            if (i == 0) {
                colors[i] = "red";     
            } else if (i == 1) {
                colors[i] = "blue";    
            } else if(i>1 && i<5){
                colors[i] = "green";   
            }else{
                colors[i] = "skyblue"; 
            }
        }

        // 3. Creazione Colonne Tablesaw
        StringColumn idCol = StringColumn.create("Vertex ID", ids);
        DoubleColumn valCol = DoubleColumn.create("Betweenness Value", values);
        
        Table table = Table.create("Data", idCol, valCol);

        // --- COSTRUZIONE MANUALE GRAFICO ---

        // 4. Assi
        Axis xAxis = Axis.builder().title("ID Vertice").build();
        Axis yAxis = Axis.builder().title("Betweenness Centrality (Double)").build();

        // 5. Layout
        Layout layout = Layout.builder()
                .title("Top Vertices by Betweenness Centrality")
                .xAxis(xAxis)
                .yAxis(yAxis)
                .width(900)
                .height(600)
                .build();

        // 6. Traccia (Barre con Colori Personalizzati)
        BarTrace trace = BarTrace.builder(
                    table.stringColumn("Vertex ID"), 
                    table.numberColumn("Betweenness Value")
                )
                .marker(Marker.builder().color(colors).build()) // Applichiamo l'array di colori
                .build();

        // 7. Visualizzazione
        Plot.show(
            Figure.builder()
                .layout(layout)
                .addTraces(trace)
                .build()
        );
    }

    public static void plotAnalisiGrado(List<List<Map.Entry<String, Integer>>> dataSet) {

        if (dataSet == null || dataSet.isEmpty()) {
            System.out.println("DataSet vuoto.");
            return;
        }

        int numGraphs = dataSet.size();
        int topN = 5; 

        Layout layout = Layout.builder()
                .title("Top 5 Hub per grafo (Raggruppati)")
                .barMode(Layout.BarMode.GROUP) 
                .xAxis(Axis.builder().title("Istanze Grafo").build())
                .yAxis(Axis.builder().title("Grado").build())
                .width(1200)
                .height(600)
                .build();

        BarTrace[] traces = new BarTrace[topN];
        
        String[] rankColors = {"crimson", "darkblue", "green", "lightgreen", "lightblue"};

        for (int rank = 0; rank < topN; rank++) {
            
            List<String> xLabels = new ArrayList<>(); 
            List<Double> yValues = new ArrayList<>(); 

            for (int i = 0; i < numGraphs; i++) {
                List<Map.Entry<String, Integer>> graphNodes = dataSet.get(i);
                
 
                if (graphNodes != null && rank < graphNodes.size()) {
                    Map.Entry<String, Integer> node = graphNodes.get(rank);
                    xLabels.add("Grafo " + (i + 1));
                    yValues.add(node.getValue().doubleValue());
                } else {
                    xLabels.add("Grafo " + (i + 1));
                    yValues.add(0.0);
                }
            }

            StringColumn xCol = StringColumn.create("X", xLabels);
            DoubleColumn yCol = DoubleColumn.create("Y", yValues);
            
            
            traces[rank] = BarTrace.builder(xCol, yCol)
                    .name("Hub " + (rank + 1) + "°") 
                    .marker(Marker.builder().color(rankColors[rank]).build())
                    .build();
        }

       
        Plot.show(new Figure(layout, traces));
    }

    public static void plotAnalisiSmallWorld(int[] diameters, double[] avgDistances) {
        int  numColonne= 2;
        int numGraphs = diameters.length;
        if(numGraphs != avgDistances.length){
            System.out.println("Errore: I dati forniti non hanno la stessa lunghezza.");
            return;
        }
        if(numGraphs == 0 ||  avgDistances.length ==0){
            System.out.println("Errore: Nessun dato da plottare.");
            return;
        }


        // 2. Configurazione Layout
        Layout layout = Layout.builder()
                .title("Analisi Small-World")
                .barMode(Layout.BarMode.GROUP) 
                .xAxis(Axis.builder().title("Istanze Grafo").build())
                .yAxis(Axis.builder().title("score").build())
                .width(1200) 
                .height(600)
                .build();

        BarTrace[] traces = new BarTrace[numColonne];

        String[] colors = {"darkblue","blue"};
        String[] leggenda = {"Diametro", "Distanza Media"};
        for(int colonna = 0; colonna <numColonne; colonna++){

            List<String> xLabels = new ArrayList<>();
            List<Double> yValues = new ArrayList<>();

            for(int i = 0; i<numGraphs; i++){
                xLabels.add("Grafo " + (i + 1));
                if(colonna == 0){
                    yValues.add((double)diameters[i]);
                }
                else{
                    yValues.add(avgDistances[i]);
                }
            }
            StringColumn xCol = StringColumn.create("X", xLabels);
            DoubleColumn yCol = DoubleColumn.create("Y", yValues);

            traces[colonna] = BarTrace.builder(xCol, yCol)
                    .name(leggenda[colonna]) 
                    .marker(Marker.builder().color(colors[colonna]).build())
                    .build();
        }

        Plot.show(new Figure(layout, traces));

    }

    public static void plotAnalisiRimozioneCasuale(double[] valori) {

        String grafi[] = new String[valori.length];

        for (int i = 0; i < valori.length; i++) {
            grafi[i] = "Grafo " + (i + 1);
        }

        Table table = Table.create("Robustness",
                StringColumn.create("Grafo", grafi),
                DoubleColumn.create("Valore", valori)
        );

        String[] colors = {
            "#FF0000", // 1. Rosso Puro
            "#FF4500", // 2. Orange Red
            "#FF8C00", // 3. Dark Orange
            "#FFA500", // 4. Orange
            "#FFD700", // 5. Gold
            "#FFFF00", // 6. Giallo
            "#ADFF2F", // 7. Green Yellow
            "#7FFF00", // 8. Chartreuse
            "#32CD32", // 9. Lime Green
            "#008000"  // 10. Verde (Green)
        };
        
        
        Axis xAxis = Axis.builder()
                .title("Nodi rimossi (Media)")
                .range(10, 18) 
                .build();
                
        Axis yAxis = Axis.builder().title("Istanza").build();

        Layout layout = Layout.builder()
                .title("Robustezza dei Grafi (Resistenza alla rimozione nodi)")
                .xAxis(xAxis)
                .yAxis(yAxis)
                .height(600)
                .width(800)
                .build();

        BarTrace trace = BarTrace.builder(
                    table.stringColumn("Grafo"), 
                    table.numberColumn("Valore") 
                )
                .orientation(BarTrace.Orientation.HORIZONTAL) 
                .marker(Marker.builder().color(colors).build())
                .opacity(0.8)
                .build();

        Plot.show(new Figure(layout, trace));
    }

    public static void plotAnalisiAttraversamento(List<List<Double>> results){
        int numColonne = 2;
        int numGraphs = results.size();
        if(numGraphs == 0 || results.get(0).size() != numColonne){
            System.out.println("Errore: I dati forniti non sono corretti.");
            return;
        }

        Layout layout = Layout.builder()
                .title("Analisi Attraversamento BFS vs DFS")
                .barMode(Layout.BarMode.GROUP) 
                .xAxis(Axis.builder().title("Istanze Grafo").build())
                .yAxis(Axis.builder().title("time (microSeconds)").build())
                .width(1200) 
                .height(600)
                .build();

        BarTrace[] traces = new BarTrace[numColonne];

        String[] colors = {"Green","Red"};
        String[] leggenda = {"BFS", "DFS"};

        for(int colonna = 0; colonna<numColonne; colonna++){
            List<String> xLabels = new ArrayList<>();
            List<Double> yValues = new ArrayList<>();

            for(int i = 0; i<numGraphs; i++){
                xLabels.add("Grafo " + (i + 1));
                yValues.add(results.get(i).get(colonna));
            }
            StringColumn xCol = StringColumn.create("X", xLabels);
            DoubleColumn yCol = DoubleColumn.create("Y", yValues);
    
            traces[colonna] = BarTrace.builder(xCol, yCol)
                .name(leggenda[colonna]) 
                .marker(Marker.builder().color(colors[colonna]).build())
                .build();
        }
        
        Plot.show(new Figure(layout, traces));
    }

    public static void plostConfrontoRImozioneCasualeMirata(double[] casuale, List<Integer> mirata){
        int numColonne = 2;

        int numGraphs = casuale.length;
        if(numGraphs != mirata.size()){
            System.out.println("Errore: I dati forniti non hanno la stessa lunghezza.");
            return;
        }

        Layout layout = Layout.builder()
                .title("Confronto Rimozione Casuale vs Mirata")
                .barMode(Layout.BarMode.GROUP) 
                .xAxis(Axis.builder().title("Istanze Grafo").build())
                .yAxis(Axis.builder().title("Nodi Rimossi").build())
                .width(1200) 
                .height(600)
                .build();

        BarTrace[] traces = new BarTrace[numColonne];

        String[] colors = {"green","crimson"};

        String[] leggenda = {"Rimozione Casuale", "Rimozione Mirata"};

        for(int colonna = 0; colonna <numColonne; colonna++){

            List<String> xLabels = new ArrayList<>();
            List<Double> yValues = new ArrayList<>();

            for(int i = 0; i<numGraphs; i++){
                xLabels.add("Grafo " + (i + 1));
                if(colonna == 0){
                    yValues.add(casuale[i]);
                }
                else{
                    yValues.add((double)mirata.get(i));
                }
            }
            StringColumn xCol = StringColumn.create("X", xLabels);
            DoubleColumn yCol = DoubleColumn.create("Y", yValues);

            traces[colonna] = BarTrace.builder(xCol, yCol)
                    .name(leggenda[colonna]) 
                    .marker(Marker.builder().color(colors[colonna]).build())
                    .build();
        }

        Plot.show(new Figure(layout, traces));

    }
}
