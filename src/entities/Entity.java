package entities;

import org.lwjgl.util.vector.Vector3f;

import models.TexturedModel;

public class Entity
{
	private TexturedModel model;
	protected Vector3f position;
	private float xRot, yRot, zRot;
	private float scale;
	
	public Entity(TexturedModel model, Vector3f position, float xRot, float yRot, float zRot, float scale)
	{
		this.model = model;
		this.position = position;
		this.xRot = xRot;
		this.yRot = yRot;
		this.zRot = zRot;
		this.scale = scale;
	}

	public Entity(Vector3f position)
	{
		this.position = position;
	}
	
	public void increasePosition(float dx, float dy, float dz)
	{
		position.x += dx;
		position.y += dy;
		position.z += dz;
	}
	
	public void increaseRotation(float dx, float dy, float dz)
	{
		xRot += dx;
		yRot += dy;
		zRot += dz;
	}
	
	public TexturedModel getModel()
	{
		return model;
	}

	public void setModel(TexturedModel model)
	{
		this.model = model;
	}

	public Vector3f getPosition()
	{
		return position;
	}

	public void setPosition(Vector3f position)
	{
		this.position = position;
	}

	public float getxRot()
	{
		return xRot;
	}

	public void setxRot(float xRot)
	{
		this.xRot = xRot;
	}

	public float getyRot()
	{
		return yRot;
	}

	public void setyRot(float yRot)
	{
		this.yRot = yRot;
	}

	public float getzRot()
	{
		return zRot;
	}

	public void setzRot(float zRot)
	{
		this.zRot = zRot;
	}

	public float getScale()
	{
		return scale;
	}

	public void setScale(float scale)
	{
		this.scale = scale;
	}
}