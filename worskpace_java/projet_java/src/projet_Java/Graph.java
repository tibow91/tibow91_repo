package projet_Java;

import java.util.Collection;
import java.util.HashMap;

import algo.graph.interfaces.IEdge;
import algo.graph.interfaces.IGraph;
import algo.graph.interfaces.INode;

public class Graph<K, V> implements IGraph<K, V>
{
	private HashMap<K, INode<K, V>> list;

	public Graph()
	{
		list = new HashMap<K, INode<K, V>>();
	}

	public INode<K, V> getNode(K val)
	{

		return list.get(val);
	}

	public Collection getNodes()
	{
		return list.values();
	}

	public void registerNode(INode<K, V> val)
	{
		list.put(val.getId(), val);
	}

	public void unregisterNode(K val)
	{
		for (IEdge edge : list.get(val).getEdges())
		{

			edge.getOther(list.get(val)).getEdges().remove(edge);

		}

		list.remove(val);
	}
}
