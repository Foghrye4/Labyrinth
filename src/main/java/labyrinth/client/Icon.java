package labyrinth.client;

import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;

/** Texture Atlas Sprite Wrapper **/
@SideOnly(value = Side.CLIENT)
public class Icon {
	private TextureAtlasSprite sprite;
	private ResourceLocation location;

	public Icon(ResourceLocation locationIn) {
		this.location = locationIn;
	}

	public void registerIcon(TextureMap map) {
		sprite = map.registerSprite(location);
	}

	public ResourceLocation getLocation() {
		return this.location;
	}

	public float getMinU() {
		return sprite.getMinU();
	}

	public float getMinV() {
		return sprite.getMinV();
	}

	public float getMaxU() {
		return sprite.getMaxU();
	}

	public float getMaxV() {
		return sprite.getMaxV();
	}

	public int getOriginX() {
		return sprite.getOriginX();
	}

	public int getOriginY() {
		return sprite.getOriginY();
	}

	public int getIconHeight() {
		return sprite.getIconHeight();
	}

	public int getIconWidth() {
		return sprite.getIconWidth();
	}
}
