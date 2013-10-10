package tilt.game;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.FloatBuffer;
import java.util.ArrayList;

import org.lwjgl.BufferUtils;
import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.openal.AL;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;
import org.lwjgl.util.glu.Sphere;
import org.newdawn.slick.Color;
import org.newdawn.slick.util.ResourceLoader;

import tilt.camera.Camera;
import tilt.camera.View;
import tilt.game.entities.Ball;
import tilt.game.entities.EntityHandler;
import tilt.game.level.Level;
import tilt.game.level.LevelHandler;
import tilt.score.Score;
import tilt.score.ScoreHandler;
import tilt.tools.Math2;
import tilt.tools.Textures;

/**
 * Basic game
 * 
 * @author Jan Martin
 * @group Joseph Guinto, Sam Luc
 * @version 1.0
 */
public class Game {

	/* Template variables */
	public String title = "Tilt";

	public int framerate = 60;
	public int width = 1024;
	public int height = 768;
	public long delta;

	private boolean finished;
	private boolean fullscreen;

	/* Game specific variables */
	// Viewpoint
	Camera camera;

	// ArrayList holding all balls
	EntityHandler entities;

	// Current level
	Level level;
	// List of level locations to be loaded
	ArrayList<String> levelFiles;
	// Current level index (of levelFiles ArrayList)
	int levelIndex = -1;
	// For changing level
	boolean levelToggle;

	// Variables for tilting the board
	float pitch;
	float tilt;
	float roll;
	float friction = 0.8f;

	// Variables for mouse look
	float mousePitch;
	float mouseTilt;

	// For mouse tilt
	float mouseX;
	float mouseY;

	// Skybox... err Skysphere
	Sphere sphere;
	float skyTilt;
	float skyTiltSpeed = 0.1f;

	// Debug
	boolean wireframe;
	float textPadding = 10;

	// Lighting
	FloatBuffer matSpecular;
	FloatBuffer lightPosition;
	FloatBuffer whiteLight;
	FloatBuffer lModelAmbient;

	// Scores
	String playerName;
	String playerNameTemp = new String();
	char[] allowedCharacters = { 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i',
			'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v',
			'w', 'x', 'y', 'z' };
	long score;
	boolean scoreboard;
	// This accessed statically via non-synchronized threads
	public static Score[] highscores;

	// State
	boolean overlay;
	boolean paused;
	boolean help;
	String pausedMessage = "Tilt board to begin";
	String[] helpMessages = { "Use WASD, arrow keys or",
			"drag your mouse to tilt.", "", "Tab to see online scores.", "",
			"R to restart the level, plus", "or minus to navigate levels.", "",
			"Try and get to the red goal", "in the shortest time!", };

	public Game() {
		try {
			create(fullscreen);
			init();
			run();
		} catch (LWJGLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			Display.destroy();
			AL.destroy();
		}
	}

