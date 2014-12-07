package projet_Java;

import java.util.ArrayList;

import org.newdawn.slick.opengl.Texture;

import algo.graph.interfaces.IEdge;

public class GenericNode implements AlgoDijkstraNode<String, Object>
{
	private String key;
	private Object objet;
	private ArrayList<IEdge> list;
	private boolean wall = false;
	private boolean grass = false;
	private boolean arrival = false;
	private boolean departure = false;
	private boolean occupied = false;
	private GenericNode upNode = null;
	private GenericNode downNode = null;
	private GenericNode leftNode = null;
	private GenericNode rightNode = null;
	private GenericNode upLeftNode = null;
	private GenericNode upRightNode = null;
	private GenericNode downLeftNode = null;
	private GenericNode downRightNode = null;
	private Texture texture = null;
	private String name = "";
	private int valueNode = 0;
	private int minDistance = Integer.MAX_VALUE;
	private AlgoDijkstraNode<String,Object> previous = null;
	private Coordinates XY; 
	
	private boolean fromLeft = false;
	private boolean fromRight = false;
	private boolean fromUp = false;
	private boolean fromDown = false;
	private boolean fromUpLeft = false;
	private boolean fromUpRight = false;
	private boolean fromDownLeft = false;
	private boolean fromDownRight = false;
	

	public GenericNode(String val,Coordinates coord)
	{
		key = val;
		list = new ArrayList<IEdge>();
		XY = coord;
	}
	
	public Coordinates getCoordinates(){
		return XY;
	}

	public ArrayList<IEdge> getEdges()
	{
		return list;
	}

	public String getId()
	{
		return key;
	}

	public Object getValue()
	{
		return objet;
	}

	public String toString()
	{
		return  this.key;
	}

	public boolean is_Wall()
	{
		return wall;
	}

	public void setAsWall()
	{
		wall = true;
	}

	public boolean is_grass()
	{
		return grass;
	}
	
	public void setAsGrass()
	{
		grass = true;
	}

	public boolean is_arrival()
	{
		return arrival;
	}
	
	public void setAsArrival(){
		this.arrival = true;
	}
	
	public boolean is_departure()
	{
		return departure;
	}

	public void setAsDeparture()
	{
		this.departure = true;
	}

	public boolean is_Occupied()
	{
		return occupied;
	}
	
	public void setAsOccupied(){
		if(is_Occupied())
			System.out.println("this node " + this + " is already occupied");
		else
			occupied=true;
	}
	public void setAsNotOccupied(){
		if(is_departure()) return;
		if(!is_Occupied())
			System.out.println("this node " + this + " is already not occupied");
		else
			occupied=false;
	}

	public GenericNode getUpNode()
	{
		return upNode;
	}

	public void setUpNode(GenericNode upNode)
	{
		if (upNode == null)
			return;
		this.upNode = upNode;
		if (upNode.getDownNode() != this)
			upNode.setDownNode(this);
	}

	public GenericNode getDownNode()
	{
		return downNode;
	}

	public void setDownNode(GenericNode downNode)
	{
		if (downNode == null)
			return;
		this.downNode = downNode;
		if (downNode.getUpNode() != this)
			downNode.setUpNode(downNode);
	}

	public GenericNode getLeftNode()
	{
		return leftNode;
	}

	public void setLeftNode(GenericNode genericNode)
	{
		if (genericNode == null)
			return;
		this.leftNode = genericNode;
		if (genericNode.getRightNode() != this)
			genericNode.setRightNode(this);
	}

	public GenericNode getRightNode()
	{
		return rightNode;
	}

	public void setRightNode(GenericNode genericNode)
	{
		if (genericNode == null)
			return;
		this.rightNode = genericNode;
		if (genericNode.getLeftNode() != this)
			genericNode.setLeftNode(this);
	}

	public GenericNode getUpLeftNode()
	{
		return upLeftNode;
	}

	public void setUpLeftNode(GenericNode genericNode)
	{
		if (genericNode == null)
			return;
		this.upLeftNode = genericNode;
		if (genericNode.getDownRightNode() != this)
			genericNode.setDownRightNode(this);
	}

	public GenericNode getUpRightNode()
	{
		return upRightNode;
	}

	public void setUpRightNode(GenericNode upRightNode)
	{
		if (upRightNode == null)
			return;
		this.upRightNode = upRightNode;
		if (upRightNode.getDownLeftNode() != this)
			upRightNode.setDownLeftNode(this);
	}

	public GenericNode getDownLeftNode()
	{
		return downLeftNode;
	}

	public void setDownLeftNode(GenericNode genericNode)
	{
		if (genericNode == null)
			return;
		this.downLeftNode = genericNode;
		if (genericNode.getUpRightNode() != this)
			genericNode.setUpRightNode(this);
	}

	public GenericNode getDownRightNode()
	{
		return downRightNode;
	}

