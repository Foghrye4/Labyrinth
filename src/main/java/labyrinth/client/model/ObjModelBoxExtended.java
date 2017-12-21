package labyrinth.client.model;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.model.obj.OBJLoader;
import net.minecraftforge.client.model.pipeline.LightUtil;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ObjModelBoxExtended extends ModelBox {

	private final ResourceLocation modelLocation;
	private IBakedModel bakedModel;
	public ObjModelBoxExtended(ResourceLocation modelLocationIn, ModelRenderer renderer, float x, float y, float z, int dx, int dy, int dz, float delta) {
		super(renderer, 0, 0, x, y, z, dx, dy, dz, delta);
		modelLocation = modelLocationIn;
	}

	public void bake() {
		try {
			IModel model = OBJLoader.INSTANCE.loadModel(modelLocation);
			bakedModel = model.bake(model.getDefaultState(), DefaultVertexFormats.ITEM,
					ModelLoader.defaultTextureGetter());;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void render(BufferBuilder bufferbuilder, float scale) {
		GlStateManager.pushMatrix();
		GlStateManager.translate(posX1 * scale, posY1 * scale, -posZ1 * scale);
		float dx = posX2 - posX1;
		float dy = posY2 - posY1;
		float dz = posZ2 - posZ1;
		GlStateManager.scale(dx * scale, dy * scale, dz * scale);
		Tessellator tessellator = Tessellator.getInstance();
		bufferbuilder.begin(7, DefaultVertexFormats.ITEM);
		for (BakedQuad quad : this.bakedModel.getQuads(null, null, 0L)) {
			bufferbuilder.addVertexData(quad.getVertexData());
		}
		tessellator.draw();
		GlStateManager.popMatrix();
	}
}
