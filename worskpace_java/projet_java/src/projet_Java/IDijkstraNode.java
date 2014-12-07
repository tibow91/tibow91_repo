package projet_Java;

import java.util.ArrayList;

import algo.graph.interfaces.IEdge; 
import algo.graph.interfaces.INode;

/** Interface à implémenter afin qu'il puisse être appliqué à un noeud l'algorithme de Dijkstra **/
public interface IDijkstraNode<K,V> extends IComparable<IDijkstraNode<K,V>>,INode<K,V>  {
	
	public ArrayList<IEdge> getEdges(); // Obtention des "Edges" d'un noeud
    public void addEdge(IEdge edge); // Ajout d'un edge au noeud
    public int getMinDistance(); // Obtention de la distance minimale depuis le noeud de départ
    public void setMinDistance(int minDistance); // Mise à jour de la distance minimale entre ce noeud et le noeud de départ
    public IDijkstraNode<K,V> getPrevious(); // Obtention du noeud précédent celui-ci dans le chemin parcouru
    public void setPrevious(IDijkstraNode<K,V> prev); // Mise à jour du noeud dont dérive celui-ci danse le chemin parcouru
}
