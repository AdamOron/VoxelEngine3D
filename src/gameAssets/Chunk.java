package gameAssets;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import entities.Entity;
import models.RawModel;
import models.TexturedModel;
import objConverter.ModelData;
import objConverter.OBJFileLoader;
import objConverter.Vertex;
import renderEngine.Loader;
import renderEngine.MasterRenderer;
import toolbox.Maths;
import toolbox.SimplexNoise;

public class Chunk
{
	public final static int X_AMOUNT = 16;
	public final static int Y_AMOUNT = 32;
	public final static int Z_AMOUNT = 16;
	public final static int MAX_HEIGHT = 16;
	
	private final static int GRASS_DEPTH = 4;
	
	public Vector3f position; // Bottom Left Block Position (If looking from above)
	TexturedModel model1, model2;
	MasterRenderer renderer;
	
	//private byte[][][] blockByte;
	//private ChunkColumn[][] columns;
	private byte[][][] blocks;
	private ArrayList<Vector3f> visibleBlocks = new ArrayList<>();
	//private ArrayList<Vector3f> visibleBlocks;
	
	public Chunk(TexturedModel model1, TexturedModel model2, Vector3f position, MasterRenderer renderer, SimplexNoise noise)
	{
		this.position = position;
		this.model1 = model1;
		this.model2 = model2;
		this.renderer = renderer;
		
		//columns = new ChunkColumn[X_AMOUNT][Z_AMOUNT];
		blocks = new byte[X_AMOUNT][Z_AMOUNT][];
		for(int x = 0; x < X_AMOUNT; x++)
		{
			for(int z = 0; z < Z_AMOUNT; z++)
			{	
				int height = (int) (noise.eval((double) (position.x + x) / 75, (double) (position.z - z) / 75) * MAX_HEIGHT);
				//columns[x][z] = new ChunkColumn(new Vector2f(position.x + x, position.z - z), Y_AMOUNT + height);
				blocks[x][z] = new byte[Y_AMOUNT + height];
				
				for(int y = 0; y < blocks[x][z].length; y++)
				{
					blocks[x][z][y] = 1;
				}
			}
		}
	}
	
	public void findVisibleBlocks()
	{		
		visibleBlocks = new ArrayList<>();
		
		for(int x = 0; x < X_AMOUNT; x++)
		{
			for(int z = Z_AMOUNT-1; z >= 0; z--)
			{
				//ChunkColumn column = columns[x][z];
				
				for(int y = 0; y < blocks[x][z].length; y++)
				{
					if(blocks[x][z][y] != 0)
					{
						if(isVisible(x, y, z))
						{
							Vector3f blockPos = new Vector3f(position.x + x, position.y + y, position.z - z);
							visibleBlocks.add(blockPos);
						}
					}
				}
			}
		}
	}
	
	public void renderVisibleBlocks()
	{
		for(Vector3f block : visibleBlocks)
		{
			renderer.processEntity(new Block(model1, block));
		}
	}
	
	public void render()
	{
		for(int x = 0; x < X_AMOUNT; x++)
		{
			for(int z = Z_AMOUNT-1; z >= 0; z--)
			{
				//ChunkColumn column = columns[x][z];
				
				for(int y = 0; y < blocks[x][z].length; y++)
				{
					if(blocks[x][z][y] != 0)
					{
						if(isVisible(x, y, z))
						{
							Vector3f blockPos = new Vector3f(position.x + x, position.y + y, position.z - z);
							renderer.processEntity(new Block(model1, blockPos));
						}
					}
				}
			}
		}
	}
	