	public void setDownRightNode(GenericNode downRightNode)
	{
		if (downRightNode == null)
			return;
		this.downRightNode = downRightNode;
		if (downRightNode.getUpLeftNode() != this)
			downRightNode.setUpLeftNode(this);
	}

	public Texture getTexture()
	{
		if (texture == null)
		{
			System.out.println("None texture has been defined in the Node");
			System.exit(0);
		}
		return texture;
	}

	public void setTexture(Texture texture)
	{
		if (texture == null)
			return;
		this.texture = texture;
	}

	public boolean is_Walkable()
	{
		boolean result = true;
		if (is_Wall())
			result = false;
		else if (is_Occupied())
			result = false;
		else if (is_departure())
			result = false;
		return result;
	}

	public int getValueNode()
	{
		return valueNode;
	}

	public void setValueNode(int value)
	{
		this.valueNode = value;
	}
	
	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}
	
	public boolean hasDownLeftNode()
	{
		boolean result = false;
		if(this.getDownLeftNode() != null)
			result = true;
		return result;
	}	
	public boolean hasDownNode()
	{
		boolean result = false;
		if(this.getDownNode() != null)
			result = true;
		return result;
	}	
	public boolean hasDownRightNode()
	{
		boolean result = false;
		if(this.getDownRightNode() != null)
			result = true;
		return result;
	}	
	public boolean hasLeftNode()
	{
		boolean result = false;
		if(this.getLeftNode() != null)
			result = true;
		return result;
	}	
	public boolean hasRightNode()
	{
		boolean result = false;
		if(this.getRightNode() != null)
			result = true;
		return result;
	}	
	public boolean hasUpLeftNode()
	{
		boolean result = false;
		if(this.getUpLeftNode() != null)
			result = true;
		return result;
	}	
	public boolean hasUpNode()
	{
		boolean result = false;
		if(this.getUpNode() != null)
			result = true;
		return result;
	}	
	public boolean hasUpRightNode()
	{
		boolean result = false;
		if(this.getUpRightNode() != null)
			result = true;
		return result;
	}

	@Override
	public int compareTo(AlgoDijkstraNode<String,Object> o) {
		// TODO Auto-generated method stub
	  	if(o == null)
    	{
    		System.out.println("Le noeud Disjkstrable à comparer est null");
    		System.exit(0);
    	}
		if(getMinDistance() < o.getMinDistance())
			return -1;
		else if(getMinDistance() == o.getMinDistance())
			return 0;
		else return 1;
	}

	@Override
	public void addEdge(IEdge edge) {
		// TODO Auto-generated method stub
		if(getEdges() != null)
			getEdges().add(edge);
	}

	@Override
	public int getMinDistance() {
		// TODO Auto-generated method stub
		return minDistance;
	}

	@Override
	public void setMinDistance(int minDistance) {
		// TODO Auto-generated method stub
		this.minDistance = minDistance;
	}

	@Override
	public AlgoDijkstraNode<String,Object> getPrevious() {
		// TODO Auto-generated method stub
		return previous;
	}

	@Override
	public void setPrevious(AlgoDijkstraNode<String,Object> prev) {
		// TODO Auto-generated method stub
		this.previous =  prev;
	}

	public boolean isFromLeft() {
		return fromLeft;
	}

	public void setFromLeft(boolean fromLeft) {
		resetFromMvt();
		this.fromLeft = fromLeft;
	}

	public boolean isFromRight() {
		return fromRight;
	}

	public void setFromRight(boolean fromRight) {
		resetFromMvt();
		this.fromRight = fromRight;
	}

	public boolean isFromDown() {
		return fromDown;
	}

	public void setFromDown(boolean fromDown) {
		resetFromMvt();
		this.fromDown = fromDown;
	}

	public boolean isFromUpLeft() {
		return fromUpLeft;
	}

	public void setFromUpLeft(boolean fromUpLeft) {
		resetFromMvt();
		this.fromUpLeft = fromUpLeft;
	}

	public boolean isFromUpRight() {
		return fromUpRight;
	}

	public void setFromUpRight(boolean fromUpRight) {
		resetFromMvt();
		this.fromUpRight = fromUpRight;
	}

	public boolean isFromUp() {
		return fromUp;
	}

	public void setFromUp(boolean fromUp) {
		resetFromMvt();
		this.fromUp = fromUp;
	}

	public boolean isFromDownLeft() {
		return fromDownLeft;
	}

	public void setFromDownLeft(boolean fromDownLeft) {
		resetFromMvt();
		this.fromDownLeft = fromDownLeft;
	}

	public boolean isFromDownRight() {
		return fromDownRight;
	}

	public void setFromDownRight(boolean fromDownRight) {
		resetFromMvt();
		this.fromDownRight = fromDownRight;
	}
	
	public void resetFromMvt(){
		this.fromDownRight = false;
		this.fromDownLeft = false;
		this.fromUp = false;
		this.fromUpLeft = false;
		this.fromDown = false;
		this.fromUpRight = false;
		this.fromRight = false;
		this.fromLeft = false;

	
	}
}
