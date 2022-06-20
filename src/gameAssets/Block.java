package gameAssets;

import org.lwjgl.util.vector.Vector3f;
import entities.Entity;
import models.TexturedModel;

public class Block extends Entity
{
	// The position vector is the bottom left point of the cube (if looking from above).
	
	public Block(TexturedModel model, Vector3f position)
	{
		super(model, position, 0, 0, 0, 0.5f);
	}
	
	public Block(Vector3f position)
	{
		super(position);
	}
	
	public Vector3f getPosition()
	{
		return position;
	}
	
	public boolean containsPoint(Vector3f point) // Check if a point is inside the cube
	{
		return point.x >= position.x && point.x <= position.x + 1 &&
			   point.y >= position.y && point.y <= position.y + 1 &&
			   point.z >= position.z - 1 && point.z <= position.z;
	}
	
	@Override
	public boolean equals(Object o)
	{
		if(o == null) return false;
		
		if(!(o instanceof Block)) return false;
		
		Block oBlock = (Block) o;
		
		Vector3f oPos = oBlock.getPosition();
		
		return position.x == oPos.x && position.y == oPos.y && position.z == oPos.z;
	}
	
	@Override
	public int hashCode()
	{
		int result = 17;
		
		result = 31 * result + (int) position.x;
		result = 31 * result + (int) position.y;
		result = 31 * result + (int) position.z;
		
		return result;
	}
}