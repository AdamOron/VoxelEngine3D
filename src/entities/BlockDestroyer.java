package entities;

import java.util.ArrayList;

import org.lwjgl.util.vector.Vector3f;
import gameAssets.Block;
import gameAssets.Chunk;
import toolbox.Maths;
import toolbox.Ray;

public class BlockDestroyer
{
	private final long COOLDOWN = 175; 
	
	private long lastTime; // Last time of block destruction.
	
	private Ray ray;
	private Camera camera;
	
	public BlockDestroyer(Camera camera)
	{
		this.camera = camera;
		
		lastTime = System.currentTimeMillis() - COOLDOWN;
	}
	
	public void update()
	{
		ray = new Ray(camera.position, Maths.directionFromRotation(camera.getRotation()));
	}
	
	public void destroyBlock(ArrayList<Chunk> visibleChunks) // Coordinates of Block which intersects with ray
	{
		if(System.currentTimeMillis() - lastTime < COOLDOWN)
		{
			return;
		}
		
		for(Chunk chunk : visibleChunks)
		{
			for(float i = 0; i < 16; i += 0.1f)
			{
				Vector3f rayPos = ray.getPointOnRay(i);
				
				try {
					float heightAtPos = chunk.getBlockHeight(rayPos.x, rayPos.z);
					
					if(rayPos.y <= heightAtPos)
					{
						chunk.destroyBlock(rayPos.x, rayPos.y, rayPos.z);
						
						lastTime = System.currentTimeMillis();
						
						return;
					}
					
				} catch (IllegalArgumentException e) {}
			}
			
			break;
		}
	}
	
	public Ray getCurrentRay()
	{
		return ray;
	}
	
	public Vector3f getDirection()
	{
		return ray.getDirection();
	}
}