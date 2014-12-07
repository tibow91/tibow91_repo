package projet_Java;

import java.util.ArrayList;

import algo.graph.interfaces.IEdge; 
import algo.graph.interfaces.INode;

/** Interface � impl�menter afin qu'il puisse �tre appliqu� � un noeud l'algorithme de Dijkstra **/
public interface IDijkstraNode<K,V> extends IComparable<IDijkstraNode<K,V>>,INode<K,V>  {
	
	public ArrayList<IEdge> getEdges(); // Obtention des "Edges" d'un noeud
    public void addEdge(IEdge edge); // Ajout d'un edge au noeud
    public int getMinDistance(); // Obtention de la distance minimale depuis le noeud de d�part
    public void setMinDistance(int minDistance); // Mise � jour de la distance minimale entre ce noeud et le noeud de d�part
    public IDijkstraNode<K,V> getPrevious(); // Obtention du noeud pr�c�dent celui-ci dans le chemin parcouru
    public void setPrevious(IDijkstraNode<K,V> prev); // Mise � jour du noeud dont d�rive celui-ci danse le chemin parcouru
}
