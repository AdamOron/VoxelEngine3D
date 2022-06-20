package entities;

import java.util.ArrayList;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.util.vector.Vector3f;
import gameAssets.Chunk;
import renderEngine.DisplayManager;

public class Player
{
	private Vector3f position;
	private Vector3f rotation; // Pitch, Yaw, Roll

	private final int MAX_HEALTH = 100; 
	private int health;
	
	public final float PLAYER_HEIGHT = 3.5f;

	private static final float WALK_SPEED = 7;
	private static final float RUN_SPEED = 15;
	private static final float GRAVITY = -100;
	private static final float JUMP_FORCE = 30;

	private static float terrainHeight = 0;

	private static final float MOUSE_SENS = 15f;

	private float currentSpeedZ = 0;
	private float currentSpeedX = 0;
	private float upwardsSpeed = 0;
	private boolean isInAir = false;

	public Player(Vector3f position, Vector3f rotation)
	{
		this.position = position;
		this.rotation = rotation;
		
		this.health = MAX_HEALTH;
	}

	public void move(ArrayList<Chunk> visibleChunks) //HashMap<Chunk, Chunk> map)
	{
		checkInputs();

		calculatePitch();
		calculateYaw();

		calculateMovementX(visibleChunks);
		calculateMovementZ(visibleChunks);

		upwardsSpeed += GRAVITY * DisplayManager.getFrameTimeSeconds();

		increasePosition(0, upwardsSpeed * DisplayManager.getFrameTimeSeconds(), 0);

		if(currentSpeedZ != 0 || currentSpeedX != 0 || upwardsSpeed != 0)
		{
			Chunk currentChunk = getCurrentChunk(visibleChunks);
			updateTerrainHeight(currentChunk);
			//System.out.println(terrainHeight);
		}

		if(position.y <= terrainHeight)
		{
			upwardsSpeed = 0;
			isInAir = false;

			position.y = terrainHeight;
		}
	}

	private void jump()
	{
		if(!isInAir)
		{
			upwardsSpeed = JUMP_FORCE;

			isInAir = true;
		}
	}

	private void checkInputs()
	{
		float actualVelocity = WALK_SPEED;

		if(Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)) actualVelocity = RUN_SPEED;

		if(Keyboard.isKeyDown(Keyboard.KEY_W))
		{
			currentSpeedZ = actualVelocity;
		} else if(Keyboard.isKeyDown(Keyboard.KEY_S)) {
			currentSpeedZ = -actualVelocity;
		} else {
			currentSpeedZ = 0; // Set speed to 0 only if not in air
		}

		if(Keyboard.isKeyDown(Keyboard.KEY_A))
		{
			currentSpeedX = actualVelocity;
		} else if(Keyboard.isKeyDown(Keyboard.KEY_D)) {
			currentSpeedX = -actualVelocity;
		} else {
			currentSpeedX = 0;
		}

		if(currentSpeedZ != 0 && currentSpeedX != 0) // Prevent excessive speed as result of moving in both axis directions
		{
			currentSpeedZ /= 1.5f;
			currentSpeedX /= 1.5f;
		}

		if(Keyboard.isKeyDown(Keyboard.KEY_SPACE))
		{
			jump();
		}
	}

	private void updateTerrainHeight(Chunk chunk)
	{
		if(chunk != null)
		{
			Float height = chunk.getBlockHeight((int) position.x, (int) position.z);
			if(height != null)
			{
				terrainHeight = height;
				return;
			}
		}

		terrainHeight = 0;
	}

	private Chunk getCurrentChunk(ArrayList<Chunk> visibleChunks)
	{
		Vector3f chunkPos = getChunkPosition(position);

		int chunkIndex = visibleChunks.indexOf(new Chunk(chunkPos));

		if(chunkIndex != -1) return visibleChunks.get(chunkIndex);

		return null;
	}

	private Chunk getChunk(Vector3f position, ArrayList<Chunk> visibleChunks)
	{
		Vector3f chunkPos = getChunkPosition(position);

		int chunkIndex = visibleChunks.indexOf(new Chunk(chunkPos));

		if(chunkIndex != -1) return visibleChunks.get(chunkIndex);

		return null;
	}

	private Vector3f getChunkPosition(Vector3f position)
	{
		return new Vector3f(roundDown(position.x, Chunk.X_AMOUNT) * 16, 0, (roundDown(position.z, Chunk.Z_AMOUNT) + 1) * 16);
	}

	private float roundDown(float value, float place)
	{
		float result = value / place;
		result = (float) Math.floor(result);

		return result;
	}

	private void calculateMovementZ(ArrayList<Chunk> visibleChunks)
	{
		float distanceZ = currentSpeedZ * DisplayManager.getFrameTimeSeconds();
		float dx = (float) (distanceZ * Math.sin(Math.toRadians(180-rotation.y)));
		float dz = (float) (distanceZ * Math.cos(Math.toRadians(180-rotation.y)));

		Vector3f nextPosition = new Vector3f(position.x + dx, position.y, position.z + dz);

		// Collision detection
		Chunk nextChunk = getChunk(nextPosition, visibleChunks);

		if(nextChunk != null)
		{
			Float nextY = nextChunk.getBlockHeight((int) nextPosition.x, (int) nextPosition.z);
	
			if(nextY <= position.y)
			{
				position = nextPosition;
			}
		} else {
			position = nextPosition;
		}
	}

	private void calculateMovementX(ArrayList<Chunk> visibleChunks)
	{
		float distanceX = currentSpeedX * DisplayManager.getFrameTimeSeconds();
		float dx = (float) (distanceX * Math.cos(Math.toRadians(rotation.y-180)));
		float dz = (float) (distanceX * Math.sin(Math.toRadians(rotation.y-180)));

		Vector3f nextPosition = new Vector3f(position.x + dx, position.y, position.z + dz);

		// Collision detection
		Chunk nextChunk = getChunk(nextPosition, visibleChunks);
		
		if(nextChunk != null)
		{
			Float nextY = nextChunk.getBlockHeight((int) nextPosition.x, (int) nextPosition.z);
	
			if(nextY <= position.y)
			{
				position = nextPosition;
			}
		} else {
			position = nextPosition;
		}
	}

	private void calculatePitch()
	{
		rotation.x = constrainValue(rotation.x - Mouse.getDY() * MOUSE_SENS * DisplayManager.getFrameTimeSeconds(), -90, 90);
	}

	private void calculateYaw()
	{
		rotation.y += Mouse.getDX() * MOUSE_SENS * DisplayManager.getFrameTimeSeconds();
	}

	private float constrainValue(float valueToConstrain, float minValue, float maxValue)
	{
		if(valueToConstrain > maxValue)
		{
			valueToConstrain = maxValue;
		}

		if(valueToConstrain < minValue)
		{
			valueToConstrain = minValue;
		}

		return valueToConstrain;
	}

	public void increasePosition(float x, float y, float z)
	{
		position.x += x;
		position.y += y;
		position.z += z;
	}

	public void increaseRotation(float x, float y, float z)
	{
		rotation.x += x;
		rotation.y += y;
		rotation.z += z;
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