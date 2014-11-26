package projet_Java;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.sound.midi.SysexMessage;

import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.newdawn.slick.Color;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;
import org.newdawn.slick.util.ResourceLoader;

import projet_Java.Node;

class Coordinates{
	public int x=0;
	public int y=0;
	public Coordinates(int x_, int y_)
	{
		x= x_;
		y= y_;
	}
}

class Deplacement{
	public Coordinates coord = null;
	public int time = 0;
	public Deplacement (Coordinates xy, int duration){
		coord = new Coordinates(xy.x, xy.y);
		time = duration;
	}
}

public class Partie {
	private String lignes[]; // Charge les lignes du fichier map.txt
	private int nblignes = 0, longueur = 0; // Délimite le nombre de pixels de
											// la map (lignes/colonnes)

	// Variables de gestion de fichier
	private BufferedReader br = null;
	private FileInputStream ips = null;
	private InputStreamReader ipsr = null;

	private Texture defaultTexture = null; // Texture par défaut (texture de sol
											// par exemple)
	private Texture mouseTexture = null;
	private Node<Integer> mapNodes[][]; // Noeuds de la carte
	private int departureXY[][] = null; // Point(s) d'apparition
	private FifoStack<Coordinates> StartingMousesToAdd = new FifoStack<Coordinates>(); 
	private FifoStack<Coordinates> ActiveMouses = new FifoStack<Coordinates>(); 
	long startTime = System.currentTimeMillis();
	Coordinates mouseTest = null;


