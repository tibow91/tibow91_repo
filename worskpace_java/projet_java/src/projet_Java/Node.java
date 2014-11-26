package projet_Java;

import org.newdawn.slick.opengl.Texture;

public class Node<V> {

	private boolean wall = false;
	private boolean grass = false;
	private boolean arrival = false;
	private boolean departure = false;
	private boolean occupied = false;
	private Node<V> upNode = null;
	private Node<V> downNode = null;
	private Node<V> leftNode = null;
	private Node<V> rightNode = null;
	private Node<V> upLeftNode = null;
	private Node<V> upRightNode = null;
	private Node<V> downLeftNode = null;
	private Node<V> downRightNode = null;
	private Texture texture = null;
	private V value;

	public boolean is_Wall() {
		return wall;
	}

	public void setAsWall() {
		wall = true;
	}

	public boolean is_grass() {
		return grass;
	}

	public boolean is_arrival() {
		return arrival;
	}
	
	public void setAsArrival()
	{
		this.arrival = true;
	}
	public boolean is_departure() {
		return departure;
	}

	public void setAsDeparture() {
		this.departure = true;
	}

	public boolean is_Occupied() {
		return occupied;
	}
	
	public void setAsOccupied(){
		
		if(!is_Occupied())  
			occupied = true;
		else
			System.out.println("This Node is already occupied");
	}
	
	public void setAsNotOccupied(){
		
		if(is_Occupied())  
			occupied = false;
		else
			System.out.println("This Node was not occupied");
	}

	public Node<V> getUpNode() {
		return upNode;
	}

	public void setUpNode(Node<V> upNode) {
		if (upNode == null)
			return;
		this.upNode = upNode;
		if (upNode.getDownNode() != this)
			upNode.setDownNode(this);
	}

	public Node<V> getDownNode() {
		return downNode;
	}

	public void setDownNode(Node<V> downNode) {
		if (downNode == null)
			return;
		this.downNode = downNode;
		if (downNode.getUpNode() != this)
			downNode.setUpNode(downNode);
	}

	public Node<V> getLeftNode() {
		return leftNode;
	}

	public void setLeftNode(Node<V> leftNode) {
		if (leftNode == null)
			return;
		this.leftNode = leftNode;
		if (leftNode.getRightNode() != this)
			leftNode.setRightNode(this);
	}

	public Node<V> getRightNode() {
		return rightNode;
	}

	public void setRightNode(Node<V> rightNode) {
		if (rightNode == null)
			return;
		this.rightNode = rightNode;
		if (rightNode.getLeftNode() != this)
			rightNode.setLeftNode(this);
	}

	public Node<V> getUpLeftNode() {
		return upLeftNode;
	}

	public void setUpLeftNode(Node<V> upLeftNode) {
		if (upLeftNode == null)
			return;
		this.upLeftNode = upLeftNode;
		if (upLeftNode.getDownRightNode() != this)
			upLeftNode.setDownRightNode(this);
	}

	public Node<V> getUpRightNode() {
		return upRightNode;
	}

	public void setUpRightNode(Node<V> upRightNode) {
		if (upRightNode == null)
			return;
		this.upRightNode = upRightNode;
		if (upRightNode.getDownLeftNode() != this)
			upRightNode.setDownLeftNode(this);
	}

	public Node<V> getDownLeftNode() {
		return downLeftNode;
	}

	public void setDownLeftNode(Node<V> downLeftNode) {
		if (downLeftNode == null)
			return;
		this.downLeftNode = downLeftNode;
		if (downLeftNode.getUpRightNode() != this)
			downLeftNode.setUpRightNode(this);
	}

	public Node<V> getDownRightNode() {
		return downRightNode;
	}

	public void setDownRightNode(Node<V> downRightNode) {
		if (downRightNode == null)
			return;
		this.downRightNode = downRightNode;
		if (downRightNode.getUpLeftNode() != this)
			downRightNode.setUpLeftNode(this);
	}

	public Texture getTexture() {
		if (texture == null) {
			System.out.println("None texture has been defined in the Node");
			System.exit(0);
		}
		return texture;
	}

	public void setTexture(Texture texture) {
		if (texture == null)
			return;
		this.texture = texture;
	}

	public boolean is_Walkable() {
		boolean result = true;
		if (is_Wall())
			result = false;
		else if (is_Occupied())
			result = false;
		else if (is_departure())
			result = false;
		else if (is_arrival())
			result = false;
		return result;
	}

	public V getValue() {
		return value;
	}

	public void setValue(V value) {
		this.value = value;
	}

}