	/**
	 * Create the frame which everything is rendered in and prepare some of the
	 * OpenGl settings. Reloads textures because once the Display is destroyed
	 * it disposes the texture data
	 * 
	 * @param fullscreen
	 *            is the screen fullscreen or windowed with dimensions specified
	 *            at top of class (width, height)
	 */
	private void create(boolean fullscreen) throws LWJGLException {
		Display.setTitle(title);
		Display.setFullscreen(fullscreen);
		if (!Display.isFullscreen()) {
			Display.setDisplayMode(new DisplayMode(width, height));
		}
		Display.setVSyncEnabled(true);
		Display.create();

		// Load textures
		Textures.init();

		// Display.setIcon(new ByteBuffer[] {
		// Textures.toBuffer(Textures.icon16),
		// Textures.toBuffer(Textures.icon32),
		// Textures.toBuffer(Textures.icon48) });

		// Enable textures
		GL11.glEnable(GL11.GL_TEXTURE_2D);

		// Enable depth testing (further away is draw behind closer)
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glDepthFunc(GL11.GL_LEQUAL);

		// Don't draw the side of things we can't possibly see
		GL11.glEnable(GL11.GL_CULL_FACE);
		GL11.glCullFace(GL11.GL_BACK);

		// Transparency
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GL11.glEnable(GL11.GL_ALPHA_TEST);
		GL11.glAlphaFunc(GL11.GL_GREATER, 0.2f);

		// Set background color to "Cornflower Blue"
		GL11.glClearColor(0.6f, 0.7f, 1, 1);

		matSpecular = BufferUtils.createFloatBuffer(4);
		matSpecular.put(1.0f).put(1.0f).put(1.0f).put(1.0f).flip();

		lightPosition = BufferUtils.createFloatBuffer(4);
		lightPosition.put(1.0f).put(1.0f).put(1.0f).put(0.0f).flip();

		whiteLight = BufferUtils.createFloatBuffer(4);
		whiteLight.put(1.0f).put(1.0f).put(1.0f).put(1.0f).flip();

		lModelAmbient = BufferUtils.createFloatBuffer(4);
		lModelAmbient.put(0.7f).put(0.7f).put(0.7f).put(1.0f).flip();

		GL11.glLight(GL11.GL_LIGHT0, GL11.GL_POSITION, lightPosition);
		GL11.glLight(GL11.GL_LIGHT0, GL11.GL_SPECULAR, whiteLight);
		GL11.glLight(GL11.GL_LIGHT0, GL11.GL_DIFFUSE, whiteLight);
		GL11.glLightModel(GL11.GL_LIGHT_MODEL_AMBIENT, lModelAmbient);

		GL11.glLightModeli(GL11.GL_LIGHT_MODEL_TWO_SIDE, GL11.GL_TRUE);

		GL11.glShadeModel(GL11.GL_SMOOTH);

		GL11.glMaterial(GL11.GL_FRONT, GL11.GL_SPECULAR, matSpecular);
		GL11.glMaterialf(GL11.GL_FRONT, GL11.GL_SHININESS, 50.0f);

		GL11.glEnable(GL11.GL_LIGHTING);
		GL11.glEnable(GL11.GL_LIGHT0);

		GL11.glEnable(GL11.GL_COLOR_MATERIAL);
		GL11.glColorMaterial(GL11.GL_FRONT, GL11.GL_AMBIENT_AND_DIFFUSE);
	}

	/**
	 * Initialize objects and prepare some things for loading
	 * 
	 * Only gets called once
	 */
	private void init() throws IOException {
		camera = new Camera();
		camera.view = View.TOPDOWN;

		levelFiles = new ArrayList<String>();

		// Read a file called levels.sam which lists the filenames of all the
		// files in the order they are meant to be played. It annoys me that I
		// can't treat path as a folder because it's inside a JAR
		InputStream stream = ResourceLoader
				.getResourceAsStream("levels/levels.sam");
		DataInputStream in = new DataInputStream(stream);
		BufferedReader br = new BufferedReader(new InputStreamReader(in));
		String line;
		while ((line = br.readLine()) != null) {
			if (!line.endsWith(".joseph")) {
				continue;
			}
			levelFiles.add("levels/" + line);
		}

		nextLevel();

		sphere = new Sphere();
		sphere.setTextureFlag(true);
		sphere.setOrientation(GLU.GLU_INSIDE);
	}

	private void run() {
		long time = getTime();
		while (!finished) {
			delta = getDelta(time);
			time = getTime();
			Display.update();
			if (Display.isCloseRequested()) {
				finished = true;
			} else if (Display.isActive()) {
				input();
				update(delta);
				draw();
				Display.sync(framerate);
			} else {
				if (Display.isVisible() || Display.isDirty()) {
					draw();
				}
			}
		}
	}

	/**
	 * Draw everything statically with OpenGL
	 */
	public void draw() {
		// Clear buffers
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);

		// Project a screen
		GL11.glMatrixMode(GL11.GL_PROJECTION);
		GL11.glLoadIdentity();

		// Set perspective from 0.1 to 100 z depths
		GLU.gluPerspective(45f,
				(float) Display.getWidth() / (float) Display.getHeight(), 10f,
				150.0f);