	public void start() // Lance la partie
	{
		initGL(1366, 600, "Java Project ESGI"); // Initialise openGL
		init("map.txt"); // Initialise la map

		while (true) {
			GL11.glClear(GL11.GL_COLOR_BUFFER_BIT); // (Ré)Initialisation du
													// buffer graphique
			pollInput();
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
	private void initGL(int width, int height, String windowTitle) {
		try {
			Display.setDisplayMode(new DisplayMode(width, height));
			Display.create();
			Display.setVSyncEnabled(true);
			Display.setTitle(windowTitle);
		} catch (LWJGLException e) {
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

	public void init(String mapfilename) {
		if (checkEmptyFileandCountLines(mapfilename)) {
			System.out.println("Fichier " + mapfilename + " vide.");
			System.exit(0);
		}

		loadFileLines(mapfilename);
		checkMapSurroundings('*'); // Vérifie si la map est bien fermée
		setDefaultTexture("PNG", "map/dust.png");

		initAndLinkMapNodes();
		setMapNodesAndSetTextures();
		getDeparturePoints(); // Récupération des points d'apparition pour que
								// les sabelettes puissent rentrer
		setMouseTexture("PNG", "map/mouse.png");
	}

	/****************************************************************/
	/************* GESTION DE LECTURE D'UN FICHIER ******************/
	// Vérifie la viabilité du fichier et compte le nombre de lignes
	private boolean checkEmptyFileandCountLines(String filename) {
		nblignes = 0;
		nblignes = countLines(filename);
		if (nblignes == 0)
			return true;
		return false;
	}

	// Compte le nombre de lignes du fichier
	private int countLines(String filename) {
		int linenumber = 0;
		br = openFile(filename); // Chargement du fichier dans un buffer

		// Comptage du nombre de lignes
		while (readLine() != null)
			linenumber++;

		closeFile(filename); // Déchargement du fichier

		return linenumber;
	}

	private BufferedReader openFile(String filename) {
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

	// Lecture d'une ligne du fichier ouvert
	private String readLine() {
		String line = null;
		try {
			line = br.readLine();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.exit(0);
		}
		return line;
	}

	// Fermeture du fichier ouvert
	private void closeFile(String filename) {
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

	// Charge les lignes du fichier dans le tableau de String lignes
	private void loadFileLines(String filename) {
		if (checkEmptyFileandCountLines(filename)) {
			System.out.println("Fichier " + filename + " vide.");
			System.exit(0);
		}

		br = openFile(filename);
		lignes = new String[nblignes]; // Allocation des lignes

		// Chargement des lignes du fichier
		for (int i = 0; i < nblignes; i++) {
			lignes[i] = readLine(); // lecture d'une ligne
			if (i == 0) {
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
			System.out.println("La largeur de la map est trop grande (" + width
					+ ")");
			System.exit(0);
		}
	}

	// Vérification que la map est bien rectangulaire
	private void checkMapFormat(String line, String filename) {
		if (longueur != line.length()) {
			System.out.println("Le fichier " + filename
					+ " n'est pas un quadrilatère.");
			System.exit(0);
		}
	}

	// Vérifie que la carte est bien fermée
	private void checkMapSurroundings(char rockchar) {
		if (nblignes <= 0) {
			System.out.println("Cannot checkMapSurroundings if file is empty");
			System.exit(0);
		}

		for (int i = 0; i < nblignes; i++) {
			if (lignes[i] == null) {
				System.out
						.println("Cannot checkMapSurroundings if lines of the file have not been loaded correctly");
				System.exit(0);
			}
			if (i == 0 || i == (nblignes - 1)) {
				for (int j = 0; j < lignes[i].length(); j++) {
					if (lignes[i].charAt(j) != rockchar) {
						System.out.println("The map is not closed");
						System.exit(0);
					}
				}
			} else {
				if (lignes[i].charAt(0) != rockchar
						|| lignes[i].charAt(lignes[i].length() - 1) != rockchar) {
					System.out.println("The map is not closed");
					System.exit(0);
				}
			}

		}
	}

	// Fixe la texture par défaut
	private void setDefaultTexture(String filetype, String filename) {
		defaultTexture = getTexture(filetype, filename);
	}

	/****************************************************************/
	/********** CHARGEMENT ET PARAMETRAGE DES NOEUDS ****************/
	// Initialise les noeuds de la carte et effectue la liaison entre eux
	private void initAndLinkMapNodes() {
		initMapNodes();
		for (int i = 0; i < nblignes; i++) {
			for (int j = 0; j < longueur; j++) {
				if (mapNodes[i][j] == null) {
					System.out
							.println("mapNode has not been set at coordonates i= "
									+ i + " j= " + j);
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
	private void initMapNodes() {
		if (nblignes <= 0 || longueur <= 0) {
			System.out
					.println("Cannot initialize Map Nodes. Please set the map format correctly");
			System.exit(0);
		}
		mapNodes = new Node[nblignes][longueur];
		for (int i = 0; i < nblignes; i++)
			for (int j = 0; j < longueur; j++)
				mapNodes[i][j] = new Node<Integer>();
	}

	// Paramètres les noeuds de la map textures comprises
	private void setMapNodesAndSetTextures() {
		// Parcourt les lignes du fichier
		// Chargement des textures selon les caractères du fichier map
		boolean error = false;
		for (int i = 0; i < nblignes; i++) {
			for (int j = 0; j < longueur; j++) {
				switch (lignes[i].charAt(j)) {
				case '*': // MUR
					mapNodes[i][j].setTexture(getMapTexture("PNG",
							"map/rock.png"));
					mapNodes[i][j].setValue(999);
					mapNodes[i][j].setAsWall();
					break;
				case ' ': // Zone normale de déplacement
					mapNodes[i][j].setTexture(getMapTexture("PNG",
							"map/dust.png"));
					mapNodes[i][j].setValue(100);
					break;
				case 'D': // Point d'apparition des personnages
					mapNodes[i][j].setTexture(getMapTexture("PNG",
							"map/door.png"));
					mapNodes[i][j].setValue(100);
					mapNodes[i][j].setAsDeparture();
					break;
				case 'A': // Point d'arrivée des personnages
					mapNodes[i][j].setTexture(getMapTexture("PNG",
							"map/cheese.png"));
					mapNodes[i][j].setValue(100);
					mapNodes[i][j].setAsArrival();
					break;
				case 'G': // Point d'arrivée des personnages
					mapNodes[i][j].setTexture(getMapTexture("PNG",
							"map/grass.png"));
					mapNodes[i][j].setValue(200);
					break;
				default:
					System.out.println("Character '" + lignes[i].charAt(j)
							+ "' has no associated texture at i= " + i
							+ " and j= " + j);
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
	// dimensions des
	// textures par défaut
	private Texture getMapTexture(String filetype, String filename) {
		Texture texture;
		texture = getTexture(filetype, filename);
		checkMapTextureFormat(texture, filename);
		return texture;
	}

	// Vérifie si la texture correspond bien aux dimensions de la texture par
	// défault
	private void checkMapTextureFormat(Texture texture, String texturefilename) {
		if (texture.getImageHeight() != getDefaultTexture().getImageHeight()) {
			System.out.println("The image texture " + texturefilename
					+ " height don't match with default texture height");
			System.exit(0);
		} else if (texture.getImageWidth() != getDefaultTexture()
				.getImageWidth()) {
			System.out.println("The image texture " + texturefilename
					+ " width don't match with default texture width");
			System.exit(0);
		}
	}

	// Charge une texture à partir du fichier indiqué
	private Texture getTexture(String filetype, String filename) {
		Texture texture = null;
		try {
			texture = TextureLoader.getTexture(filetype,
					ResourceLoader.getResourceAsStream(filename));
		} catch (Exception e) {
			System.out.println(e.toString());
			System.exit(0);
		}
		return texture;
	}

	/****************************************************************/
	/********** CHARGEMENT DES POINTS D'APPARITION ******************/
	// Charge les coordonnées des points d'apparition
	private void getDeparturePoints() {
		int nb = 0;
		nb = getDeparturePointsNumber();

		if (nb == 0) {
			System.out
					.println("Il n'y a pas de points d'apparition sur la map");
			System.exit(0);
		}
		departureXY = new int[nb][2];

		for (int i = 0, x = 0; i < nblignes; i++) {
			for (int j = 0; j < longueur; j++) {
				if (mapNodes[i][j].is_departure() && x < nb) {
					departureXY[x][0] = i;
					departureXY[x][1] = j;
					x++;
				} else if (x >= nb)
					return;
			}
		}
	}

	// Comptage des points d'apparition
	private int getDeparturePointsNumber() {
		int nb = 0;
		for (int i = 0; i < nblignes; i++)
			for (int j = 0; j < longueur; j++)
				if (mapNodes[i][j].is_departure())
					nb++;
		return nb;
	}
	
	// Paramètre la texture représentant la souris
	public void setMouseTexture(String filetype, String filename) {
		this.mouseTexture = getTexture(filetype, filename);
	}

	// Détection des périphériques d'entrée (clavier/souris)
	public void pollInput() {

		if (Mouse.isButtonDown(0)) {
			int x = Mouse.getX();
			int y = Mouse.getY();

			System.out.println("MOUSE DOWN @ X: " + x + " Y: " + y);
		}

		if (Keyboard.isKeyDown(Keyboard.KEY_SPACE)) {
			System.out.println("SPACE KEY IS DOWN");
		}

		while (Keyboard.next()) {
			if (Keyboard.getEventKeyState()) {
				if (Keyboard.getEventKey() == Keyboard.KEY_DOWN) {
					if(moveDown(mapNodes[mouseTest.x][mouseTest.y]) == 1)	
						mouseTest.x ++;
				}
				if (Keyboard.getEventKey() == Keyboard.KEY_UP) {
					if(moveUp(mapNodes[mouseTest.x][mouseTest.y])== 1)
						mouseTest.x --;
				}
				if (Keyboard.getEventKey() == Keyboard.KEY_LEFT) {
					if(moveLeft(mapNodes[mouseTest.x][mouseTest.y])== 1)
						mouseTest.y --;
				}
				if (Keyboard.getEventKey() == Keyboard.KEY_RIGHT) {
					if(moveRight(mapNodes[mouseTest.x][mouseTest.y])== 1)
						mouseTest.y ++;
				}

			} else {
				if (Keyboard.getEventKey() == Keyboard.KEY_DOWN) {
				}
				if (Keyboard.getEventKey() == Keyboard.KEY_UP) {
				}
				if (Keyboard.getEventKey() == Keyboard.KEY_LEFT) {
				}
				if (Keyboard.getEventKey() == Keyboard.KEY_RIGHT) {
				}
			}
		}
	}
	
	private int move(Node<?> startNode, Node<?> endNode){
		if(!startNode.is_Occupied())
			System.out.println("the start Node is not occupied");
		else if(endNode.is_Occupied())
			System.out.println("the end Node is occupied");
		else if(endNode.is_arrival())
		{
			startNode.setAsNotOccupied();
			// Enlever la souris de la liste des souris actives
			// Décrémenter le nombre de souris
			return 2;
		}
		else if(!endNode.is_Walkable())
			System.out.println("the end node is not walkable for a mouse");
		else
		{
			endNode.setAsOccupied();
			startNode.setAsNotOccupied();
			return 1;
		}
		return 0;
	}
	
	private int moveUp(Node<?> node){
		return move(node,node.getUpNode());
	}
	
	private int moveDown(Node<?> node){
		return move(node,node.getDownNode());
	}
	private int moveLeft(Node<?> node){
		return move(node,node.getLeftNode());
	}
	private int moveRight(Node<?> node){
		return move(node,node.getRightNode());
	}
	
	private int moveUpRight(Node<?> node){
		return move(node,node.getUpRightNode());
	}
	
	private int moveDownRight(Node<?> node){
		return move(node,node.getDownRightNode());
	}
	private int moveUpLeft(Node<?> node){
		return move(node,node.getUpLeftNode());
	}
	private int moveDownLeft(Node<?> node){
		return move(node,node.getDownLeftNode());
	}
	
	

	/****************************************************************/
	/****************** AFFICHAGE GRAPHIQUE *************************/
	// Mise à jour graphique de la carte
	public void render() {
		Color.white.bind();
		long estimatedTime = System.currentTimeMillis() - startTime;

		try {
			int xSpace = 0; // Abscisses
			int ySpace = 0; // Ordonnées

			// Définition des quadrilatères associés aux textures du tableau de
			// textures
			for (int i = 0; i < nblignes; i++) {
				for (int j = 0; j < longueur; j++) {
					displayTextureQuad(getDefaultTexture(), xSpace, ySpace);
					displayTextureQuad(mapNodes[i][j].getTexture(), xSpace,
							ySpace);
					if(mapNodes[i][j].is_Occupied())
						displayTextureQuad(getMouseTexture(), xSpace,
								ySpace);

					// Incrémentation de l'abscisse pour chaque colonne
					xSpace += getDefaultTexture().getImageWidth();
				}

				// Incrémentation des ordonnées à la fin de chaque ligne
				ySpace += getDefaultTexture().getImageHeight();
				// Réinitialisation des abscisses à la fin de chaque ligne
				xSpace = 0;
			}
			
			// ... do something ...
			if(estimatedTime > 2000)
			{
				UpdateActiveMouses();
				SearchAndSetNewMouses();
				startTime = System.currentTimeMillis();
			}

			for (int i = 0; i < 1; i++) {
				for (int j = 0; j < longueur; j++) {
					// displayTextureQuad(getDefaultTexture(),xSpace, ySpace);
					displayQuad(xSpace, ySpace, getDefaultTexture()
							.getImageWidth());
					xSpace += getDefaultTexture().getImageWidth();
				}
				// Incrémentation des ordonnées à la fin de chaque ligne
				ySpace += getDefaultTexture().getImageHeight();
				// Réinitialisation des abscisses à la fin de chaque ligne
				xSpace = 0;
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// Affichage d'un bloc de texture aux coordonnées indiquées
	private void displayTextureQuad(Texture texture, int x, int y) {
		texture.bind(); // or GL11.glBind(texture.getTextureID());

		GL11.glBegin(GL11.GL_QUADS);
		GL11.glTexCoord2f(0, 0);
		GL11.glVertex2f(x, y);
		GL11.glTexCoord2f(1, 0);
		GL11.glVertex2f(x + texture.getTextureWidth(), y);
		GL11.glTexCoord2f(1, 1);
		GL11.glVertex2f(x + texture.getTextureWidth(),
				y + texture.getTextureHeight());
		GL11.glTexCoord2f(0, 1);
		GL11.glVertex2f(x, y + texture.getTextureHeight());
		GL11.glEnd();
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
	private Texture getDefaultTexture() {
		if (defaultTexture == null) {
			System.out.println("Default texture has not been set");
			System.exit(0);
		}
		return defaultTexture;
	}
	
	// met à jour la position des souris
	private void UpdateActiveMouses() {
//		FifoStack<Deplacement> UpdatedMouse = new FifoStack<Deplacement>();
//		while(!ActiveMouses.isEmpty())
//		{
//			algorithm(ActiveMouses.pop(),UpdatedMouse);
//		}
//		
//		while(!UpdatedMouse.isEmpty())
//		{
//			ActiveMouses.push(UpdatedMouse.pop());
//		}
	}
	

	private void algorithm( Coordinates coord, FifoStack<Deplacement> pil){
		// Etablir la nouvelle position vers laquelle déplacer la souris
		// Tenter un déplacement de la souris avec move
		// Si le déplacement a réussi ajouter les nouvelles coordonées à pil
		// Sinon si la souris a atteint le point d'arrivée ne rien faire
		// sinon si la souris ne peut pas se déplacer réajouter l'ancienne position à la pile pil
		
		// 
	}
	
	// Affichage des sabelettes aux positions où ils se trouvent
	private void SearchAndSetNewMouses() {
		if (departureXY == null) {
			System.out.println("Tableau de points d'apparition non assigné");
			System.exit(0);
		}
		int x, y;
		Coordinates coord = new Coordinates(0,0);
		for (int i = 0; i < departureXY.length; i++) {
			coord.x = x = departureXY[i][0];
			coord.y = y = departureXY[i][1];
			TestAndAddStartingMouses(coord,mapNodes[x][y]);
		}
		
		if(!StartingMousesToAdd.isEmpty())
		{
			while(!StartingMousesToAdd.isEmpty()) {
				coord = StartingMousesToAdd.pop();
				mapNodes[coord.x][coord.y].setAsOccupied();
//				System.out.println("mapNodes[" + coord.x + "][" + coord.y + "] is now occupied");
				ActiveMouses.push(new Coordinates(coord.x, coord.y));
			}
			if(mouseTest == null)
				if(!ActiveMouses.isEmpty())
					mouseTest = new Coordinates(ActiveMouses.peek().x, ActiveMouses.peek().y);
		}
				
	}

	// Affichage des sabelettes au point d'apparition
	private void TestAndAddStartingMouses(Coordinates entrycoord, Node<?> entrynode) {
		if(TestWalkableNode(entrynode.getLeftNode()))
			addStartingMouse(entrycoord.x, entrycoord.y-1);
		if(TestWalkableNode(entrynode.getRightNode()))
			addStartingMouse(entrycoord.x, entrycoord.y+1);
		if(TestWalkableNode(entrynode.getUpNode()))
			addStartingMouse(entrycoord.x-1, entrycoord.y);
		if(TestWalkableNode(entrynode.getDownNode()))
			addStartingMouse(entrycoord.x+1, entrycoord.y);
		if(TestWalkableNode(entrynode.getUpRightNode()))
			addStartingMouse(entrycoord.x-1, entrycoord.y+1);
		if(TestWalkableNode(entrynode.getDownRightNode()))
			addStartingMouse(entrycoord.x+1, entrycoord.y+1);
		if(TestWalkableNode(entrynode.getUpLeftNode()))
			addStartingMouse(entrycoord.x-1, entrycoord.y-1);
		if(TestWalkableNode(entrynode.getDownLeftNode()))
			addStartingMouse(entrycoord.x+1, entrycoord.y-1);
	}
	
	// Vérifie l'existence du noeud et paramètre sa texture se lo
	private boolean TestWalkableNode(Node<?> node) {
		if (node != null) {
			if (node.is_Walkable())
				return true;
//				node.setTexture(getMapTexture(filetype, filename));
		}
		return false;
	}
	
	private void addStartingMouse(int x_, int y_)
	{
		if(StartingMousesToAdd != null)
			StartingMousesToAdd.push(new Coordinates(x_,y_));
		else
		{
			System.out.println("StartingMousesToAdd has not been initialized");
			System.exit(0);
		}
	}

	// Affichage d'un quadrilatère de couleur unie aux coordonées indiquées
	private void displayQuad(int x, int y, int width) {
		// set the color of the quad (R,G,B,A)
		// GL11.glColor3f(0.5f,0.5f,1.0f);
		GL11.glColor3f(0f, 0f, 0f);

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

	public static void main(String[] argv) {
		Partie partie = new Partie();
		partie.start();
	}


}