	public Entity constructMesh(ModelData block, Loader loader)
	{
		ArrayList<Vertex> vertices = new ArrayList<>();
		ArrayList<Integer> indices = new ArrayList<>();
		
		int blockCounter = 0;
		
		for(int x = 0; x < X_AMOUNT; x++)
		{
			for(int z = 0; z < Z_AMOUNT; z++)
			{
				for(int y = 0; y < blocks[x][z].length; y++)
				{
					if(isVisible(x, y, z))
					{
						for(int i = 0; i < block.getIndices().length; i += 3)
						{
							indices.add(block.getIndices()[i] + block.getVertices().length);
							indices.add(block.getIndices()[i+1] + block.getTextureCoords().length);
							indices.add(block.getIndices()[i+2] + block.getNormals().length);
						}
						
						for(int i = 0; i < block.getVertices().length; i += 3)
						{
							float posX = block.getVertices()[i] + x * 2;
							float posY = block.getVertices()[i+1] + y * 2;
							float posZ = block.getVertices()[i+2] - z * 2;
							
							Vector3f position = new Vector3f(posX, posY, posZ);
							Vertex vertex = new Vertex(vertices.size(), position);
							vertices.add(vertex);
						}
						
						blockCounter++;
					}
				}
			}
		}
		
		ArrayList<Vector2f> textureCoords = new ArrayList<>();
		ArrayList<Vector3f> normals = new ArrayList<>();
		
		for(int i = 0; i < blockCounter; i++)
		{
			for(int j = 0; j < block.getTextureCoords().length; j += 2)
			{
				Vector2f textureCoord = new Vector2f(block.getTextureCoords()[j], block.getTextureCoords()[j+1]);
				textureCoords.add(textureCoord);
			}
			
			for(int j = 0; j < block.getNormals().length; j += 3)
			{
				Vector3f normal = new Vector3f(block.getNormals()[j], block.getNormals()[j+1], block.getNormals()[j+2]);
				normals.add(normal);
			}
		}
		
		float[] verticesArray = new float[vertices.size() * 3];
		float[] textureCoordsArray = new float[textureCoords.size() * 2];
		float[] normalsArray = new float[normals.size() * 3];
		
		float furthestPoint = OBJFileLoader.convertDataToArrays(vertices, textureCoords, normals, verticesArray, textureCoordsArray, normalsArray);
		
		int[] indicesArray = OBJFileLoader.convertIndicesListToArray(indices);
		
		ModelData chunkData = new ModelData(verticesArray, textureCoordsArray, normalsArray, indicesArray, furthestPoint);
		
		RawModel chunkModel = loader.loadToVAO(chunkData.getVertices(), chunkData.getTextureCoords(), chunkData.getNormals(), chunkData.getIndices());
		
		TexturedModel chunk = new TexturedModel(chunkModel, model1.getTexture());
		
		return new Entity(chunk, position, 0, 0, 0, 1);
	}
	
	private boolean isVisible(int x, int y, int z)
	{
		if(blocks[x][z][y] == 0) return false;
		
		if(x == 0 || x == X_AMOUNT-1)
		{
			return true;
		}
		
		if(z == 0 || z == Z_AMOUNT-1)
		{
			return true;
		}
		
		if(y == 0 || y == blocks[x][z].length-1)
		{
			return true;
		}
		
		if(blocks[x][z][y-1] == 0) // Bottom Face
		{
			return true;
		}
		
		if(blocks[x][z][y+1] == 0) // Top Face
		{
			return true;
		}
		
		if(blocks[x-1][z].length <= y)
		{
			return true;
		}
		
		if(blocks[x+1][z].length <= y)
		{
			return true;
		}
		
		if(blocks[x][z+1].length <= y)
		{
			return true;
		}
		
		if(blocks[x][z-1].length <= y)
		{
			return true;
		}
		
		
		if(blocks[x-1][z][y] == 0) // Left Face
		{
			return true;
		}
		
		if(blocks[x+1][z][y] == 0) // Right Face
		{
			return true;
		}
		
		if(blocks[x][z+1][y] == 0) // Front Face
		{
			return true;
		}
		
		if(blocks[x][z-1][y] == 0) // Back Face
		{
			return true;
		}
		
		return false;
	}
	
//	private boolean isFaceVisible(Vector3f playerRotation, Vector3f faceNormal)
//	{
//		Vector3f playerDirection = Maths.directionFromRotation(playerRotation);
//		
//		float dotProduct = Vector3f.dot(playerDirection, faceNormal);
//		
//		return dotProduct < 0;
//	}
	
	public Chunk(Vector3f position)
	{
		this.position = position;
	}
	
	public Vector3f containsBlock(Vector3f point)
	{
		if(point.x > position.x + X_AMOUNT || point.x < position.x || point.z < position.z - Z_AMOUNT || point.z > position.z) return new Vector3f(-1, -1, -1);
		
		for(Vector3f blockPos : visibleBlocks)
		{	
			if(new Block(blockPos).containsPoint(point)) return blockPos;
		}
		
//		int x = Math.abs((int) point.x) % X_AMOUNT;
//		int z = Math.abs((int) point.z) % Z_AMOUNT;
//		
//		System.out.println(x+", "+z);
//		
//		byte[] column = blocks[x][z];
//		
//		if(point.y > position.y + column.length) return new Vector3f(-1, -1, -1);
//		
//		int y = Math.abs((int) point.y) % (column.length);
//		
//		if(blocks[x][z][y] == 1) return new Vector3f(x, y, z);
		
		return new Vector3f(-1, -1, -1);
	}
	
