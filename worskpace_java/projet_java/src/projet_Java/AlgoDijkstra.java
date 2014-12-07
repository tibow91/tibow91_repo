package projet_Java;

import java.util.ArrayList;

import algo.graph.interfaces.IEdge;

public class AlgoDijkstra {

	// Cherche le meilleur Edge
    public static <K,V> void computePaths(GenericNode source, GenericNode dest)
    {
        source.setMinDistance(0);
		AlgoComparator<IDijkstraNode<K,V>> comp = new AlgoComparator<IDijkstraNode<K,V>>();
        AlgoPriorityQueue<IDijkstraNode<K,V>> Queue = new AlgoPriorityQueue<IDijkstraNode<K,V>>(comp);
      	Queue.add((IDijkstraNode<K,V>)source);
      	boolean firststep = true;
      	
      	while (Queue.peek() != null) 
      	{      		
      		GenericNode u = (GenericNode) Queue.remove();
      		if(u == dest){
      			source.setPrevious(null);
      			return;
      		}
            // Visit each edge exiting u
            for (IEdge e : u.getEdges())
            {
            	GenericNode v = (GenericNode) e.getOther(u);
            	if(v.is_arrival() && dest != v) continue;
            	if(firststep && !v.is_Walkable()) continue;
                int weight = (int) e.getAttribute("cost");  
                int distanceThroughU = u.getMinDistance() + weight;
				if (distanceThroughU < v.getMinDistance()) 
				{
				    Queue.remove((IDijkstraNode<K,V>)v);
				    v.setMinDistance(distanceThroughU);
				    v.setPrevious(u);
				    Queue.add((IDijkstraNode<K,V>)v);
//				    System.out.println("<");
		 		}
//				System.out.println("edge");
            }
            firststep = false;
            
//            System.out.println("IDijkstraNode u = " + u);
        }
		source.setPrevious(null);

    }

    public static  ArrayList<GenericNode> getShortestPathTo(GenericNode target)
    {
    	ArrayList<GenericNode> path = new ArrayList<GenericNode>();
    	for (GenericNode vertex = target; vertex != null; vertex = (GenericNode)vertex.getPrevious()){
    		path.add(vertex);
    	}
    	AlgoSort.reverse(path);
    	path.remove(0);
    	return path;
    }
    
    
    public static <K,V>  ArrayList<GenericNode> route(GenericNode paris,GenericNode marseille){
    	computePaths(paris, marseille);
    	return getShortestPathTo(marseille);
    }
    



}
