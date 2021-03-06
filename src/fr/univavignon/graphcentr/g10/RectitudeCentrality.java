package fr.univavignon.graphcentr.g10;

import fr.univavignon.graphcentr.g07.core.centrality.CentralityResult;
import fr.univavignon.graphcentr.g07.core.centrality.SpatialWeightedCentrality;
import fr.univavignon.graphcentr.g07.core.graphs.SpatialWeightedGraph;
import fr.univavignon.graphcentr.g07.core.readers.GraphMLReader;
import fr.univavignon.graphcentr.g07.core.utility.Benchmark;

/**
 * @author Christophe
 * @version 1.0
 * Cette classe permet définie la mesure de rectitude sur un graphe simple spatial et pondéré
 */
public class RectitudeCentrality implements SpatialWeightedCentrality {
	
	private SpatialWeightedGraph graph = null ;
	
	/**
	 * @author Christophe
	 */
	public RectitudeCentrality(SpatialWeightedGraph graph) {
		this.graph = graph ;
	}
	
	/**
	 * @author Christophe
	 * @param graph 			un graphe spatial
	 * @return adjacencyMatrix	la matrice d'adjacence (distance)
	 */
	private double[][] WarshallFloyd() {
		// recupère le nombre de noeud du graphe
		int nbNodes = graph.getNodeCount();
		// recupère la matrice d'adjacence du graphe
		double[][] adjacencyMatrix = graph.toAdjacencyMatrix();
		{
			double[][] adjacencyMatrix2 = new double[nbNodes][nbNodes];
			for	(int i = 0 ; i < nbNodes ; i++) {
				for (int j = 0 ; j < nbNodes ; j++) {				
					if (i == j  || adjacencyMatrix[i][j] != 0) {
						adjacencyMatrix2[i][j] = adjacencyMatrix[i][j];
					}
					else {
						adjacencyMatrix2[i][j] = Double.MAX_VALUE; 
					}
				}
			}
			adjacencyMatrix = adjacencyMatrix2 ;	
		}
		// initialisation de chaque case avec la valeur max possible
		for(int k = 0 ; k < nbNodes ; k++) {
			for(int i = 0 ; i < nbNodes ; i++) {
				for (int j = 0 ; j < nbNodes ; j++) {
					if (adjacencyMatrix[i][j] > adjacencyMatrix[i][k] + adjacencyMatrix[k][j]) {
						adjacencyMatrix[i][j] = adjacencyMatrix[i][k] + adjacencyMatrix[k][j];
					}
				}
			}
		}
		return adjacencyMatrix ;
	}
	
	/**
	 * @author Christophe
	 * @param graph			un graphe spatial
	 * @return moyenne		un tableau de moyenne
	 */
	private double[] rectitudeMoyenne() {
		int nbNodes = graph.getNodeCount();
		double[][] distanceShortestPathMatrix = WarshallFloyd();
		double[] moyenne = new double[nbNodes];
		for (int k = 0 ; k < nbNodes ; k++) {
			for (int j = 0 ; j < nbNodes ; j++) {
				if (k != j) {
					moyenne[k] += StraightnessNodes(k, j, distanceShortestPathMatrix[k][j]);
				}
			}
			moyenne[k] /= nbNodes -1;			
		}
		return moyenne ;
	}
	
	/**
	 * @author Habib Mohamed
	 * @param k
	 * @param j
	 * @param distanceShortestPath
	 * @return resStraightness
	 */
	public double StraightnessNodes(int k, int j, double distanceShortestPath)
	{
		//Initialisation
		double resStraightness = 0;
		double de = graph.getEuclideanDistance(graph.getNodeAt(k), graph.getNodeAt(j));
		
		if (distanceShortestPath == Double.MAX_VALUE) {
			resStraightness = 0;
		} else {
			if (distanceShortestPath == de) {
				resStraightness = 1;
			}
			else {
				resStraightness = de / distanceShortestPath;
			}
		}
		return resStraightness;
	}
	
	/**
	 * @author Christophe
	 * @param inGraph
	 * @return result
	 */
	@Override
	public CentralityResult evaluate(SpatialWeightedGraph inGraph) {
		CentralityResult result = new CentralityResult();
		this.graph = inGraph ;
		
		double[] moyenne = rectitudeMoyenne();
		for (int i = 0 ; i < moyenne.length ; i++) {
			result.add(moyenne[i]);
		}
		
		return result;
	}
	
	/**
	 * @author Christophe
	 * @param inGraph
	 */
	public void rectitudeBenchmark(SpatialWeightedGraph inGraph) {
		// Start benchmark
		Benchmark.start();
		// Create first snapshot point, called "First loop"
		Benchmark.addSnapshot("temps pour l'evaluation complète");
		evaluate(inGraph);
		// Finishes benchmark
		Benchmark.stop();
		// And print snapshots
		Benchmark.printSnapshots();
	}
	
	/**
	 * @author Christophe
	 * @param inGraph
	 */
	public void rectitudeBenchmarkDetail(SpatialWeightedGraph inGraph) {
		// Start benchmark
		Benchmark.start();
		// Create first snapshot point, called "First loop"
		int nbNodes = graph.getNodeCount();
		Benchmark.addSnapshot("WarshallFloyd");
		double[][] distanceShortestPathMatrix = WarshallFloyd();
		
		Benchmark.addIteration();
		Benchmark.addSnapshot("calcul des moyennes");
		double[] moyenne = new double[nbNodes];
		for (int k = 0 ; k < nbNodes ; k++) {
			for (int j = 0 ; j < nbNodes ; j++) {
				if (k != j) {
					moyenne[k] += StraightnessNodes(k, j, distanceShortestPathMatrix[k][j]);
				}
			}
			moyenne[k] /= nbNodes -1;			
		}
		
		Benchmark.addIteration();
		Benchmark.addSnapshot("mettre les resultats dans CentralityResult");
		CentralityResult result = new CentralityResult();
		for (int i = 0 ; i < moyenne.length ; i++) {
			result.add(moyenne[i]);
		}
		// Finishes benchmark
		Benchmark.stop();
		// And print snapshots
		Benchmark.printSnapshots();
	}
	
	/**
	 * @author Christophe
	 * @param sourceFile
	 */
	public void rectitudeBenchmarkFromFile(String sourceFile) {
		
		SpatialWeightedGraph graphe = new SpatialWeightedGraph();
		
		GraphMLReader reader = new GraphMLReader();
		reader.updateFromFile(sourceFile, graphe);
		
		for (int i = 0 ; i < 3 ; i++) {
			// Start benchmark
			Benchmark.start();
			// Create first snapshot point, called "First loop"
			Benchmark.addSnapshot("temps pour l'evaluation complète");
			evaluate(graphe);
			// Finishes benchmark
			Benchmark.stop();
			// And print snapshots
			Benchmark.printSnapshots();
			
			Benchmark.saveToFile("tests"+i+sourceFile);
		}		
	}
}