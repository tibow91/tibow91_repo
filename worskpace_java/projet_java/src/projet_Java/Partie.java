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
	private Node mapNodes[][]; // Noeuds de la carte
	private int departureXY[][] = null; // Point(s) d'apparition

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
				mapNodes[i][j] = new Node();
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
				}
				if (Keyboard.getEventKey() == Keyboard.KEY_UP) {
					System.out.println("A Key Pressed");
				}
				if (Keyboard.getEventKey() == Keyboard.KEY_LEFT) {
					System.out.println("A Key Pressed");
				}
				if (Keyboard.getEventKey() == Keyboard.KEY_RIGHT) {
					System.out.println("A Key Pressed");
				}

			} else {
				if (Keyboard.getEventKey() == Keyboard.KEY_DOWN) {
					System.out.println("A Key Released");
				}
				if (Keyboard.getEventKey() == Keyboard.KEY_UP) {
					System.out.println("A Key Released");
				}
				if (Keyboard.getEventKey() == Keyboard.KEY_LEFT) {
					System.out.println("A Key Released");
				}
				if (Keyboard.getEventKey() == Keyboard.KEY_RIGHT) {
					System.out.println("A Key Released");
				}
			}
		}
	}

	/****************************************************************/
	/****************** AFFICHAGE GRAPHIQUE *************************/
	// Mise à jour graphique de la carte
	public void render() {
		Color.white.bind();

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

					// Incrémentation de l'abscisse pour chaque colonne
					xSpace += getDefaultTexture().getImageWidth();
				}

				// Incrémentation des ordonnées à la fin de chaque ligne
				ySpace += getDefaultTexture().getImageHeight();
				// Réinitialisation des abscisses à la fin de chaque ligne
				xSpace = 0;
			}

			displaySabelettes();

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

	// Obtention de la texture par défaut
	private Texture getDefaultTexture() {
		if (defaultTexture == null) {
			System.out.println("Default texture has not been set");
			System.exit(0);
		}
		return defaultTexture;
	}

	// Affichage des sabelettes aux positions où ils se trouvent
	private void displaySabelettes() {
		if (departureXY == null) {
			System.out.println("Tableau de points d'apparition non assigné");
			System.exit(0);
		}
		int x, y;
		for (int i = 0; i < departureXY.length; i++) {
			x = departureXY[i][0];
			y = departureXY[i][1];
			displayStartingSabelettes(mapNodes[x][y], "PNG", "map/mouse.png");
		}
	}

	// Affichage des sabelettes au point d'apparition
	private void displayStartingSabelettes(Node entrynode, String filetype,
			String filename) {
		TestNodeAndSetTexture(entrynode.getRightNode(), filetype, filename);
		TestNodeAndSetTexture(entrynode.getLeftNode(), filetype, filename);
		TestNodeAndSetTexture(entrynode.getUpNode(), filetype, filename);
		TestNodeAndSetTexture(entrynode.getDownNode(), filetype, filename);
		TestNodeAndSetTexture(entrynode.getUpRightNode(), filetype, filename);
		TestNodeAndSetTexture(entrynode.getDownRightNode(), filetype, filename);
		TestNodeAndSetTexture(entrynode.getUpLeftNode(), filetype, filename);
		TestNodeAndSetTexture(entrynode.getDownLeftNode(), filetype, filename);
	}

	// Vérifie l'existence du noeud et paramètre sa texture se lo
	private void TestNodeAndSetTexture(Node node, String filetype,
			String filename) {
		if (node != null) {
			if (node.is_Walkable())
				node.setTexture(getMapTexture(filetype, filename));
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