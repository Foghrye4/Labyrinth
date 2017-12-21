package labyrinth.client.renderer;

import labyrinth.client.model.ModelMiniSpider;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.monster.EntitySpider;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderMiniSpider<T extends EntitySpider> extends RenderLiving<T> {
	private static final ResourceLocation SPIDER_TEXTURES = new ResourceLocation("textures/entity/spider/spider.png");
	private ModelMiniSpider model;

	public RenderMiniSpider(RenderManager renderManagerIn, ModelMiniSpider modelIn) {
		super(renderManagerIn, modelIn, 0.5F);
		model = modelIn;
	}
	
    public void doRender(T entity, double x, double y, double z, float entityYaw, float partialTicks)
    {
        super.doRender(entity, x, y, z, entityYaw, partialTicks);
        GlStateManager.pushMatrix();
        this.renderLivingAt(entity, x, y, z);
        float f = 0.0625F;
        model.testMesh.render(f);
        GlStateManager.popMatrix();

    }

	protected float getDeathMaxRotation(T entityLivingBaseIn) {
		return 180.0F;
	}

	/**
	 * Returns the location of an entity's texture. Doesn't seem to be called
	 * unless you call Render.bindEntityTexture.
	 */
	protected ResourceLocation getEntityTexture(T entity) {
		return SPIDER_TEXTURES;
	}
}
