package projet_Java;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
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
	private BufferedReader br = null;
	private FileInputStream ips = null;
	private InputStreamReader ipsr = null;
	
	// Lance la partie
	public void start()
	{
		initGL(1366, 600, "Java Project ESGI"); // Initialise openGL
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
	private void initGL(int width, int height, String windowTitle)
	{
		try
		{
			Display.setDisplayMode(new DisplayMode(width, height));
			Display.create();
			Display.setVSyncEnabled(true);
			Display.setTitle(windowTitle);
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
	
	private BufferedReader openFile(String filename)
	{
		// Chargement du fichier dans un buffer
		try {
			ips = new FileInputStream(filename);
			ipsr = new InputStreamReader(ips);
			br = new BufferedReader(ipsr);
			return br;
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("Cannot find map file " + filename);
			System.exit(0);
		} 
		return null;
	}
	
	private void closeFile(String filename)
	{
		try {
			br.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.exit(0);
		}
		try {
			ipsr.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.exit(0);
		}
		try {
			ips.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.exit(0);
		}
	}
	
	private String readLine()
	{
		String line = null;
		try {
			line = br.readLine();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return line;	
	}
	
	private int countLines(String filename)
	{
		int linenumber=0;
		br = openFile(filename);	// Chargement du fichier dans un buffer

		// Comptage du nombre de lignes
		while(readLine() != null )
			linenumber++;		
		
		closeFile(filename); 	// Déchargement du fichier

		return linenumber;
	}
	
	private boolean checkEmptyFileandCountLines(String filename)
	{
		nblignes = 0;
		nblignes = countLines(filename);
		if(nblignes == 0)
			return true;
		return false;
	}
	
	private void checkMapWidth(int width)
	{
		if(width > 51)				// Vérification de la bonne largeur de la map
		{
			System.out.println("La largeur de la map est trop grande (" + width + ")");
			System.exit(0);
		}
	}
	
	private void checkMapFormat(String line, String filename) // Vérification que la map est bien rectangulaire
	{
		if(longueur != line.length()) 
		{
			System.out.println("Le fichier " + filename + " n'est pas un quadrilatère.");
			System.exit(0);
		}
	}
	
	private void loadFileLines(String filename)
	{
		if(checkEmptyFileandCountLines(filename))
		{
			System.out.println("Fichier " + filename + " vide.");
			System.exit(0);
		}
		
		br = openFile(filename);
		lignes = new String[nblignes]; // Allocation des lignes
		
		// Chargement des lignes du fichier
		for(int i=0;i<nblignes;i++)
		{
			lignes[i] = readLine(); // lecture d'une ligne
			if(i==0)
			{
				longueur = lignes[i].length();
				checkMapWidth(longueur);
			}
			else checkMapFormat(lignes[i],filename);
		}
		closeFile(filename);
		System.out.println("Longueur = " + longueur);
	}
	
	private void loadTextures()
	{
		textureArray = new Texture[nblignes][longueur];		
		
		// Parcourt les lignes du fichier
		// Chargement des textures selon les caractères du fichier map
		boolean error=false;
		for ( int i = 0; i < nblignes; i++)
		{
			for ( int j = 0; j < longueur; j++)
			{				
				switch (lignes[i].charAt(j))
				{
					case '*' :
						textureArray[i][j] = getTexture("PNG","map/rock.png");
						break;
					case ' ' :
						textureArray[i][j] = getTexture("PNG","map/dust.png");
						break;
					case 'A' :
						textureArray[i][j] = getTexture("PNG","map/door.png");
						break;
					case 'R' :
						textureArray[i][j] = getTexture("PNG","map/cheese.png");
						break;
					default:
						System.out.println("Character '" + lignes[i].charAt(j) +
												"' has no associated texture at i= " + i + " and j= " + j);
						error = true;
						break;
				}
			}
		}
		if(error)
			System.exit(0);
	}
	
	public void init(String mapfilename)
	{
		if(checkEmptyFileandCountLines(mapfilename))
		{
			System.out.println("Fichier " + mapfilename + " vide.");
			System.exit(0);
		}

		loadFileLines(mapfilename);
		
		// Allocation des textures 2D
		loadTextures(); 
	}
	
	private Texture getTexture(String filetype, String filename)
	{
		Texture texture = null;
		try{
			texture = TextureLoader.getTexture(filetype, ResourceLoader.getResourceAsStream(filename));			
		}catch (Exception e){
			System.out.println(e.toString());
			System.exit(0);
		}
		return texture;
	}

	// Définition des quadrilatères associés aux textures du tableau de textures
	public void render()
	{
		Color.white.bind();

		try
		{
			int xSpace = 0; // Abscisses
			int ySpace = 0; // Ordonnées
			
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