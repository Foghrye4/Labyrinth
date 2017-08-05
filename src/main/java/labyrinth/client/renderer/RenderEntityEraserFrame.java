package labyrinth.client.renderer;

import labyrinth.LabyrinthMod;
import labyrinth.entity.EntityEraserFrame;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;

public class RenderEntityEraserFrame extends Render<EntityEraserFrame>{

	ResourceLocation texture = new ResourceLocation(LabyrinthMod.MODID, "blocks/steel");
	private Minecraft mc;
	private EntityPlayerSP player;
	private double renderPosY;
	private double renderPosX;
	private double renderPosZ;
	private int frame = Integer.MIN_VALUE;
			
	public RenderEntityEraserFrame(RenderManager renderManager) {
		super(renderManager);
		mc = Minecraft.getMinecraft();
	}
	
	@Override
    public void doRender(EntityEraserFrame entity, double x, double y, double z, float entityYaw, float partialTicks)
    {
		frame++;
        this.player = this.mc.player;
        this.renderPosX = this.player.lastTickPosX + (this.player.posX - this.player.lastTickPosX) * (double)partialTicks;
        this.renderPosY = this.player.lastTickPosY + (this.player.posY - this.player.lastTickPosY) * (double)partialTicks;
        this.renderPosZ = this.player.lastTickPosZ + (this.player.posZ - this.player.lastTickPosZ) * (double)partialTicks;
		AxisAlignedBB axisalignedbb = entity.getEntityBoundingBox();
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        GlStateManager.glLineWidth(6.0F);
        GlStateManager.disableTexture2D();
        GlStateManager.depthMask(false);

        RenderGlobal.drawSelectionBoundingBox(axisalignedbb.expandXyz(0.002D).offset(-this.renderPosX, -this.renderPosY, -this.renderPosZ), (frame & 255)/255f, (frame+64 & 255)/255f, (frame+128 & 255)/255f, 1.0F);
        
        GlStateManager.depthMask(true);
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
    }

	@Override
	protected ResourceLocation getEntityTexture(EntityEraserFrame entity) {
		return texture;
	}
}