		// Ready for model rendering
		GL11.glMatrixMode(GL11.GL_MODELVIEW);
		GL11.glLoadIdentity();

		GL11.glColor3f(1, 1, 1);
		GL11.glPushMatrix();
		{
			// Allow mouselook when first person view is enabled
			if (camera.view == View.FIRSTPERSON) {
				mousePitch += Mouse.getDY() / 5;
				mouseTilt += Mouse.getDX() / 5;
				mousePitch = Math2.limit(mousePitch, -90, 90);

				GL11.glRotatef(mousePitch, -1, 0, 0);
				GL11.glRotatef(mouseTilt, 0, 1, 0);

				// You have to tilt the board here before viewing the camera in
				// first person, otherwise the camera will stay on the xz plane
				GL11.glRotatef(tilt, 0, 0, -1);
				GL11.glRotatef(pitch, 1, 0, 0);
			} else if (camera.view == View.TOPDOWN) {
				int offset = level.width();
				if (level.height() > level.width()) {
					offset = level.height();
				}
				if (offset > 17) {
					camera.height = 40 + (offset - 17) * 4;
				} else {
					camera.height = 40;
				}
			}

			// Move to the camera
			camera.view();

			// Draw the sky
			Textures.sky.bind();
			if (camera.view != View.FIRSTPERSON) {
				GL11.glRotatef(90, -1, 0, 0);
			}
			GL11.glRotatef(skyTilt, 0, 0, 1);
			GL11.glDisable(GL11.GL_LIGHTING);
			sphere.draw(60, 9, 9);
			GL11.glEnable(GL11.GL_LIGHTING);
			GL11.glRotatef(skyTilt, 0, 0, -1);
			if (camera.view != View.FIRSTPERSON) {
				GL11.glRotatef(90, 1, 0, 0);
			}

			// Translate back to center of ball if following/first person
			if (camera.view == View.FOLLOW || camera.view == View.FIRSTPERSON) {
				GL11.glTranslatef(level.width(), level.height(), 0);
			}

			if (camera.view != View.FIRSTPERSON) {
				// Tilt the board
				GL11.glRotatef(tilt, 0, 1, 0);
				GL11.glRotatef(pitch, 1, 0, 0);
				GL11.glRotatef(roll, 0, 0, 1);
			}

			// Draw the board/level
			GL11.glDisable(GL11.GL_LIGHTING);
			level.draw();
			GL11.glEnable(GL11.GL_LIGHTING);
			// Draw the balls
			entities.draw();
		}
		GL11.glPopMatrix();

		// Disable depth testing for 2d stuff
		GL11.glDisable(GL11.GL_DEPTH_TEST);

		// Project a screen
		GL11.glMatrixMode(GL11.GL_PROJECTION);
		GL11.glLoadIdentity();

		// Switch to orthographic mode
		GL11.glOrtho(0, width, height, 0, -1, 1);

		// Ready for quad rendering
		GL11.glMatrixMode(GL11.GL_MODELVIEW);
		GL11.glLoadIdentity();

