package projet_Java;

import java.util.HashMap;

import algo.graph.interfaces.IEdge;
import algo.graph.interfaces.INode;

public class GenericEdge<K,V> implements IEdge
{
	private INode<K, V> ville1;
	private INode<K, V> ville2;
	private HashMap<String, Object> attributs;

	public GenericEdge(INode<K, V> val1, INode<K, V> val2, Object val3)
	{
		ville1 = val1;
		ville2 = val2;
		attributs = new HashMap<String, Object>();
		attributs.put("cost", val3);

//		ville1.getEdges().add(this);
		val1.getEdges().add(this);
	}

	public Object getAttribute(String val)
	{
		return attributs.get(val);
	}

	public void setAttribute(String val1, Object val2)
	{
		attributs.put(val1, val2);
	}

	@Override
	public INode getOther(INode val) {
		// TODO Auto-generated method stub
		if (ville1 == val)
		{
			return ville2;
		}
		return ville1;
	}
}
