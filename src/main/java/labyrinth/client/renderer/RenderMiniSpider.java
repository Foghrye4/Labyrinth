package labyrinth.client.renderer;

import labyrinth.client.model.ModelMiniSpider;
import labyrinth.entity.EntityMiniSpiderLeveled;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderMiniSpider extends RenderLiving<EntityMiniSpiderLeveled> {

	public RenderMiniSpider(RenderManager renderManagerIn, ModelMiniSpider modelIn) {
		super(renderManagerIn, modelIn, 0.5F);
	}
	
    public void doRender(EntityMiniSpiderLeveled entity, double x, double y, double z, float entityYaw, float partialTicks)
    {
        super.doRender(entity, x, y, z, entityYaw, partialTicks);

    }

	protected float getDeathMaxRotation(EntityMiniSpiderLeveled entityLivingBaseIn) {
		return 180.0F;
	}

	/**
	 * Returns the location of an entity's texture. Doesn't seem to be called
	 * unless you call Render.bindEntityTexture.
	 */
	protected ResourceLocation getEntityTexture(EntityMiniSpiderLeveled entity) {
		return TextureMap.LOCATION_BLOCKS_TEXTURE;
	}
	
    protected void applyRotations(EntityMiniSpiderLeveled entity, float f, float rotationYaw, float partialTicks)
    {
		float roll = entity.prevBodyRoll + (entity.bodyRoll - entity.prevBodyRoll) * partialTicks;
        float pitch = entity.prevBodyPitch + (entity.bodyPitch - entity.prevBodyPitch) * partialTicks;
        GlStateManager.rotate(roll, 0.0F, 0.0F, 1.0F);
        GlStateManager.rotate(pitch, 1.0F, 0.0F, 0.0F);
    	super.applyRotations(entity, f, rotationYaw, partialTicks);
    }

	
	@Override
    public float prepareScale(EntityMiniSpiderLeveled entitylivingbaseIn, float partialTicks)
    {
		super.prepareScale(entitylivingbaseIn, partialTicks);
        return 0.03F;
    }
}
