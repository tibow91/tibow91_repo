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
	private Texture[] textureArray;

	public void start()
	{
		initGL(1600, 800);

		init();

		while (true)
		{
			GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);
			render();

			Display.update();
			Display.sync(100);

			if (Display.isCloseRequested())
			{
				Display.destroy();
				System.exit(0);
			}
		}
	}

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

	public void init()
	{
		String chaine = "";
		String fichier = "map.txt";

		// lecture du fichier texte
		try
		{
			InputStream ips = new FileInputStream(fichier);
			InputStreamReader ipsr = new InputStreamReader(ips);
			BufferedReader br = new BufferedReader(ipsr);
			String ligne;

			while ((ligne = br.readLine()) != null)
			{
				// System.out.println(ligne);
				chaine += ligne + "\n";
			}
			br.close();
		} catch (Exception e)
		{
			System.out.println(e.toString());
		}

		textureArray = new Texture[chaine.length()];
		
		try
		{
			// Parcourt chaine
			for (int i = 0; i < chaine.length(); i++)
			{
				switch (chaine.charAt(i))
				{
					case '*' :
						textureArray[i] = TextureLoader.getTexture("PNG", ResourceLoader.getResourceAsStream("map/rock.png"));
						break;
					case ' ' :
						textureArray[i] = TextureLoader.getTexture("PNG", ResourceLoader.getResourceAsStream("map/dust.png"));
						break;
					case 'A' :
						textureArray[i] = TextureLoader.getTexture("PNG", ResourceLoader.getResourceAsStream("map/door.png"));
						break;
					case 'R' :
						textureArray[i] = TextureLoader.getTexture("PNG", ResourceLoader.getResourceAsStream("map/cheese.png"));
						break;
				}
			}
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	public void render()
	{
		Color.white.bind();

		try
		{
			int xSpace = 0 - textureArray[0].getImageWidth();
			int ySpace = 0;
			
			for (int i=0; i < textureArray.length; i++)
			{
				System.out.println("x = " + xSpace);
				System.out.println("y = " + ySpace);
				
				if(textureArray[i] == null)
				{
					ySpace += textureArray[0].getImageHeight();
					xSpace = 0 - textureArray[0].getImageWidth();
				}
				else
				{
					xSpace += textureArray[i].getImageWidth();
					
					textureArray[i].bind();
		
					GL11.glBegin(GL11.GL_QUADS);
					GL11.glTexCoord2f(0, 0);
					GL11.glVertex2f(xSpace, ySpace);
					GL11.glTexCoord2f(1, 0);
					GL11.glVertex2f(xSpace + textureArray[i].getTextureWidth(), ySpace);
					GL11.glTexCoord2f(1, 1);
					GL11.glVertex2f(xSpace + textureArray[i].getTextureWidth(), textureArray[i].getTextureHeight());
					GL11.glTexCoord2f(0, 1);
					GL11.glVertex2f(xSpace, textureArray[i].getTextureHeight());
					GL11.glEnd();
				}
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