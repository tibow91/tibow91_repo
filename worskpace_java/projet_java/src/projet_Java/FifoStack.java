package projet_Java;

import projet_Java.AlgoLinkedList;
public class FifoStack<V> {
	
	private AlgoLinkedList<V> topObject = null;
	private int size = 0;
	
//	public V pop()throws StackEmptyException{	
	public V pop(){	
		V topValue = null;
		AlgoLinkedList <V> newtopObject = null;
		
		if(!isEmpty())
		{
			topValue = peek();
			if(size() > 1)
				newtopObject = topObject.getNext();
				
			topObject.remove();
			size --;
		}
		else{} // StackEmptyException
		
		topObject = newtopObject;
		return topValue;

	}
	
//	public V peek() throws StackEmptyException {
	public V peek() {
		V val = null;
		if(!isEmpty())
			val = topObject.getValue();
		else {} // StackEmptyException
		return val;		
	}
	
	public void push(V value)
	{				
		if(isEmpty()){
			topObject = new AlgoLinkedList<V>(value);
		}
		else{
			topObject.setNewEndLinkedList(value);
		}		
		size++;			
	}
	
	public int size()
	{
		return size;
	}
	
	public boolean isEmpty()
	{
		if(size() == 0)
			return true;
		return false;
	}
	
	void clear()
	{
		while(!isEmpty())
			pop();
	}
	
//	public FifoStack (){
//		Objects = new AlgoLinkedList<V>
//	}

}
