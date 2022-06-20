package textures;

public class ModelTexture
{
	private int textureID;
	
	private float shineDamper = 1f;
	private float reflectivity = 0f;
	
	private boolean hasTransparency = false;
	private boolean useFakeLighting = false;
	
	public ModelTexture(int textureID)
	{
		this.textureID = textureID;
	}
	
	public int getTextureID()
	{
		return textureID;
	}
	
	public float getShineDamper()
	{
		return shineDamper;
	}
	
	public float getReflectivity()
	{
		return reflectivity;
	}
	
	public void setShineDamper(float shineDamper)
	{
		this.shineDamper = shineDamper;
	}
	
	public void setReflectivity(float reflectivity)
	{
		this.reflectivity = reflectivity;
	}
	
	public void setTransparency(boolean hasTransparency)
	{
		this.hasTransparency = hasTransparency;
	}
	
	public boolean getTransparency()
	{
		return hasTransparency;
	}
	
	public void setUseFakeLighting(boolean useFakeLighting)
	{
		this.useFakeLighting = useFakeLighting;
	}
	
	public boolean getUseFakeLighting()
	{
		return useFakeLighting;
	}
}