package labyrinth.client.renderer;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.client.event.DrawBlockHighlightEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class DrawBlockHighlightEventHandler {
	public boolean drawEraserFrame = false;
	private AxisAlignedBB box = new AxisAlignedBB(0, 0, 0, 0, 0, 0);
	
	@SubscribeEvent
	public void drawBlockSelectionBox(DrawBlockHighlightEvent event) {
		if(!drawEraserFrame)
			return;
		renderBB(event.getPartialTicks());
	}
	
	public void setNewPos(BlockPos from, BlockPos to){
		box = new AxisAlignedBB(from, to);
		box = box.expand(1.02d, 1.02d, 1.02d);
		box = box.expand(-0.02d, -0.02d, -0.02d);
	}
	
	
	public void renderBB(float partialTick) {
		Entity player = Minecraft.getMinecraft().getRenderViewEntity();
		GL11.glEnable(GL11.GL_BLEND);
		OpenGlHelper.glBlendFunc(770, 771, 1, 0);
		GL11.glColor4f(0.0F, 0.0F, 0.0F, 0.8F);
		GL11.glLineWidth(2.0F);
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glDepthMask(false);
		double offsetX = player.lastTickPosX + (player.posX - player.lastTickPosX) * partialTick;
		double offsetY = player.lastTickPosY + (player.posY - player.lastTickPosY) * partialTick;
		double offsetZ = player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * partialTick;
		RenderGlobal.drawSelectionBoundingBox(box.offset(-offsetX, -offsetY, -offsetZ),
					1.0f, 0.0f, 0.0f, 1.0f);
		GL11.glDepthMask(true);
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glDisable(GL11.GL_BLEND);
	}
}
