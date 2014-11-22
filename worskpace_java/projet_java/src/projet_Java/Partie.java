package projet_Java;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.newdawn.slick.Color;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;
import org.newdawn.slick.util.ResourceLoader;

public class Partie
{
	private String lignes[]; // Charge les lignes du fichier map.txt
	private Texture[][] textureArray; // Tableau de textures à 2 dimensions (lignes/colonnes) 
	private int nblignes=0,longueur=0; // Délimite le nombre de pixels de la map (lignes/colonnes)
	
	// Lance la partie
	public void start()
	{
		initGL(1366, 600); // Initialise openGL
		init("map.txt"); // Initialise la map

		while (true)
		{
			GL11.glClear(GL11.GL_COLOR_BUFFER_BIT); // (Ré)Initialisation du buffer graphique
			render(); // Mise à jour des textures

			Display.update(); // Mise à jour des graphismes
			Display.sync(100); // ??

			if (Display.isCloseRequested()) // Penser à faire un bouton quitter
			{
				Display.destroy();
				System.exit(0);
			}
		}
	}
	
	// Initialise openGL
	private void initGL(int width, int height)
	{
		try
		{
			Display.setDisplayMode(new DisplayMode(width, height));
			Display.create();
			Display.setVSyncEnabled(true);
			Display.setTitle("Java Project ESGI");
		} catch (LWJGLException e)
		{
			e.printStackTrace();
			System.exit(0);
		}

		GL11.glEnable(GL11.GL_TEXTURE_2D);

		GL11.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);

		// enable alpha blending
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

		GL11.glViewport(0, 0, width, height);
		GL11.glMatrixMode(GL11.GL_MODELVIEW);

		GL11.glMatrixMode(GL11.GL_PROJECTION);
		GL11.glLoadIdentity();
		GL11.glOrtho(0, width, height, 0, 1, -1);
		GL11.glMatrixMode(GL11.GL_MODELVIEW);
	}

	public void init(String filename)
	{
		String fichier = filename; 
		int i,j;
		// lecture du fichier texte
		try
		{
			// Chargement du fichier dans un buffer
			InputStream ips = new FileInputStream(fichier); 
			InputStreamReader ipsr = new InputStreamReader(ips);
			BufferedReader br = new BufferedReader(ipsr);
			
			// Comptage du nombre de lignes
			while(br.readLine() != null)
				nblignes++;			
			
			System.out.println("nblignes = " + nblignes);
			
			// Déchargement du fichier
			br.close();
			ipsr.close();
			ips.close();
			
			// Vérifie que le fichier n'est pas vide
			if(nblignes == 0)
			{
				System.out.println("Fichier " + fichier + " vide.");
				System.exit(0);
			}
			
			// Rechargement du fichier
			ips = new FileInputStream(fichier);
			ipsr = new InputStreamReader(ips);		
			br = new BufferedReader(ipsr);
						
			lignes = new String[nblignes]; // Allocation des lignes
			
			// Chargement des lignes du fichier
			for(i=0;i<nblignes;i++)
			{
				lignes[i] = br.readLine(); // lecture d'une ligne
//				System.out.println("lignes = " + lignes[i]);
				if(i==0)
				{
					longueur = lignes[i].length();
					// Vérification de la bonne largeur de la map
					if(longueur > 51)
					{
						System.out.println("La largeur de la map est trop grande");
						System.exit(0);
					}
				}
				else if(longueur != lignes[i].length()) // Vérification que la map est bien rectangulaire
				{
					System.out.println("Le fichier " + fichier + " n'est pas un quadrilatère.");
					System.exit(0);
				}
			}
			System.out.println("Longueur = " + longueur);

			// Fermeture du fichier
			br.close();	
			ipsr.close();
			ips.close();
			
		} catch (Exception e)
		{
			System.out.println(e.toString());
			System.exit(0);

		}
		
		// Allocation des textures 2D
		textureArray = new Texture[nblignes][longueur];

		try
		{
			// Parcourt les lignes du fichier
			// Chargement des textures selon les caractères du fichier map
			for ( i = 0; i < nblignes; i++)
			{
				for ( j = 0; j < longueur; j++)
				{				
					switch (lignes[i].charAt(j))
					{
						case '*' :
							textureArray[i][j] = TextureLoader.getTexture("PNG", ResourceLoader.getResourceAsStream("map/rock.png"));
							break;
						case ' ' :
							textureArray[i][j] = TextureLoader.getTexture("PNG", ResourceLoader.getResourceAsStream("map/dust.png"));
							break;
						case 'A' :
							textureArray[i][j] = TextureLoader.getTexture("PNG", ResourceLoader.getResourceAsStream("map/door.png"));
							break;
						case 'R' :
							textureArray[i][j] = TextureLoader.getTexture("PNG", ResourceLoader.getResourceAsStream("map/cheese.png"));
							break;
						default:textureArray[i][j]=null;
							break;
					}
				}
			}
//			 System.out.println(" = "+ textureArray.length);

		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	// Définition des quadrilatères associés aux textures du tableau de textures
	public void render()
	{
		Color.white.bind();

		try
		{
			int xSpace = 0;
			int ySpace = 0;
			
			for (int i=0; i < nblignes; i++)
			{
				for (int j=0; j < longueur; j++)
				{
					textureArray[i][j].bind(); // or GL11.glBind(texture.getTextureID());
	
					GL11.glBegin(GL11.GL_QUADS);
					GL11.glTexCoord2f(0, 0);
					GL11.glVertex2f(xSpace, ySpace);
					GL11.glTexCoord2f(1, 0);
					GL11.glVertex2f(xSpace + textureArray[i][j].getTextureWidth(), ySpace);
					GL11.glTexCoord2f(1, 1);
					GL11.glVertex2f(xSpace + textureArray[i][j].getTextureWidth(), ySpace + textureArray[i][j].getTextureHeight());
					GL11.glTexCoord2f(0, 1);
					GL11.glVertex2f(xSpace, ySpace + textureArray[i][j].getTextureHeight());
					GL11.glEnd();
					
					// Incrémentation de l'abscisse pour chaque colonne
					xSpace += textureArray[i][j].getImageWidth(); 

				}
				
				// Incrémentation des ordonnées à la fin de chaque ligne
				ySpace += textureArray[i][0].getImageHeight();
				// Réinitialisation des abscisses à la fin de chaque ligne
				xSpace = 0;

			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	public static void main(String[] argv)
	{
		Partie partie = new Partie();
		partie.start();
	}
}