	public void destroyBlock(float x, float y, float z)
	{
		if(!inBounds(x, y, z)) throw new IllegalArgumentException("Block does exist in specified chunk.");
		
		int actualX = Math.abs((int) x) % X_AMOUNT;
		int actualZ = Math.abs((int) z) % Z_AMOUNT;
		int actualY = Math.abs((int) y) % Y_AMOUNT;
		
		if(y >= blocks[actualX][actualZ].length) throw new IllegalArgumentException("Block does exist in specified chunk (y out of bounds).");
		
		blocks[actualX][actualZ][actualY] = 0;
		
		findVisibleBlocks();
	}
	
	public float getBlockHeight(float x, float z)
	{
		int actualX = Math.abs((int) x) % X_AMOUNT;
		int actualZ = Math.abs((int) z) % Z_AMOUNT;
		
		if(!inBounds(x, position.y, z)) throw new IllegalArgumentException("Block does exist in specified chunk.");
		
		float highest = position.y;
		
		for(int y = 0; y < blocks[actualX][actualZ].length; y++)
		{
			if(blocks[actualX][actualZ][y] != 0)
			{
				highest = y;
			}
		}
		
		return highest + 1;
	}
	
	private boolean inBounds(float x, float y, float z)
	{
		if(x < position.x || y < position.y || z > position.z) return false;
		
		if(x >= position.x + X_AMOUNT || y >= position.y + Y_AMOUNT || z <= position.z - Z_AMOUNT) return false;
		
		return true;
	}
	
//	public Chunk(TexturedModel model1, TexturedModel model2, Vector3f position, MasterRenderer renderer)
//	{
//		this.position = position;
//		this.model1 = model1;
//		this.model2 = model2;
//		this.renderer = renderer;
//		
//		blockByte = new byte[X_AMOUNT][Y_AMOUNT][Z_AMOUNT];
//		for(int x = 0; x < X_AMOUNT; x++)
//		{
//			for(int y = 0; y < Y_AMOUNT; y++)
//			{
//				for(int z = 0; z < Z_AMOUNT; z++)
//				{
//					blockByte[x][y][z] = 1;
//				}
//			}
//		}
//		
//		visibleBlocks = new ArrayList<>(16);
//	}
	
//	public void render()
//	{
//		visibleBlocks = new ArrayList<>(16);
//		
//		for(int x = 0; x < X_AMOUNT; x++)
//		{
//			for(int y = 0; y < Y_AMOUNT; y++)
//			{
//				for(int z = 0; z < Z_AMOUNT; z++)
//				{
//					if(blockByte[x][y][z] == 0) continue;
//					
//					if(isVisible(x, y, z))
//					{
//						Vector3f blockPos = new Vector3f(position.x + x, position.y - y, position.z - z);
//						
//						renderer.processEntity(new Block(model1, blockPos));
//						
//						visibleBlocks.add(blockPos);
//					}
//				}
//			}
//		}
//	}
//	
//	private boolean isVisible(int x, int y, int z)
//	{
//		if(x == 0 || x == X_AMOUNT-1 || 
//		   y == 0 || y == Y_AMOUNT-1 || 
//		   z == 0 || z == Z_AMOUNT-1)
//		{
//			return true;
//		}
//		
//		if(blockByte[x][y+1][z] == 0 ||
//		   blockByte[x][y-1][z] == 0 ||
//		   blockByte[x][y][z+1] == 0 ||
//	 	   blockByte[x][y][z-1] == 0 ||
//		   blockByte[x+1][y][z] == 0 ||
//		   blockByte[x-1][y][z] == 0)
//		{
//			return true;
//		}
//		
//		return false;
//	}
	
//	public Float getBlockHeight(int x, int z)
//	{	
//		for(Vector3f blockPos : visibleBlocks)
//		{
//			if(blockPos.x == x && blockPos.z == z) return blockPos.y;
//		}
//		
//		return null;
//	}
	
	@Override
	public boolean equals(Object o)
	{
		if(o == null) return false;
		
		if(!(o instanceof Chunk)) return false;
		
		Chunk oChunk = (Chunk) o;
		
		return position.equals(oChunk.position);
	}
	
	@Override
	public int hashCode()
	{
		int result = 17;
		
		result = 31 * result + (int) position.x;
		result = 31 * result + (int) position.z;
		
		return result;
	}
}

class ChunkColumn
{	
	int height; // Amount of blocks above height
	
	byte[] blocks;
	
	Vector2f position; // X, Z
	
	public ChunkColumn(Vector2f position, int height)
	{
		this.position = position;
		this.height = height;
		
		blocks = new byte[height];
		
		for(int i = 0; i < height; i++)
		{
			blocks[i] = 1;
		}
	}
}