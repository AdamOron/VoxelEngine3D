package renderEngine;

import java.util.ArrayList;
import java.util.HashMap;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Matrix4f;
import entities.Camera;
import entities.Entity;
import entities.Light;
import models.TexturedModel;
import shaders.StaticShader;
import shaders.TerrainShader;
import terrains.Terrain;

public class MasterRenderer
{
	private float fFov;
	private static final float fNear = 0.1f, fFar = 1000f;	
	private static final float SKY_RED = 0.53f, SKY_GREEN = 0.81f, SKY_BLUE = 0.93f;
	
	private Matrix4f projectionMatrix;
	
	private StaticShader shader = new StaticShader();
	private EntityRenderer renderer;
	
	//private TerrainRenderer terrainRenderer;
	//private TerrainShader terrainShader = new TerrainShader();
	
	private HashMap<TexturedModel, ArrayList<Entity>> entities = new HashMap<>();
	private ArrayList<Terrain> terrains = new ArrayList<>();
	
	public MasterRenderer(float fFov)
	{
		this.fFov = fFov;
		
		enableCulling();
		
		createProjectionMatrix();
		
		renderer = new EntityRenderer(shader, projectionMatrix);
		
		//terrainRenderer = new TerrainRenderer(terrainShader, projectionMatrix);
	}
	
	public void render(Light light, Camera camera)
	{
		prepare();
		
		shader.start();
		shader.loadSkyColor(SKY_RED, SKY_GREEN, SKY_BLUE);
		shader.loadLight(light);
		
		shader.loadViewMatrix(camera);
		
		renderer.render(entities);
		
		shader.stop();
		
		//terrainShader.start();
		//terrainShader.loadLight(light);
		//terrainShader.loadSkyColor(SKY_RED, SKY_GREEN, SKY_BLUE);
		//terrainShader.loadViewMatrix(camera);
		
		//terrainRenderer.render(terrains);
		
		//terrainShader.stop();
		
		//terrains.clear();
		entities.clear();
	}
	
	public void processEntity(Entity entity)
	{
		TexturedModel entityModel = entity.getModel();
		
		ArrayList<Entity> batch = entities.get(entityModel);
		
		if(batch != null)
		{
			batch.add(entity);
		} else {
			ArrayList<Entity> newBatch = new ArrayList<>();
			newBatch.add(entity);
			
			entities.put(entityModel, newBatch);
		}
	}
	
	public void processTerrain(Terrain terrain)
	{
		terrains.add(terrain);
	}
	
	public void prepare()
	{
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glDepthFunc(GL11.GL_LESS);
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
		GL11.glClearColor(SKY_RED, SKY_GREEN, SKY_BLUE, 1); // R, G, B, ALPHA
	}
	
	private void createProjectionMatrix()
	{
		float fAspectRatio = (float) Display.getWidth() / (float) Display.getHeight();
		float y_scale = (float) ((1f / Math.tan(Math.toRadians(fFov / 2))) * fAspectRatio);
		float x_scale = y_scale / fAspectRatio;
		float frustumLength = fFar - fNear;

		projectionMatrix = new Matrix4f();
		projectionMatrix.m00 = x_scale;
		projectionMatrix.m11 = y_scale;
		projectionMatrix.m22 = -((fFar+fNear) / frustumLength);
		projectionMatrix.m23 = -1f;
		projectionMatrix.m32 = -((2f * fNear * fFar) / frustumLength);
		projectionMatrix.m33 = 0f;
	}
	
	public static void enableCulling()
	{
		GL11.glEnable(GL11.GL_CULL_FACE);
		GL11.glCullFace(GL11.GL_BACK);
	}
	
	public static void disableCulling()
	{
		GL11.glDisable(GL11.GL_CULL_FACE);
	}
	
	public void cleanUp()
	{
		shader.cleanUp();
		//terrainShader.cleanUp();
	}
	
	public Matrix4f getProjectionMatrix()
	{
		return projectionMatrix;
	}
}