		GL11.glPushMatrix();
		{
			GL11.glDisable(GL11.GL_LIGHTING);
			GL11.glDisable(GL11.GL_CULL_FACE);

			if (overlay) {
				GL11.glColor4f(0, 0, 0, 0.5f);
				GL11.glBegin(GL11.GL_QUADS);
				GL11.glVertex2f(0, 0);
				GL11.glVertex2f(Display.getWidth(), 0);
				GL11.glVertex2f(Display.getWidth(), Display.getHeight());
				GL11.glVertex2f(0, Display.getHeight());
				GL11.glEnd();
			}

			Color.white.bind();

			Textures.font.drawString(textPadding, textPadding, level.name);
			String timer = Math2.formatMilliseconds(score);
			Textures.font.drawString(width - Textures.font.getWidth("00:00:00")
					- textPadding, textPadding, timer);

			// This whole block should be redone and a HUDTextDisplay
			// function should be created
			if (help) {
				overlay = true;
				for (int i = 0; i < helpMessages.length; i++) {
					Textures.font.drawString(textPadding * 6, textPadding * 6
							+ (i + 1) * (Textures.font.getHeight() + 4),
							helpMessages[i]);
				}
			} else if (scoreboard) {
				overlay = true;
				if (highscores == null) {
					Textures.font.drawString(textPadding * 6, textPadding * 6
							+ Textures.font.getHeight() + 4, "Loading...");
				} else {
					if (highscores.length < 1) {
						Textures.font.drawString(textPadding * 6, textPadding
								* 6 + Textures.font.getHeight() + 4,
								"No scores available");
					}
					for (int i = 0; i < highscores.length; i++) {
						String name = highscores[i].name;
						String score = Math2
								.formatMilliseconds(highscores[i].score);
						float sx = textPadding * 6;
						float sy = textPadding * 6 + (i + 1)
								* (Textures.font.getHeight() + 4);
						Textures.font.drawString(sx, sy, name);
						Textures.font
								.drawString(
										width
												- Textures.font
														.getWidth("00:00:00")
												- sx, sy, score);
					}
				}
			} else {
				if (playerName == null) {
					overlay = true;
					Textures.font
							.drawString((width - Textures.font
									.getWidth("Enter your name")) / 2,
									(height - Textures.font.getHeight()) / 2
											- Textures.font.getHeight() * 2,
									"Enter your name");
					Textures.font.drawString((width - Textures.font
							.getWidth(playerNameTemp + '|')) / 2,
							(height - Textures.font.getHeight()) / 2
									- Textures.font.getHeight() * 0.5f,
							playerNameTemp + '|');

				} else if (paused) {
					overlay = true;
					Textures.font
							.drawString((width - Textures.font
									.getWidth(pausedMessage)) / 2,
									(height - Textures.font.getHeight()) / 2
											- Textures.font.getHeight() * 0.5f,
									pausedMessage);
				} else {
					overlay = false;
				}
			}

			Textures.font.drawString(textPadding,
					height - Textures.font.getHeight() - textPadding, "Help");

			Textures.font.drawString(
					width
							- textPadding
							- Textures.font.getWidth((levelIndex + 1) + "/"
									+ levelFiles.size()), height
							- Textures.font.getHeight() - textPadding,
					(levelIndex + 1) + "/" + levelFiles.size());

			GL11.glEnable(GL11.GL_CULL_FACE);
			GL11.glEnable(GL11.GL_LIGHTING);
		}
		GL11.glPopMatrix();

