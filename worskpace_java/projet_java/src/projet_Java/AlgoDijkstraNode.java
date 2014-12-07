package projet_Java;

import java.util.ArrayList;

import algo.graph.interfaces.IEdge;
import algo.graph.interfaces.INode;

public interface AlgoDijkstraNode<K,V> extends IComparable<AlgoDijkstraNode<K,V>>,INode<K,V>  {
	
	public ArrayList<IEdge> getEdges();
    public void addEdge(IEdge edge);
    public int getMinDistance();
    public void setMinDistance(int minDistance);
    public AlgoDijkstraNode<K,V> getPrevious();
    public void setPrevious(AlgoDijkstraNode<K,V> prev);
}
