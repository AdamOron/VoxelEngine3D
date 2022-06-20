package entities;

import org.lwjgl.util.vector.Vector3f;

import models.TexturedModel;

public class Crosshair extends Entity
{
	public Crosshair(TexturedModel model, Vector3f position, float xRot, float yRot, float zRot, float scale)
	{
		super(model, position, xRot, yRot, zRot, scale);
	}	
}