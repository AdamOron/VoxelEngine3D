package entities;

import org.lwjgl.util.vector.Vector3f;

public class Camera
{
	Player player;
	
	public Vector3f position;
	public Vector3f rotation; // Pitch, Yaw, Roll
	
	public Camera()
	{
		position = new Vector3f(0, 0, 0);
		rotation = new Vector3f(0, 0, 0);
	}
	
	public Camera(Vector3f position, Vector3f rotation)
	{
		this.position = position;
		this.rotation = rotation;
	}
	
	public Camera(Player player)
	{
		position = new Vector3f(player.getPosition().x, player.getPosition().y + player.PLAYER_HEIGHT, player.getPosition().z);
		rotation = player.getRotation();
		
		this.player = player;
	}
	
	public void updatePosition()
	{
		position = new Vector3f(player.getPosition().x, player.getPosition().y + player.PLAYER_HEIGHT, player.getPosition().z);
	}
	
	public Vector3f getPosition()
	{
		return position;
	}
	
	public Vector3f getRotation()
	{
		return rotation;
	}
}