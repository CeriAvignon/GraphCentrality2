package fr.univavignon.graphcentr.g03;

import fr.univavignon.graphcentr.g07.core.graphs.SimpleGraph;
import fr.univavignon.graphcentr.g07.core.utility.Benchmark;
import fr.univavignon.graphcentr.g07.core.centrality.*;

import cern.colt.matrix.DoubleMatrix2D;
import cern.colt.matrix.DoubleMatrix1D;
import cern.colt.matrix.impl.DenseDoubleMatrix2D;
import cern.colt.matrix.linalg.EigenvalueDecomposition;


/** 
 * @author Delfeil
 * @brief Compute EigenVector centrality using the algorithm from the library Colt
 */
class EigenVectorColt implements SimpleCentrality
{
	@Override
	public CentralityResult evaluate(SimpleGraph g)
	{
		CentralityResult r=new CentralityResult();
		double[] eigenV=EigenVectorAlgorithm(g);
		for(int i=0; i<eigenV.length; i++)
		{
			r.add(eigenV[i]);
		}
		return r;
	}
	
	
	/**
	 * 
	 * @param g : the graph
	 * @brief Compute EigenVector centrality using the Colt's algorithm
	 * @return array containing each node's centrality
	 *
	 */
	public double[] EigenVectorAlgorithm(SimpleGraph g)
	{
		double[] values;
		double [][] adjMat=g.toAdjacencyMatrix();
		DenseDoubleMatrix2D adj=new DenseDoubleMatrix2D(adjMat);
		
		EigenvalueDecomposition ed=new EigenvalueDecomposition(adj);
		DoubleMatrix2D eignV2d=ed.getV();
		DoubleMatrix1D eignV=eignV2d.viewColumn(g.getNodeCount()-1);
		values=eignV.toArray();
		return values;
	}
}
