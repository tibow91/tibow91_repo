package projet_Java;

import java.awt.Font;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.newdawn.slick.Color;
import org.newdawn.slick.TrueTypeFont;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;
import org.newdawn.slick.util.ResourceLoader;

import algo.graph.interfaces.IEdge;
import projet_Java.GenericNode;

import java.util.ArrayList;
public class Partie
{
	private String lignes[]; // Charge les lignes du fichier map.txt
	private int nblignes = 0, longueur = 0; // Délimite le nombre de pixels de
											// la map (lignes/colonnes)

	// Variables de gestion de fichier
	private BufferedReader br = null;
	private FileInputStream ips = null;
	private InputStreamReader ipsr = null;


	private GenericNode mapNodes[][]; // Noeuds de la carte
	private int departureXY[][] = null; // Point(s) d'apparition
	private static FifoStack<LapMouse> ActiveMouses = new FifoStack<LapMouse>(); 
	private ArrayList<ArrivalPoint> arrivalArray = new ArrayList<ArrivalPoint>();
	private FifoStack<Integer> doorMoves = new FifoStack<Integer>();

	private boolean startGame=false; // Indique si la partie est lancée
	private long lapTime = 350;
	long startTime = System.currentTimeMillis();
	private int nbActiveMouses = 0; // Nombre de souris en déplacement
	private int nbMove = 0;
	private int nbLap = 0;
	private int arrivedMouses = 0;
	private int MousesToGo = 0;
	
	/** Textures par défault **/
	static private Texture defaultTexture = null; // Texture par défault de chaque parcelle de terrain
	private Texture mouseTexture = null; // Texture d'une souris par défault	
	/* Textures de souris selon le mouvement qu'elles effectuent */
	private Texture fromLeftMouseTexture = null;
	private Texture fromRightMouseTexture = null;
	private Texture fromUpMouseTexture = null;
	private Texture fromDownMouseTexture = null;
	private Texture fromUpLeftMouseTexture = null;
	private Texture fromUpRightMouseTexture = null;
	private Texture fromDownLeftMouseTexture = null;
	private Texture fromDownRightMouseTexture = null;

	private TrueTypeFont font;
	private Button lButton = new Button();
	private Button sButton = new Button();
	private Button decLapTime = new Button();
	private Button incLapTime = new Button();
	
	static public int RESWIDTH = 600;
	static public int RESHEIGHT = 1366;
	
