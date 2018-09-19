package labyrinth.client.model;

import labyrinth.client.Icon;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.obj.OBJLoader;
import net.minecraftforge.client.model.pipeline.LightUtil;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ObjModelBoxExtended extends ModelBox {

	private final ResourceLocation modelLocation;
	private IBakedModel bakedModel;
	private Icon icon;
	public final float dx;
	public final float dy;
	public final float dz;
	
	
	public ObjModelBoxExtended(ResourceLocation modelLocationIn, ModelRenderer renderer, float x, float y, float z, int dxIn, int dyIn, int dzIn, float delta) {
		super(renderer, 0, 0, x, y, z, dxIn, dyIn, dzIn, delta);
		modelLocation = modelLocationIn;
		dx=dxIn;
		dy=dyIn;
		dz=dzIn;
	}

	public void bake(final Icon iconIn) {
		icon = iconIn;
		try {
			IModel model = OBJLoader.INSTANCE.loadModel(modelLocation);
			bakedModel = model.bake(model.getDefaultState(), DefaultVertexFormats.ITEM,
					this::textureGetter);;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private TextureAtlasSprite textureGetter(ResourceLocation location){
		return icon.getTextureAtlasSprite();
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void render(BufferBuilder bufferbuilder, float scale) {
		GlStateManager.pushMatrix();
		GlStateManager.translate(posX1 * scale, posY1 * scale, posZ1 * scale);
		GlStateManager.scale(-dx * scale, -dy * scale, dz * scale);
		Tessellator tessellator = Tessellator.getInstance();
		bufferbuilder.begin(7, DefaultVertexFormats.ITEM);
		for (BakedQuad quad : this.bakedModel.getQuads(null, null, 0L)) {
			LightUtil.renderQuadColor(bufferbuilder, quad, -1);
		}
		tessellator.draw();
		GlStateManager.popMatrix();
	}
}
