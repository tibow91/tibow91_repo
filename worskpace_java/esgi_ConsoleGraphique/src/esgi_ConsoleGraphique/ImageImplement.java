package esgi_ConsoleGraphique;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;

import javax.swing.JPanel;

class ImageImplement extends JPanel
{
	private Image img;
	private int xImg = 0;
	private int yImg = 0;

	public ImageImplement(Image img, int xImg, int yImg)
	{
		this.img = img;
		this.xImg = xImg;
		this.yImg = yImg;
		Dimension size = new Dimension(img.getWidth(null), img.getHeight(null));
		setPreferredSize(size);
		setMinimumSize(size);
		setMaximumSize(size);
		setSize(size);
		setLayout(null);
	}

	public void paintComponent(Graphics g)
	{
		g.drawImage(img, this.xImg, this.yImg, null);
	}
}