	public void start() // Lance la partie
	{
		initGL(RESHEIGHT, RESWIDTH, "Java Project ESGI"); // Initialise openGL
		init("map.txt"); // Initialise la map

		while (true)
		{
			GL11.glClear(GL11.GL_COLOR_BUFFER_BIT); // (Ré)Initialisation du
													// buffer graphique
			pollInput();
			render(); // Mise à jour des textures
			UpdateMouses(); // Mise à jour des souris
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

	public void init(String mapfilename)
	{
		if (checkEmptyFileandCountLines(mapfilename))
		{
			System.out.println("Fichier " + mapfilename + " vide.");
			System.exit(0);
		}

		loadFileLines(mapfilename);
		checkMapSurroundings('*'); // Vérifie si la map est bien fermée
		setDefaultTexture("PNG", "map/dust.png");

		initAndLinkMapNodes();
		setMapNodesAndSetTextures();
		setEdgesAndGraph();
		getDeparturePoints(); // Récupération des points d'apparition pour que
								// les sabelettes puissent rentrer

		//		System.out.println("path "+ mapNodes[22][7] + " to " + mapNodes[x][y] + " = " + AlgoDijkstra.route(mapNodes[22][7], mapNodes[x][y]));
		setMouseTexture("PNG", "map/mouse.png");
		calculateArrivalPointsNumber();
		setFromLeftMouseTexture("map/m_right.png");
		setFromRightMouseTexture("map/m_left.png");
		setFromUpMouseTexture("map/m_front.png");
		setFromDownMouseTexture("map/m_back.png");
		setFromDownLeftMouseTexture("map/m_rightTop.png");
		setFromDownRightMouseTexture("map/m_leftTop.png");
		setFromUpRightMouseTexture("map/m_leftBottom.png");
		setFromUpLeftMouseTexture("map/m_rightBottom.png");
		
		Font awtFont = new Font("Times New Roman", Font.BOLD, 24);
		font = new TrueTypeFont(awtFont, true);
		
		int x = longueur * getDefaultTexture().getImageWidth();
		int y = nblignes * getDefaultTexture().getImageHeight();
		lButton.set(x/2, y+60, getDefaultTexture().getImageHeight(), 100);
		lButton.setText("LANCER");
		lButton.setBackGround(true);
		
		sButton.set(x/2, y+60, getDefaultTexture().getImageHeight(), 100);
		sButton.setText("STOP");
		sButton.setBackGround(true);
		
		decLapTime.set(474,y+26, getDefaultTexture().getImageHeight(), 26);
		decLapTime.setText("-");
		
		incLapTime.set(750,y+26, getDefaultTexture().getImageHeight(), 26);
		incLapTime.setText("+");
	}
	
	/****************************************************************/
	/************* GESTION DE LECTURE D'UN FICHIER ******************/
	// Vérifie la viabilité du fichier et compte le nombre de lignes
	private boolean checkEmptyFileandCountLines(String filename)
	{
		nblignes = 0;
		nblignes = countLines(filename);
		if (nblignes == 0)
			return true;
		return false;
	}

	// Compte le nombre de lignes du fichier
	private int countLines(String filename)
	{
		int linenumber = 0;
		br = openFile(filename); // Chargement du fichier dans un buffer

		// Comptage du nombre de lignes
		while (readLine() != null)
			linenumber++;

		closeFile(filename); // Déchargement du fichier

		return linenumber;
	}

	private BufferedReader openFile(String filename)
	{
		// Chargement du fichier dans un buffer
		try
		{
			ips = new FileInputStream(filename);
			ipsr = new InputStreamReader(ips);
			br = new BufferedReader(ipsr);
			return br;
		} catch (FileNotFoundException e)
		{
			e.printStackTrace();
			System.out.println("Cannot find map file " + filename);
			System.exit(0);
		}
		return null;
	}

	// Lecture d'une ligne du fichier ouvert
	private String readLine()
	{
		String line = null;
		try
		{
			line = br.readLine();
		} catch (IOException e)
		{
			e.printStackTrace();
			System.exit(0);
		}
		return line;
	}

	// Fermeture du fichier ouvert
	private void closeFile(String filename)
	{
		try
		{
			br.close();
		} catch (IOException e)
		{
			e.printStackTrace();
			System.exit(0);
		}
		try
		{
			ipsr.close();
		} catch (IOException e)
		{
			e.printStackTrace();
			System.exit(0);
		}
		try
		{
			ips.close();
		} catch (IOException e)
		{
			e.printStackTrace();
			System.exit(0);
		}
	}

	// Charge les lignes du fichier dans le tableau de String lignes
	private void loadFileLines(String filename)
	{
		if (checkEmptyFileandCountLines(filename))
		{
			System.out.println("Fichier " + filename + " vide.");
			System.exit(0);
		}

		br = openFile(filename);
		lignes = new String[nblignes]; // Allocation des lignes

		// Chargement des lignes du fichier
		for (int i = 0; i < nblignes; i++)
		{
			lignes[i] = readLine(); // lecture d'une ligne
			if (i == 0)
			{
				longueur = lignes[i].length();
				checkMapLineWidth(longueur);
			} else
				checkMapFormat(lignes[i], filename);
		}
		closeFile(filename);
		System.out.println("Longueur = " + longueur);
	}

	/****************************************************************/
	/********** VERIFICATION DE LA CARTE (DIMENSIONS) ****************/
	// Vérifier si la ligne lue est de longueur correcte
	private void checkMapLineWidth(int width) // faire un setMapLineWidth
	{
		if (width > 51) // Vérification de la bonne largeur de la map
		{
			System.out.println("La largeur de la map est trop grande (" + width + ")");
			System.exit(0);
		}
	}

	// Vérification que la map est bien rectangulaire
	private void checkMapFormat(String line, String filename)
	{
		if (longueur != line.length())
		{
			System.out.println("Le fichier " + filename + " n'est pas un quadrilatère.");
			System.exit(0);
		}
	}

	// Vérifie que la carte est bien fermée
	private void checkMapSurroundings(char rockchar)
	{
		if (nblignes <= 0)
		{
			System.out.println("Cannot checkMapSurroundings if file is empty");
			System.exit(0);
		}

		for (int i = 0; i < nblignes; i++)
		{
			if (lignes[i] == null)
			{
				System.out.println("Cannot checkMapSurroundings if lines of the file have not been loaded correctly");
				System.exit(0);
			}
			if (i == 0 || i == (nblignes - 1))
			{
				for (int j = 0; j < lignes[i].length(); j++)
				{
					if (lignes[i].charAt(j) != rockchar)
					{
						System.out.println("The map is not closed");
						System.exit(0);
					}
				}
			} else
			{
				if (lignes[i].charAt(0) != rockchar || lignes[i].charAt(lignes[i].length() - 1) != rockchar)
				{
					System.out.println("The map is not closed");
					System.exit(0);
				}
			}

		}
	}

	// Fixe la texture par défaut
	private void setDefaultTexture(String filetype, String filename)
	{
		defaultTexture = getTexture(filetype, filename);
	}

	/****************************************************************/
	/********** CHARGEMENT ET PARAMETRAGE DES NOEUDS ****************/
	// Initialise les noeuds de la carte et effectue la liaison entre eux
	private void initAndLinkMapNodes()
	{
		initMapNodes();
		for (int i = 0; i < nblignes; i++)
		{
			for (int j = 0; j < longueur; j++)
			{
				if (mapNodes[i][j] == null)
				{
					System.out.println("mapNode has not been set at coordonates i= " + i + " j= " + j);
					System.exit(0);
				}
				if (j > 0)
					mapNodes[i][j].setLeftNode(mapNodes[i][j - 1]);
				if (i > 0)
					mapNodes[i][j].setUpNode(mapNodes[i - 1][j]);
				if ((i < (nblignes - 1)) && (j < (longueur - 1)))
					mapNodes[i][j].setDownRightNode(mapNodes[i + 1][j + 1]);
				if (j > 0 && i < (nblignes - 1))
					mapNodes[i][j].setDownLeftNode(mapNodes[i + 1][j - 1]);
			}
		}
		
	}

	// Initialise les noeuds de la carte si les dimensions de la carte ont bien
	// été chargées
	private void initMapNodes()
	{
		if (nblignes <= 0 || longueur <= 0)
		{
			System.out.println("Cannot initialize Map Nodes. Please set the map format correctly");
			System.exit(0);
		}
		mapNodes = new GenericNode[nblignes][longueur];
		for (int i = 0; i < nblignes; i++)
		{
			for (int j = 0; j < longueur; j++)
			{
				String key = "" + i + "-" + j;
				mapNodes[i][j] = new GenericNode(key,new Coordinates(i,j));
			}
		}
	}

	// Créée les Edges et le graph
	private void setEdgesAndGraph()
	{

		GenericEdge<String,Object> edge = null;
		int weight = 0;
		Graph<String,Object> graph = new Graph<String, Object>();
		for (int i = 0; i < nblignes; i++)
		{
			for (int j = 0; j < longueur; j++)
			{
				if (mapNodes[i][j].is_grass() == true)
				{
					weight = 2;
				} else
				{
					weight = 1; 
				}
				if (mapNodes[i][j].hasDownLeftNode() && mapNodes[i][j].getDownLeftNode().is_Walkable())
					edge = new GenericEdge<String,Object>(mapNodes[i][j], mapNodes[i][j].getDownLeftNode(), weight);
				
				if (mapNodes[i][j].hasDownNode() && mapNodes[i][j].getDownNode().is_Walkable())
					edge = new GenericEdge<String,Object>(mapNodes[i][j], mapNodes[i][j].getDownNode(), weight);
				
				if (mapNodes[i][j].hasDownRightNode() && mapNodes[i][j].getDownRightNode().is_Walkable())
					edge = new GenericEdge<String,Object>(mapNodes[i][j], mapNodes[i][j].getDownRightNode(), weight);
				
				if (mapNodes[i][j].hasLeftNode() && mapNodes[i][j].getLeftNode().is_Walkable())
					edge = new GenericEdge<String,Object>(mapNodes[i][j], mapNodes[i][j].getLeftNode(), weight);
				
				if (mapNodes[i][j].hasRightNode() && mapNodes[i][j].getRightNode().is_Walkable())
					edge = new GenericEdge<String,Object>(mapNodes[i][j], mapNodes[i][j].getRightNode(), weight);
				
				if (mapNodes[i][j].hasUpLeftNode() && mapNodes[i][j].getUpLeftNode().is_Walkable())
					edge = new GenericEdge<String,Object>(mapNodes[i][j], mapNodes[i][j].getUpLeftNode(), weight);
				
				if (mapNodes[i][j].hasUpNode() && mapNodes[i][j].getUpNode().is_Walkable())
					edge = new GenericEdge<String,Object>(mapNodes[i][j], mapNodes[i][j].getUpNode(), weight);
				
				if (mapNodes[i][j].hasUpRightNode() && mapNodes[i][j].getUpRightNode().is_Walkable())
					edge = new GenericEdge<String,Object>(mapNodes[i][j], mapNodes[i][j].getUpRightNode(), weight);

				graph.registerNode(mapNodes[i][j]);

				//System.out.println(mapNodes[i][j].getName() + " # " + mapNodes[i][j].getEdges().size());
//				System.out.println(graph.getNodes().size());
			}
		}
	}

	// Paramètres les noeuds de la map textures comprises
	private void setMapNodesAndSetTextures()
	{
		// Parcourt les lignes du fichier
		// Chargement des textures selon les caractères du fichier map
		boolean error = false;
		for (int i = 0; i < nblignes; i++)
		{
			for (int j = 0; j < longueur; j++)
			{
				switch (lignes[i].charAt(j))
				{
					case '*': // MUR
						mapNodes[i][j].setTexture(getMapTexture("PNG", "map/rock.png"));
						mapNodes[i][j].setAsWall();
						break;
					case ' ': // Zone normale de déplacement
						mapNodes[i][j].setTexture(getMapTexture("PNG", "map/dust.png"));
						break;
					case 'D': // Point d'apparition des personnages
						mapNodes[i][j].setTexture(getMapTexture("PNG", "map/door.png"));
						mapNodes[i][j].setAsDeparture();
						break;
					case 'A': // Point d'arrivée des personnages
						mapNodes[i][j].setTexture(getMapTexture("PNG", "map/cheese.png"));
						mapNodes[i][j].setAsArrival();
						break;
					case 'G': // Point d'arrivée des personnages
						mapNodes[i][j].setTexture(getMapTexture("PNG", "map/grass.png"));
						mapNodes[i][j].setAsGrass();
						break;
					default:
						System.out.println("Character '" + lignes[i].charAt(j) + "' has no associated texture at i= " + i + " and j= " + j);
						error = true;
						break;
				}
			}
		}
		if (error)
			System.exit(0);
	}

	/****************************************************************/
	/****************** GESTION DES TEXTURES ************************/
	// Récupère une texture à partir du fichier indiqué s'il correspond bien aux
	// dimensions des textures par défaut
	private Texture getMapTexture(String filetype, String filename)
	{
		Texture texture;
		texture = getTexture(filetype, filename);
		checkMapTextureFormat(texture, filename);
		return texture;
	}

	// Vérifie si la texture correspond bien aux dimensions de la texture par
	// défault
	private void checkMapTextureFormat(Texture texture, String texturefilename)
	{
		if (texture.getImageHeight() != getDefaultTexture().getImageHeight())
		{
			System.out.println("The image texture " + texturefilename + " height don't match with default texture height");
			System.exit(0);
		} else if (texture.getImageWidth() != getDefaultTexture().getImageWidth())
		{
			System.out.println("The image texture " + texturefilename + " width don't match with default texture width");
			System.exit(0);
		}
	}

	// Charge une texture à partir du fichier indiqué
	private Texture getTexture(String filetype, String filename)
	{
		Texture texture = null;
		try
		{
			texture = TextureLoader.getTexture(filetype, ResourceLoader.getResourceAsStream(filename));
		} catch (Exception e)
		{
			System.out.println(e.toString());
			System.exit(0);
		}
		return texture;
	}

	/****************************************************************/
	/********** CHARGEMENT DES POINTS D'APPARITION ******************/
	// Charge les coordonnées des points d'apparition
	private void getDeparturePoints()
	{
		int nb = 0;
		nb = getDeparturePointsNumber();
		System.out.println(nb + " departure points");
		if (nb == 0)
		{
			System.out.println("Il n'y a pas de points d'apparition sur la map");
			System.exit(0);
		}
		departureXY = new int[nb][2];

		for (int i = 0, x = 0; i < nblignes; i++)
		{
			for (int j = 0; j < longueur; j++)
			{
				if (mapNodes[i][j].is_departure() && x < nb)
				{
					departureXY[x][0] = i;
					departureXY[x][1] = j;
					x++;
				} else if (x >= nb)
					return;
			}
		}
	}

	// Comptage des points d'apparition
	private int getDeparturePointsNumber()
	{
		int nb = 0;
		for (int i = 0; i < nblignes; i++)
			for (int j = 0; j < longueur; j++)
				if (mapNodes[i][j].is_departure())
					nb++;
		return nb;
	}
	
	private void calculateArrivalPointsNumber()
	{
		int nb = 0;
		for (int i = 0; i < nblignes; i++){
			for (int j = 0; j < longueur; j++){
				if (mapNodes[i][j].is_arrival())
				{
					ArrivalPoint ar = new ArrivalPoint(new Coordinates(i,j));
					if(!doorMoves.isEmpty()){
						ar.nbMovesToGo = doorMoves.pop();
					}
					nb++;
					arrivalArray.add(ar);
				}
			}
		}
		for(int i=0; i<nb; i++){
			MousesToGo += arrivalArray.get(i).nbMovesToGo;
		}
		if(nb == 0){
			System.out.println("there is not Arrival Points available !!!");
			System.exit(0);
		}
	}
	

	// Paramètre la texture représentant la souris
	private void setMouseTexture(String filetype, String filename) {
		this.mouseTexture = getTexture(filetype, filename);
	}

	// Détection des périphériques d'entrée (clavier/souris)
	public void pollInput()
	{

		if (Mouse.isButtonDown(0))
		{
			int x = Mouse.getX();
			int y = Mouse.getY();
			Coordinates coord = new Coordinates(x, y);
//			System.out.println("MOUSE DOWN @ X: " + x + " Y: " + y);
			if( !startGame && lButton.isClicked(coord))
				startGame = true;
			else if(sButton.isClicked(coord))
				startGame = false;
			if(decLapTime.isClicked(coord))
				lapTime --;
			if(incLapTime.isClicked(coord))
				lapTime ++;			
			
		}

		if (Keyboard.isKeyDown(Keyboard.KEY_SPACE))
		{
			System.out.println("SPACE KEY IS DOWN");
		}

		while (Keyboard.next())
		{
			if (Keyboard.getEventKeyState())
			{
				if (Keyboard.getEventKey() == Keyboard.KEY_DOWN)
				{
				}
				if (Keyboard.getEventKey() == Keyboard.KEY_UP)
				{
					System.out.println("A Key Pressed");
				}
				if (Keyboard.getEventKey() == Keyboard.KEY_LEFT)
				{
					System.out.println("A Key Pressed");
				}
				if (Keyboard.getEventKey() == Keyboard.KEY_RIGHT)
				{
					System.out.println("A Key Pressed");
				}

			} else
			{
				if (Keyboard.getEventKey() == Keyboard.KEY_DOWN)
				{
					System.out.println("A Key Released");
				}
				if (Keyboard.getEventKey() == Keyboard.KEY_UP)
				{
					System.out.println("A Key Released");
				}
				if (Keyboard.getEventKey() == Keyboard.KEY_LEFT)
				{
					System.out.println("A Key Released");
				}
				if (Keyboard.getEventKey() == Keyboard.KEY_RIGHT)
				{
					System.out.println("A Key Released");
				}
			}
		}
	}
	
	private int move(GenericNode startNode, GenericNode endNode){
		if(!startNode.is_Occupied() && !startNode.is_departure()){
			System.out.println("the start Node " + startNode + " is not occupied");
		}
		else if(endNode.is_Occupied())
			System.out.println("the end Node is occupied");
		else if(endNode.is_arrival())
		{
			startNode.setAsNotOccupied();
			System.out.println("The mouse on "+ startNode + "has arrived !");
			// Enlever la souris de la liste des souris actives
			// Décrémenter le nombre de souris
			return 1;
		}
		else if(!endNode.is_Walkable())
			System.out.println("the end node is not walkable for a mouse");
		else
		{
			endNode.setAsOccupied();
			startNode.setAsNotOccupied();
			if(startNode == endNode.getDownLeftNode())
				endNode.setFromDownLeft(true);
			else if(startNode == endNode.getDownRightNode())
				endNode.setFromDownRight(true);
			else if(startNode == endNode.getUpLeftNode())
				endNode.setFromUpLeft(true);
			else if(startNode == endNode.getUpRightNode())
				endNode.setFromUpRight(true);
			else if(startNode == endNode.getDownNode())
				endNode.setFromDown(true);
			else if(startNode == endNode.getUpNode())
				endNode.setFromUp(true);
			else if(startNode == endNode.getLeftNode())
				endNode.setFromLeft(true);
			else if(startNode == endNode.getRightNode())
				endNode.setFromRight(true);
			return 0;
		}
		return -1;
	}
	
	private int moveUp(GenericNode node){
		return move(node,node.getUpNode());
	}
	
	private int moveDown(GenericNode node){
		return move(node,node.getDownNode());
	}
	private int moveLeft(GenericNode node){
		return move(node,node.getLeftNode());
	}
	private int moveRight(GenericNode node){
		return move(node,node.getRightNode());
	}
	
	private int moveUpRight(GenericNode node){
		return move(node,node.getUpRightNode());
	}
	
	private int moveDownRight(GenericNode node){
		return move(node,node.getDownRightNode());
	}
	private int moveUpLeft(GenericNode node){
		return move(node,node.getUpLeftNode());
	}
	private int moveDownLeft(GenericNode node){
		return move(node,node.getDownLeftNode());
	}
	

	private void cleanRoutesAndResetDistance(){
		for(int i=0; i< nblignes; i++){
			for(int j=0; j< longueur; j++){
				mapNodes[i][j].setPrevious(null);
				mapNodes[i][j].setMinDistance(Integer.MAX_VALUE);
			}
		}
	}

	/****************************************************************/
	/****************** AFFICHAGE GRAPHIQUE *************************/
	// Mise à jour graphique de la carte
	public void render()
	{
		Color.white.bind();
		try
		{
			int xSpace = 0; // Abscisses
			int ySpace = 0; // Ordonnées

			// Définition des quadrilatères associés aux textures du tableau de
			// textures
			for (int i = 0; i < nblignes; i++)
			{
				for (int j = 0; j < longueur; j++)
				{
					displayTextureQuad(getDefaultTexture(), xSpace, ySpace);
					displayTextureQuad(mapNodes[i][j].getTexture(), xSpace, ySpace);
					if(mapNodes[i][j].is_Occupied()){
						if(mapNodes[i][j].isFromDown())
							displayTextureQuad(getFromDownMouseTexture(), xSpace,ySpace);
						else if(mapNodes[i][j].isFromUp())
							displayTextureQuad(getFromUpMouseTexture(), xSpace,ySpace);
						else if(mapNodes[i][j].isFromRight())
							displayTextureQuad(getFromRightMouseTexture(), xSpace,ySpace);
						else if(mapNodes[i][j].isFromLeft())
							displayTextureQuad(getFromLeftMouseTexture(), xSpace,ySpace);
						else if(mapNodes[i][j].isFromUpLeft())
							displayTextureQuad(getFromUpLeftMouseTexture(), xSpace,ySpace);
						else if(mapNodes[i][j].isFromUpRight())
							displayTextureQuad(getFromUpRightMouseTexture(), xSpace,ySpace);
						else if(mapNodes[i][j].isFromDownRight())
							displayTextureQuad(getFromDownRightMouseTexture(), xSpace,ySpace);
						else if(mapNodes[i][j].isFromDownLeft())
							displayTextureQuad(getFromDownLeftMouseTexture(), xSpace,ySpace);
						else
							displayTextureQuad(getMouseTexture(), xSpace,ySpace);
					}

					// Incrémentation de l'abscisse pour chaque colonne
					xSpace += getDefaultTexture().getImageWidth();
				}

				// Incrémentation des ordonnées à la fin de chaque ligne
				ySpace += getDefaultTexture().getImageHeight();
				// Réinitialisation des abscisses à la fin de chaque ligne
				xSpace = 0;
			}
			
			
	
//			displayQuad(xSpace, ySpace, getDefaultTexture().getImageWidth(),1);

			
			for (int i = 0; i < 1; i++)
			{
				for (int j = 0; j < longueur; j++)
				{
					// displayTextureQuad(getDefaultTexture(),xSpace, ySpace);
					displayQuad(xSpace, ySpace, getDefaultTexture().getImageWidth(),0);
					xSpace += getDefaultTexture().getImageWidth();
				}
				// Incrémentation des ordonnées à la fin de chaque ligne
//				ySpace += getDefaultTexture().getImageHeight();
				// Réinitialisation des abscisses à la fin de chaque ligne
				xSpace = 0;
			}
			
			int x = longueur * getDefaultTexture().getImageWidth();
			int y = nblignes * getDefaultTexture().getImageHeight();
			if(!startGame){
				lButton.draw();
				decLapTime.draw();
				incLapTime.draw();
			}
			else{
				sButton.draw();
			}
			
//			font.drawString(0, ySpace, "THE LIGHTWEIGHT JAVA GAMES LIBRARY", Color.yellow);
			font.drawString(0, ySpace, "Nombre de déplacements = " + nbMove, Color.yellow);
			font.drawString(500, ySpace, "Nombre de Souris en déplacement = " + nbActiveMouses, Color.yellow);

			ySpace += 26;
			font.drawString(0, ySpace, "Nombre de Tours = " + nbLap, Color.yellow);
			font.drawString(500, ySpace, "Temps d'un tour = " + lapTime, Color.yellow);
			ySpace += 26;
			font.drawString(0, ySpace, "Nombre de Souris arrivées = " + arrivedMouses, Color.yellow);
			ySpace += 26;
			font.drawString(0, ySpace, "Nombre de Souris devant arriver = " + MousesToGo, Color.yellow);
			ySpace += 26;
			

		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	// Affichage d'un bloc de texture aux coordonnées indiquées
	private void displayTextureQuad(Texture texture, int x, int y)
	{
		texture.bind(); // or GL11.glBind(texture.getTextureID());

		GL11.glBegin(GL11.GL_QUADS);
		GL11.glTexCoord2f(0, 0);
		GL11.glVertex2f(x, y);
		GL11.glTexCoord2f(1, 0);
		GL11.glVertex2f(x + texture.getTextureWidth(), y);
		GL11.glTexCoord2f(1, 1);
		GL11.glVertex2f(x + texture.getTextureWidth(), y + texture.getTextureHeight());
		GL11.glTexCoord2f(0, 1);
		GL11.glVertex2f(x, y + texture.getTextureHeight());
		GL11.glEnd();

		GL11.glDisable(GL11.GL_TEXTURE_2D);
		// set the color of the quad (R,G,B,A)
		GL11.glColor3f(255, 150, 10);

		// draw quad
//		GL11.glBegin(GL11.GL_QUADS);
//		GL11.glVertex2f(0, 100);
//		GL11.glVertex2f(300, 100);
//		GL11.glVertex2f(300, 300);
//		GL11.glVertex2f(100, 0);
//		GL11.glEnd();

		GL11.glFlush();
	}
	
	public Texture getMouseTexture() {
		if(mouseTexture == null)
		{
			System.out.println("Mouse Texture has not been set");
			System.exit(0);
		}
		return mouseTexture;
	}
	// Obtention de la texture par défaut
	static public Texture getDefaultTexture()
	{
		if (defaultTexture == null)
		{
			System.out.println("Default texture has not been set");
			System.exit(0);
		}
		return defaultTexture;
	}
	
	private long getLapTime(){
		return lapTime;
	}
	
	private void setLapTime(long time){
		lapTime = time;
	}
	
	private void UpdateMouses(){
		long estimatedTime = System.currentTimeMillis() - startTime;

		if(startGame && estimatedTime > getLapTime())
		{
			// Incrémenter le nombre de tour
			nbLap++;
			
			// traiter toutes les souris déjà présentes sur le terrain
			if(nbActiveMouses > 0){
				moveActiveMouses();
			}
			
			// Ajout de nouvelles souris si disponible
			if(!arrivalArray.isEmpty())
				SearchAndSetNewMouses();
			
			if(nbActiveMouses == 0)
				System.out.println("arrivalArray size = " + arrivalArray.size());
			// Fin de la Partie si la liste arrivalArray est vide et s'il n'y a plus d'activeMouses (souris en déplacement)
			if(arrivalArray.isEmpty() && nbActiveMouses<=0 ){
				System.out.println("Fin de la partie");
				System.out.println("Nombre de deplacements = " + nbMove);
				System.out.println("Nombre de tours = " + nbLap);
				System.out.println("Nombre de souris arrivées = " + arrivedMouses);
				System.out.println("Nombre de souris devant arriver = " + MousesToGo);
				System.out.println("Nombre de souris en déplacement = " + nbActiveMouses);	
				startGame = false;
			}			
			startTime = System.currentTimeMillis();
		}
	}
	
	private void printActiveMouses(){
		System.out.println("ActiveMouses :");
		for(int i=0; i<ActiveMouses.size(); i++){
			LapMouse l = ActiveMouses.pop();
			System.out.println(l.XY);
			ActiveMouses.push(l);						
		}
	}
	
	private void moveActiveMouses(){

		int size = ActiveMouses.size();
		ArrayList<LapMouse> tab = new ArrayList<LapMouse>();
		for(int i=0; i< size ; i++){
			tab.add(ActiveMouses.pop());
		}
		for(int i=0; i< size ; i++){
			// Décrémenter nombre de tour pour toutes les souris
			tab.get(i).decreaseLap();
			// si tour = 0 alors 
			if(tab.get(i).Lap == 0){
				Coordinates exit = new Coordinates(0,0);

				// exécuter l'algorithme pour toutes les destinations
				if(algorithm(tab.get(i).XY,exit)<0){
					tab.get(i).setLap(1);
					ActiveMouses.push(tab.get(i));
					System.out.println("None path has been chosen ");
					continue;
				}
				
				// tenter un déplacement vers la sortie la plus rapide
				if(moveLapMouse(tab.get(i),exit) > 0)
					ActiveMouses.push(tab.get(i));
			}
			else{
			// sinon ajouter la souris à ActiveMouses
				ActiveMouses.push(tab.get(i));
			}
			
		}
		tab.clear();
		
		
	}
	private int algorithm( Coordinates startpoint, Coordinates blocktogo){
		int xa = startpoint.x;
		int ya = startpoint.y;
		if(arrivalArray.isEmpty()) return -1;
		
		boolean foundOne = false;
		Coordinates dest = new Coordinates(0,0);
		int size = arrivalArray.size();
		int minDistance = Integer.MAX_VALUE;
		for(int i=0; i< size; i++){
			
			ArrivalPoint ap = arrivalArray.get(i);
			int xb= ap.XY.x;
			int yb= ap.XY.y;
//			System.out.println("Lancement de l'algorithme pour " + startpoint + " vers pt d'arrivée " + ap.getCoordinates());;
			ArrayList<GenericNode> path =  AlgoDijkstra.route(mapNodes[xa][ya],mapNodes[xb][yb] );
//			System.out.println(mapNodes[xa][ya] +" path = " + path);
			
			if(path.isEmpty()){
				cleanRoutesAndResetDistance();
				continue;
			}
			GenericNode nodetogo = (GenericNode) path.get(0);
			int newDistance = path.get(path.size()-1).getMinDistance();
			path.clear();
			cleanRoutesAndResetDistance();

			if(newDistance < minDistance){
				foundOne = true;
				minDistance = newDistance;
				dest.setCoordinates(nodetogo.getCoordinates());
			}			
		}
		
		if(!foundOne) return -1;
		blocktogo.setCoordinates(dest);

		// 
		return 0;
	}
	
	private void seekAndDecreaseArrivalPoint(Coordinates coord){
		if(arrivalArray.isEmpty()){
			System.out.println("Impossible de décrémenter Arrival Point " + coord + " car le tableau d'arrivée est vide");
			return;
		}
		boolean found = false;
		for(ArrivalPoint ar : arrivalArray){
			if(ar.getCoordinates().equals(coord)){
				ar.decrease();
				System.out.println("Arrival Point " + ar.getCoordinates() + " has been decreased");
				found = true;
				//	Si le point d'arrivée est nul retirer le point d'arrivée de la liste
				if(ar.isFull()){
					if(arrivalArray.remove(ar) == false){
						System.out.println("Cannot retrieve ArrivalPoint");
						System.exit(0);
					}
					System.out.println("Point d'arrivée " + ar.getCoordinates() + " retiré de la liste");
				}
				break;						
			}
		}
		if(!found)
			System.out.println("Le point d'arrivée " + coord + " n'est pas ou plus répertorié");
	}
	
	
	private int moveLapMouse(LapMouse l, Coordinates exit){
		
		int res = move(mapNodes[l.XY.x][l.XY.y],mapNodes[exit.x][exit.y]);
		
		// si le déplacement réussit
		if(res >=0){
			// Incrémenter le nombre de déplacements
			nbMove++;
			// Mettre à jour les coordonnées de la souris
			l.setCoordinates(exit);
		}
		
		switch(res)
		{
			case 1: 					
				// si c'est un point d'arrivée incrémenter le nombre de souris arrivées
				arrivedMouses++;
				//	Décrémenter le nombre de souris pouvant accéder à ce point d'arrivée
				seekAndDecreaseArrivalPoint(exit);
				// Décrémenter le nombre de souris en déplacement
				nbActiveMouses--;
				System.out.println("Nombre de souris en déplacement = " + nbActiveMouses);

				return 0;
			case 0:
				// si la case d'arrivée est de l'herbe
				if(mapNodes[exit.x][exit.y].is_grass()){
					// ajouter la souris à ActiveMouses avec tour = 2n
					l.setLap(2);
				}
				else{
					// sinon ajouter la souris à ActiveMouses avec tour = 1
					l.setLap(1);
				}						
				break;
			case -1:
				l.setLap(1);
			default:
				break;
		}
		return 1;
	}
	
	// Recherche des nouvelles souris disponibles aux points d'apparition
	private void SearchAndSetNewMouses() {
		if (departureXY == null) {
			System.out.println("Tableau de points d'apparition non assigné");
			System.exit(0);
		}
		
		Coordinates coord = new Coordinates(0,0);
		int x,y;
		for (int i = 0; i < departureXY.length; i++) {
			coord.x = x = departureXY[i][0];
			coord.y = y = departureXY[i][1];
			AddStartingMouses(mapNodes[x][y]);
		}						
	}

	// Affichage des nouvelles souris près du point d'apparition 
	private void AddStartingMouses(GenericNode entrynode) {
		if(MousesToGo <= 0) return;
        for (IEdge e : entrynode.getEdges()){
    		TestNodeAndAddStartingMouse(entrynode, (GenericNode) e.getOther(entrynode));
        }
	}
	
	private void TestNodeAndAddStartingMouse(GenericNode entrynode, GenericNode nodeToGo){
		if(MousesToGo > 0 && TestWalkableNode(nodeToGo)){
			// Si le déplacement réussit
			if(move(entrynode,nodeToGo) == 0){
				// Ajouter les souris dans ActiveMouses 
				addNewActiveMouses(nodeToGo);
				
				// Incrémenter le nombre de souris en déplacement
				nbActiveMouses++;
				System.out.println("Nombre de souris en déplacement = " + nbActiveMouses);
				
				// Décrémenter le nombre de souris pouvant apparaître (MousesToGo)
				MousesToGo--;
			}
		}
	}
	
	// Ajout d'une nouvelle souris sur le terrain et paramètre le nombre de tours selon le type du terrai
	private void addNewActiveMouses(GenericNode nodeToGo){
		LapMouse lm = new LapMouse(nodeToGo.getCoordinates());
		if(nodeToGo.is_grass())
			lm.setLap(2); // 2 tours
		else
			lm.setLap(1); // 1 tour
		ActiveMouses.push(lm);
	}
	
	// Vérifie l'existence du noeud et indique si son occupation est possible
	private boolean TestWalkableNode(GenericNode node) {
		if (node != null) {
			if (node.is_Walkable())
				return true;
//				node.setTexture(getMapTexture(filetype, filename));
		}
		return false;
	}
	
	
	// Affichage d'un quadrilatère de couleur unie aux coordonées indiquées
	private void displayQuad(int x, int y, int width, int color)
	{
		// set the color of the quad (R,G,B,A)
//		 GL11.glColor3f(0.5f,0.5f,1.0f);
		if(color == 0)
			GL11.glColor3f(0f, 0f, 0f);
		else
			GL11.glColor3f(0.95f, 0.8f,0.8f);
		
		// draw quad
		GL11.glBegin(GL11.GL_QUADS);
		GL11.glTexCoord2f(0, 0);
		GL11.glVertex2f(x, y);
		GL11.glTexCoord2f(1, 0);
		GL11.glVertex2f(x + width, y);
		GL11.glTexCoord2f(1, 1);
		GL11.glVertex2f(x + width, y + width);
		GL11.glTexCoord2f(0, 1);
		GL11.glVertex2f(x, y + width);
		GL11.glEnd();
	}
	
	static public void displayQuad(int x, int y, int height, int width, int color)
	{
		// set the color of the quad (R,G,B,A)
//		 GL11.glColor3f(0.5f,0.5f,1.0f);
		if(color == 0)
			GL11.glColor3f(0f, 0f, 0f);
		else if(color == 1)
			GL11.glColor3f(0.95f, 0.8f,0.8f);
		else
			GL11.glColor3f(1f, 1f,0f);
		
		// draw quad
		GL11.glBegin(GL11.GL_QUADS);
		GL11.glTexCoord2f(0, 0);
		GL11.glVertex2f(x, y);
		GL11.glTexCoord2f(1, 0);
		GL11.glVertex2f(x + width, y);
		GL11.glTexCoord2f(1, 1);
		GL11.glVertex2f(x + width, y + height);
		GL11.glTexCoord2f(0, 1);
		GL11.glVertex2f(x, y + height);
		GL11.glEnd();
	}
	
	public static void main(String[] argv)
	{
		Partie partie = new Partie();
		partie.start();
	}

	/** Setters pour les textures de souris avec direction **/
	public Texture getFromLeftMouseTexture() {
		return fromLeftMouseTexture;
	}

	public void setFromLeftMouseTexture(String filename) {
		this.fromLeftMouseTexture = getTexture("PNG", filename);

	}

	public Texture getFromRightMouseTexture() {
		return fromRightMouseTexture;
	}

	public void setFromRightMouseTexture(String filename) {
		this.fromRightMouseTexture = getTexture("PNG", filename);
	}

	public Texture getFromUpMouseTexture() {
		return fromUpMouseTexture;
	}

	public void setFromUpMouseTexture(String filename) {
		this.fromUpMouseTexture = getTexture("PNG", filename);
	}

	public Texture getFromDownMouseTexture() {
		return fromDownMouseTexture;
	}

	public void setFromDownMouseTexture(String filename) {
		this.fromDownMouseTexture = getTexture("PNG", filename);
	}

	public Texture getFromUpLeftMouseTexture() {
		return fromUpLeftMouseTexture;
	}

	public void setFromUpLeftMouseTexture(String filename) {
		this.fromUpLeftMouseTexture = getTexture("PNG", filename);
	}

	public Texture getFromUpRightMouseTexture() {
		return fromUpRightMouseTexture;
	}

	public void setFromUpRightMouseTexture(String filename) {
		this.fromUpRightMouseTexture = getTexture("PNG", filename);
	}

	public Texture getFromDownLeftMouseTexture() {
		return fromDownLeftMouseTexture;
	}

	public void setFromDownLeftMouseTexture(String filename) {
		this.fromDownLeftMouseTexture = getTexture("PNG", filename);
	}

	public Texture getFromDownRightMouseTexture() {
		return fromDownRightMouseTexture;
	}

	public void setFromDownRightMouseTexture(String filename) {
		this.fromDownRightMouseTexture = getTexture("PNG", filename);
	}
}