package projet_Java;

public class ArrivalPoint {
	Coordinates XY = new Coordinates(0,0);
	int nbMovesToGo = 10;
	public ArrivalPoint(Coordinates coord){
		XY = coord;
	}
	public ArrivalPoint(Coordinates coord, int moves){
		XY = coord;
		nbMovesToGo = moves;
	}
	
	public void setMoves(int newNbMoves){
		nbMovesToGo = newNbMoves;
	}
	
	public void decrease(){
		nbMovesToGo--;
	}
	
	public Coordinates getCoordinates(){
		return XY;
	}
	
	public boolean isFull(){
		if(nbMovesToGo <= 0){
			return true;
		}
		return false;
	}
	
}
