package projet_Java;

import java.awt.Font;

import org.newdawn.slick.Color;
import org.newdawn.slick.TrueTypeFont;

public class Button {
	public Coordinates XY[] = new Coordinates[4];
	private TrueTypeFont font;
	public String text ="";
	private Font awtFont;
	private boolean background = false;
	
	public void set(int x, int y, int height, int width){
		XY[0] = new Coordinates(x, y);
		XY[1] = new Coordinates(x+width, y);
		XY[2] = new Coordinates(x, y+height);
		XY[3] = new Coordinates(x+width, y+height);	
	}
	public Button(){
		set(0,0,0,0);
		awtFont = new Font("Times New Roman", Font.BOLD, 24);
	}
	
	public void setText(String text){
		font = new TrueTypeFont(awtFont, true);
		this.text = text;
	}
	
	public void setBackGround(boolean res){
		background = res;
	}
	
	public void draw(){
		if(background)
			Partie.displayQuad(XY[0].x,XY[0].y, Partie.getDefaultTexture().getImageHeight(), 100,2);
		font.drawString(XY[0].x, XY[0].y, text, Color.red);
	}
	
	public boolean isClicked(Coordinates mouse){
		if(mouse == null) return false;

		if(mouse.x >= XY[0].x && mouse.x <= XY[1].x){
			if(mouse.y <= (Partie.RESWIDTH-XY[0].y) && mouse.y >= (Partie.RESWIDTH-XY[2].y)){
				return true;
			}
		}
		return false;
	}
}
