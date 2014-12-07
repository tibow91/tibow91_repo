package projet_Java;

public class LapMouse {
	public Coordinates XY = new Coordinates(0,0);
	public int Lap = 1;	
	
	public LapMouse(Coordinates coord){
		XY = coord;
	}
	public LapMouse (Coordinates coord, int tour){
		XY = coord;
		Lap = tour;
	}
	
	public LapMouse(LapMouse l){
		this.replaceBy(l);
	}
	
	public void setLap(int tour){
		Lap = tour;
	}
	
	public void decreaseLap(){
		Lap--;
	}
	
	public void setCoordinates(Coordinates coord){
		XY = coord;
	}
	
	public void replaceBy(LapMouse lm){
		XY = lm.XY;
		Lap = lm.Lap;
	}
}
