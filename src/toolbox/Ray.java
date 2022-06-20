package toolbox;

import org.lwjgl.util.vector.Vector3f;

public class Ray
{
	private Vector3f startPos;
	private Vector3f direction;
	
	public Ray(Vector3f startPos, Vector3f direction)
	{
		this.startPos = startPos;
		this.direction = direction;
	}

	public Vector3f getPointOnRay(float distance)
	{
		Vector3f offsetFromStart = new Vector3f(direction.x * distance, direction.y * distance, direction.z * distance);
		
		return Vector3f.add(startPos, offsetFromStart, null);
	}
	
	public Vector3f getStartPos()
	{
		return startPos;
	}
	
	public Vector3f getDirection()
	{
		return direction;
	}
}