		// Re-enable depth testing
		GL11.glEnable(GL11.GL_DEPTH_TEST);
	}

	/**
	 * Update positions of entities and map
	 * 
	 * @param delta
	 *            milliseconds since last update
	 */
	private void update(long delta) {
		if (playerName == null) {
			roll -= 0.2f;
			pitch = -45f;
			roll %= 360;
			pitch %= 360;
		} else {
			if (!paused) {
				score += delta;
				entities.applyForce(tilt / 3f, -pitch / 3f);
			}
			pitch *= friction;
			tilt *= friction;
			roll *= friction;
		}

		if (level.restart) {
			restartLevel();
		}

		entities.update(delta);
		entities.collision(level);

		if (entities.size() == 0) {
			finishLevel();
		}

		if (!paused) {
			level.update(delta);
		}

		// If the ball the camera was tracking no longer exists, look at another
		if (!entities.contains(camera.child)) {
			camera.child = entities.get(0);
		}

		camera.update();

		skyTilt += skyTiltSpeed;
	}

	/**
	 * Process user input using the static Keyboard class provided by LWJGL
	 * Note: as this is not a listener it must be called every tick
	 */
	private void input() {
		if (Keyboard.isKeyDown(Keyboard.KEY_ESCAPE)) {
			Display.destroy();
			System.exit(0);
		}

		if (Keyboard.isKeyDown(Keyboard.KEY_ADD)
				|| Keyboard.isKeyDown(Keyboard.KEY_EQUALS)) {
			if (!levelToggle) {
				levelToggle = true;
				nextLevel();
			}
		} else if (Keyboard.isKeyDown(Keyboard.KEY_MINUS)
				|| Keyboard.isKeyDown(Keyboard.KEY_UNDERLINE)) {
			if (!levelToggle) {
				levelToggle = true;
				previousLevel();
			}
		} else if (Keyboard.isKeyDown(Keyboard.KEY_R)) {
			// Ignore if typing players name
			if (playerName != null) {
				if (!levelToggle) {
					levelToggle = true;
					restartLevel();
				}
			}
		} else {
			levelToggle = false;
		}

		if (Keyboard.isKeyDown(Keyboard.KEY_TAB)) {
			scoreboard = true;
		} else {
			scoreboard = false;
		}

		// Swapping fullscreen/windowed mode
		if (Keyboard.isKeyDown(Keyboard.KEY_RETURN)) {
			if (Keyboard.isKeyDown(Keyboard.KEY_LMENU)
					|| Keyboard.isKeyDown(Keyboard.KEY_RMENU)) {
				try {
					fullscreen = !fullscreen;
					Display.destroy();
					create(fullscreen);
				} catch (LWJGLException e) {
					e.printStackTrace();
				}
			}
		}

		if (Mouse.getX() < textPadding + Textures.font.getWidth("Help")
				&& Mouse.getY() < Textures.font.getHeight() + textPadding) {
			help = true;
		} else {
			help = false;
		}

		if (playerName == null) {
			while (Keyboard.next()) {
				if (!Keyboard.getEventKeyState()) {
					continue;
				}
				// Special cases
				switch (Keyboard.getEventKey()) {
				case Keyboard.KEY_RETURN:
					if (playerNameTemp.length() > 0) {
						playerName = playerNameTemp;
					}
					break;
				case Keyboard.KEY_BACK:
				case Keyboard.KEY_DELETE:
					if (playerNameTemp.length() > 0) {
						playerNameTemp = playerNameTemp.substring(0,
								playerNameTemp.length() - 1);
					}
					break;
				}
				// There's almost certainly a better way to do this...
				boolean allowed = false;
				for (char c : allowedCharacters) {
					if (Character.toLowerCase(Keyboard.getEventCharacter()) == c) {
						allowed = true;
						break;
					}
				}
				if (allowed) {
					if (playerNameTemp.length() < 8) {
						playerNameTemp += Keyboard.getEventCharacter();
					}
				}
			}
			return;
		} else if (paused) {
			if (Keyboard.isKeyDown(Keyboard.KEY_A)
					|| Keyboard.isKeyDown(Keyboard.KEY_LEFT)
					|| Keyboard.isKeyDown(Keyboard.KEY_D)
					|| Keyboard.isKeyDown(Keyboard.KEY_RIGHT)
					|| Keyboard.isKeyDown(Keyboard.KEY_W)
					|| Keyboard.isKeyDown(Keyboard.KEY_UP)
					|| Keyboard.isKeyDown(Keyboard.KEY_S)
					|| Keyboard.isKeyDown(Keyboard.KEY_DOWN)
					|| Mouse.isButtonDown(0) || Mouse.isButtonDown(1)
					|| Mouse.isButtonDown(2)) {
				paused = false;
			}
			return;
		}

		// Moving the board
		if (Keyboard.isKeyDown(Keyboard.KEY_A)
				|| Keyboard.isKeyDown(Keyboard.KEY_LEFT)) {
			tilt -= 2f;
		}
		if (Keyboard.isKeyDown(Keyboard.KEY_D)
				|| Keyboard.isKeyDown(Keyboard.KEY_RIGHT)) {
			tilt += 2f;
		}
		if (Keyboard.isKeyDown(Keyboard.KEY_W)
				|| Keyboard.isKeyDown(Keyboard.KEY_UP)) {
			pitch -= 2f;
		}
		if (Keyboard.isKeyDown(Keyboard.KEY_S)
				|| Keyboard.isKeyDown(Keyboard.KEY_DOWN)) {
			pitch += 2f;
		}

		// Moving the board with the mouse
		if (camera.view == View.TOPDOWN || camera.view == View.FOLLOW) {
			if (Mouse.isButtonDown(0) || Mouse.isButtonDown(1)
					|| Mouse.isButtonDown(2)) {
				// Prevent the help screen from showing when using mouse drag
				// controls
				help = false;
				Mouse.setGrabbed(true);
				if (mouseX == -1 && mouseY == -1) {
					mouseX = Mouse.getX();
					mouseY = Mouse.getY();
				}
				tilt += Math2.limit((Mouse.getX() - mouseX) / 100f, -2f, 2f);
				pitch -= Math2.limit((Mouse.getY() - mouseY) / 100f, -2f, 2f);
			} else {
				Mouse.setGrabbed(false);
				mouseX = -1;
				mouseY = -1;
			}
		}

		// Camera viewpoints
		if (Keyboard.isKeyDown(Keyboard.KEY_F1)) {
			camera.view = View.TOPDOWN;
			camera.locked = false;
			camera.pitch = 0;
			camera.x = camera.y = camera.z = 0;
			if (!fullscreen) {
				Mouse.setGrabbed(false);
			}
		}
		if (Keyboard.isKeyDown(Keyboard.KEY_F2)) {
			camera.view = View.FOLLOW;
			camera.locked = true;
			camera.height = 40;
			camera.pitch = 0;
			if (!fullscreen) {
				Mouse.setGrabbed(false);
			}
		}
		if (Keyboard.isKeyDown(Keyboard.KEY_F3)) {
			camera.view = View.FIRSTPERSON;
			camera.locked = true;
			// Jan you are a bad boy for making all these variables public
			camera.height = camera.child.height;
			// ...b-but it feels so good :'(
			camera.pitch = 90;
			Mouse.setGrabbed(true);
		}

		if (Keyboard.isKeyDown(Keyboard.KEY_F4)) {
			wireframe = !wireframe;
			if (wireframe) {
				GL11.glPolygonMode(GL11.GL_FRONT, GL11.GL_LINE);
			} else {
				GL11.glPolygonMode(GL11.GL_FRONT, GL11.GL_FILL);
			}
		}
	}

	/**
	 * Restart the current level
	 */
	public void restartLevel() {
		level = LevelHandler.load(levelFiles.get(levelIndex));

		// If there was a problem loading the level just skip it
		if (level == null) {
			nextLevel();
			return;
		}

		// Wipe entities
		entities = new EntityHandler();
		float[][] spawns = level.spawns();

		// Check if there are any spawns at all
		if (spawns.length < 1) {
			System.out.println(level.name
					+ " has no spawns! What are you doing!?");
			nextLevel();
		}

		for (int i = 0; i < spawns.length; i++) {
			float x = spawns[i][0];
			float y = spawns[i][1];
			float z = 15;
			entities.add(new Ball(x, y, z, 0.9f));
		}

		camera.child = entities.get(0);

		score = 0;
		paused = true;
	}

	/**
	 * Finish the current level
	 */
	public void finishLevel() {
		ScoreHandler.upload(playerName, level.name, score);
		nextLevel();
	}

	/**
	 * Increment level counter and start level
	 */
	public void nextLevel() {
		levelIndex++;
		if (levelIndex > levelFiles.size() - 1) {
			levelIndex = 0;
		}
		restartLevel();

		// Nullify highscores as we don't know if/when they will be loaded
		highscores = null;
		// New thread
		ScoreHandler.get(level.name);
	}

	/**
	 * See nextLevel
	 */
	public void previousLevel() {
		levelIndex--;
		if (levelIndex < 0) {
			levelIndex = levelFiles.size() - 1;
		}
		restartLevel();

		highscores = null;
		ScoreHandler.get(level.name);
	}

	/**
	 * These probably don't need to exist
	 */
	private long getTime() {
		return System.currentTimeMillis();
	}

	private long getDelta(long time) {
		return System.currentTimeMillis() - time;
	}
}