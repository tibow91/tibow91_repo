package esgi_ConsoleGraphique;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class Fenetre extends JFrame
{
	public static void main(String args[])
	{
		new Fenetre().start();
	}

	public void start()
	{
		String chaine = "";
		String fichier = "map.txt";
		int tailleMapX = 0;
		int tailleMapY = 0;
		int sizeImg = 26;
		int empX = 0-sizeImg;
		int empY = 0;
		ImageImplement sprite = null;

		// lecture du fichier texte
		try
		{
			InputStream ips = new FileInputStream(fichier);
			InputStreamReader ipsr = new InputStreamReader(ips);
			BufferedReader br = new BufferedReader(ipsr);
			String ligne;
			
			while ((ligne = br.readLine()) != null)
			{
				System.out.println(ligne);
				chaine += ligne + "\n";
				tailleMapX = ligne.length();
				tailleMapY++;
			}
			br.close();
		}
		catch (Exception e)
		{
			System.out.println(e.toString());
		}
		
		System.out.println(chaine);
		
		// Parcourt chaine
		for (int i=0; i<chaine.length(); i++)
		{
			empX += sizeImg;
			if(chaine.charAt(i) == '\n')
			{
				// Pour les nouvelles lignes
				empX = 0-sizeImg;
				empY += sizeImg;
//				System.out.println("backL");
			}
			else if(chaine.charAt(i) == ' ')
			{
				sprite = new ImageImplement(new ImageIcon("map/dust.png").getImage(), empX, empY);
				this.add(sprite);
//				setVisible(true);
//				System.out.println("space");
			}
			else if(chaine.charAt(i) == 'A')
			{
				sprite = new ImageImplement(new ImageIcon("map/door.png").getImage(), empX, empY);
				this.add(sprite);
//				setVisible(true);
//				System.out.println("A");
			}
			else if(chaine.charAt(i) == '*')
			{
				sprite = new ImageImplement(new ImageIcon("map/rock.png").getImage(), empX, empY);
				this.add(sprite);
//				setVisible(true);
//				System.out.println("wall");
			}
			else
			{
				sprite = new ImageImplement(new ImageIcon("map/mouse.png").getImage(), empX, empY);
				this.add(sprite);
//				setVisible(true);
//				System.out.println("other");
			}
		}

		// Affichage icone test
		ImageImplement spriteTest = new ImageImplement(new ImageIcon("map/mouse.png").getImage(), 52, 26);
		ImageImplement spriteTest2 = new ImageImplement(new ImageIcon("map/cheese.png").getImage(), 78, 26);
		ImageImplement spriteTest3 = new ImageImplement(new ImageIcon("map/door.png").getImage(), 52, 52);
		ImageImplement spriteTest4 = new ImageImplement(new ImageIcon("map/dust.png").getImage(), 78, 52);
		this.add(spriteTest);
		this.add(spriteTest2);
		this.add(spriteTest3);
		this.add(spriteTest4);
		setVisible(true);
		setSize(tailleMapX*sizeImg, tailleMapY*sizeImg);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
	}
}
