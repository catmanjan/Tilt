package tilt.camera;

import org.lwjgl.opengl.GL11;

import tilt.game.entities.Entity;

public class Camera {

	public View view;

	public float x;
	public float y;
	public float z;
	public float height;

	public float pitch;
	public float roll;
	public float yaw;

	public Entity child;
	public boolean locked;

	public Camera(float x, float y, float z) {
		this.x = x;
		this.y = y;
		this.z = z;
		view = View.TOPDOWN;
	}

	public Camera() {
		this(0, 0, 0);
	}

	public void update() {
		if (locked && child != null) {
			x = child.x;
			y = child.y;
			z = child.z;
		}
	}

	public void step(float distance) {
		step(distance, 0);
	}

	public void step(float distance, float offset) {
		double hRad = Math.toRadians(yaw + offset + 90);

		x -= (float) (Math.cos(hRad) * distance);
		z -= (float) (Math.sin(hRad) * distance);

		if (offset == 0) {
			double vRad = Math.toRadians(pitch + 90);
			y -= (float) (Math.cos(vRad) * distance);
		}
	}

	public void view() {
		// This is inverted because of OpenGls reverse yAxis
		GL11.glRotatef(pitch, -1, 0, 0);
		GL11.glRotatef(yaw, 0, 1, 0);
		GL11.glRotatef(roll, 0, 0, 1);
		GL11.glTranslatef(-x, -y, -z - height);
	}

}
