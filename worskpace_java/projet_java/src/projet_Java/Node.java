package projet_Java;

import org.newdawn.slick.opengl.Texture;

public class Node {

	private boolean wall = false;
	private boolean grass = false;
	private boolean arrival = false;
	private boolean departure = false;
	private boolean occupied = false;
	private Node upNode = null;
	private Node downNode = null;
	private Node leftNode = null;
	private Node rightNode = null;
	private Node upLeftNode = null;
	private Node upRightNode = null;
	private Node downLeftNode = null;
	private Node downRightNode = null;
	private Texture texture = null;
	private int value = 0;

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

	public boolean is_departure() {
		return departure;
	}

	public void setAsDeparture() {
		this.departure = true;
	}

	public boolean is_Occupied() {
		return occupied;
	}

	public Node getUpNode() {
		return upNode;
	}

	public void setUpNode(Node upNode) {
		if (upNode == null)
			return;
		this.upNode = upNode;
		if (upNode.getDownNode() != this)
			upNode.setDownNode(this);
	}

	public Node getDownNode() {
		return downNode;
	}

	public void setDownNode(Node downNode) {
		if (downNode == null)
			return;
		this.downNode = downNode;
		if (downNode.getUpNode() != this)
			downNode.setUpNode(downNode);
	}

	public Node getLeftNode() {
		return leftNode;
	}

	public void setLeftNode(Node leftNode) {
		if (leftNode == null)
			return;
		this.leftNode = leftNode;
		if (leftNode.getRightNode() != this)
			leftNode.setRightNode(this);
	}

	public Node getRightNode() {
		return rightNode;
	}

	public void setRightNode(Node rightNode) {
		if (rightNode == null)
			return;
		this.rightNode = rightNode;
		if (rightNode.getLeftNode() != this)
			rightNode.setLeftNode(this);
	}

	public Node getUpLeftNode() {
		return upLeftNode;
	}

	public void setUpLeftNode(Node upLeftNode) {
		if (upLeftNode == null)
			return;
		this.upLeftNode = upLeftNode;
		if (upLeftNode.getDownRightNode() != this)
			upLeftNode.setDownRightNode(this);
	}

	public Node getUpRightNode() {
		return upRightNode;
	}

	public void setUpRightNode(Node upRightNode) {
		if (upRightNode == null)
			return;
		this.upRightNode = upRightNode;
		if (upRightNode.getDownLeftNode() != this)
			upRightNode.setDownLeftNode(this);
	}

	public Node getDownLeftNode() {
		return downLeftNode;
	}

	public void setDownLeftNode(Node downLeftNode) {
		if (downLeftNode == null)
			return;
		this.downLeftNode = downLeftNode;
		if (downLeftNode.getUpRightNode() != this)
			downLeftNode.setUpRightNode(this);
	}

	public Node getDownRightNode() {
		return downRightNode;
	}

	public void setDownRightNode(Node downRightNode) {
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

	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		this.value = value;
	}

}
