package engineTester;

import java.util.ArrayList;
import java.util.Arrays;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import entities.BlockDestroyer;
import entities.Camera;
import entities.Entity;
import entities.Light;
import entities.Player;
import gameAssets.Block;
import gameAssets.Chunk;
import models.RawModel;
import models.TexturedModel;
import objConverter.ModelData;
import objConverter.OBJFileLoader;
import renderEngine.DisplayManager;
import renderEngine.Loader;
import renderEngine.MasterRenderer;
import textures.ModelTexture;
import toolbox.FrustumCulling;
import toolbox.SimplexNoise;

public class MainGameLoop
{	
	public static void main(String[] args)
	{
		DisplayManager.createDisplay();

		Loader loader = new Loader();

		// ----------- TERRAIN TEXTURE -----------------

		// --------------------------------------------

		Light light = new Light(new Vector3f(0, 2000, -150), new Vector3f(1, 1, 1));

		Player player = new Player(new Vector3f(0, 0, -1), new Vector3f(0, 0, 0));
		
		Camera camera = new Camera(player);

		float fFov = 90f;
		float viewDistance = 64;
		
		MasterRenderer renderer = new MasterRenderer(fFov);
		FrustumCulling fc = new FrustumCulling(fFov, viewDistance);

		// --------------- LOAD AND CREATE BLOCK MODEL -----------------
		ModelTexture gray = new ModelTexture(loader.loadTexture("grassy"));
		ModelTexture gold = new ModelTexture(loader.loadTexture("gray"));

		ModelData blockData = OBJFileLoader.loadOBJ("blocks/cube");
		
		RawModel blockRawModel = loader.loadToVAO(blockData.getVertices(), blockData.getTextureCoords(), blockData.getNormals(), blockData.getIndices());

		TexturedModel blockGray = new TexturedModel(blockRawModel, gray);
		TexturedModel blockGold = new TexturedModel(blockRawModel, gold);
		
		//HashMap<Chunk, Chunk> map = new HashMap<>();
		ArrayList<Chunk> map = new ArrayList<>();
		
		BlockDestroyer destroyer = new BlockDestroyer(camera);
		
		SimplexNoise noise = new SimplexNoise();
		
		for(int i = 0; i < 20; i++)
		{
			for(int j = 0; j < 20; j++)
			{
				Chunk chunk = new Chunk(blockGray, blockGold, new Vector3f(i*16, 0, j*-16), renderer, noise);
				chunk.findVisibleBlocks();
				map.add(chunk);
			}
		}
		
		player.move(map);
		
		Block b = new Block(blockGray, new Vector3f(0, 0, 0));
		
		while(!Display.isCloseRequested())
		{
			player.move(map);
			camera.updatePosition();
			
			ArrayList<Chunk> visibleChunks = new ArrayList<>();
			
			for(Chunk chunk : map)
			{
				if(fc.isVisible(player, chunk))
				{
					//renderer.processEntity(b);
					chunk.renderVisibleBlocks();
					//Entity chunkEntity = chunk.constructMesh(blockData, loader);
					//renderer.processEntity(chunkEntity);
					
					visibleChunks.add(chunk);
				}
			}
			
			if(Mouse.isButtonDown(0))
			{
				destroyer.update();
				destroyer.destroyBlock(visibleChunks);
			}
			
			renderer.render(light, camera);
			DisplayManager.updateDisplay();
		}

		renderer.cleanUp();
		loader.cleanUp();
		DisplayManager.closeDisplay();
	}
}