package toolbox;

import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import entities.Player;
import gameAssets.Chunk;

public class FrustumCulling
{	
	private final float fTheta; // FOV / 2
	private final float VIEW_DISTANCE;
	
	public FrustumCulling(float fFov, float viewDistance)
	{
		this.fTheta = fFov / 2;
		
		this.VIEW_DISTANCE = viewDistance;
	}
	
	public boolean isVisible(Player player, Chunk chunk)
	{
		Vector2f p0 = new Vector2f(player.getPosition().x, player.getPosition().z); // Saves player's position as 2D Vector
		Vector2f p1 = getPosition(p0, fTheta + player.getRotation().y - 90); // Finds the position of the left 'ray' of the player's Field Of View. Subtracting 90 from the player's rotation because the player's rotation is always offset by 90 degrees.
		Vector2f p2 = getPosition(p0, -fTheta + player.getRotation().y - 90); // Finds the position of the right 'ray' of the player's Field Of View. Subtracting 90 from the player's rotation because the player's rotation is always offset by 90 degrees.
		
		Vector2f chunk1 = new Vector2f(chunk.position.x, chunk.position.z); // Saves the chunk's position as 2D Vector.
		Vector2f chunk2 = new Vector2f(chunk1.x, chunk1.y - Chunk.Z_AMOUNT);
		
		if(segmentIntersection(p0, p1, chunk1, chunk2)) return true;
		if(segmentIntersection(p0, p2, chunk1, chunk2)) return true;
		
		Vector2f chunk3 = new Vector2f(chunk1.x + Chunk.X_AMOUNT, chunk1.y);
		
		if(segmentIntersection(p0, p1, chunk1, chunk3)) return true;
		if(segmentIntersection(p0, p2, chunk1, chunk3)) return true;
		
		Vector2f chunk4 = new Vector2f(chunk3.x, chunk2.y);
		
		if(segmentIntersection(p0, p1, chunk2, chunk4)) return true;
		if(segmentIntersection(p0, p2, chunk2, chunk4)) return true;
		
		if(segmentIntersection(p0, p1, chunk3, chunk4)) return true;
		if(segmentIntersection(p0, p2, chunk3, chunk4)) return true;
		
		if(pointInTriangle(chunk1, p0, p1, p2) ||
		   pointInTriangle(chunk2, p0, p1, p2) ||
		   pointInTriangle(chunk3, p0, p1, p2) ||
		   pointInTriangle(chunk4, p0, p1, p2))
		{
			return true;
		}
		
		return false;
	}
	
	private boolean segmentIntersection(Vector2f pos1, Vector2f pos2, Vector2f pos3, Vector2f pos4)
	{
		float x1 = pos1.x, x2 = pos2.x, x3 = pos3.x, x4 = pos4.x;
		float y1 = pos1.y, y2 = pos2.y, y3 = pos3.y, y4 = pos4.y;
		
		float det = (x1 - x2) * (y3 - y4) - (y1 - y2) * (x3 - x4);
		
		float t = ((x1 - x3) * (y3 - y4) - (y1 - y3) * (x3 - x4)) / det;
		
		if(t < 0 || t > 1) return false;
		
		float u = -(((x1 - x2) * (y1 - y3) - (y1 - y2) * (x1 - x3)) / det);
		
		if(u < 0 || u > 1) return false;
		
		return true;
	}
	
	private boolean pointInTriangle(Vector2f pt, Vector2f a, Vector2f b, Vector2f c)
	{
		float w1 = (a.x * (c.y - a.y) + (pt.y - a.y) * (c.x - a.x) - pt.x * (c.y - a.y)) / ((b.y - a.y) * (c.x - a.x) - (b.x - a.x) * (c.y - a.y));
		
		if(w1 < 0) return false;
		
		float w2 = (pt.y - a.y - w1 * (b.y - a.y)) / (c.y - a.y);
		
		if(w2 < 0) return false;
		
		return w1 + w2 <= 1;
	}
	
	private Vector2f getPosition(Vector2f pos, float angle)
	{
		return new Vector2f(VIEW_DISTANCE * (float) Math.cos(Math.toRadians(angle)) + pos.x, VIEW_DISTANCE * (float) Math.sin(Math.toRadians(angle)) + pos.y);
	}
}