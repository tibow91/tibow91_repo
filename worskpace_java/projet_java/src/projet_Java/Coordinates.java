package projet_Java;

/** Classe pouvant contenir des coordonnées sur 2 dimensions **/
public class Coordinates{
	public int x=0;
	public int y=0;
	public Coordinates(int x_, int y_)
	{
		x= x_;
		y= y_;
	}
	
	public boolean equals(Coordinates coord){
		if(x == coord.x && y==coord.y)
			return true;
		return false;
	}
	
	public String toString(){
		String s = x +"-" +y;
		return s ;
	}
	
	public void setCoordinates(Coordinates coord){
		if(coord == null) return;
		x=coord.x;
		y=coord.y;
	}
}