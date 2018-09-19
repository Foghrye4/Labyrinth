package labyrinth.client.model;

import labyrinth.client.Icon;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ObjModelRenderer extends ModelRenderer {

	public ObjModelRenderer(ModelBase model) {
		super(model);
	}

	public void bake(final Icon iconIn) {
		for (ModelBox box : this.cubeList) {
			if (box instanceof ObjModelBoxExtended) {
				ObjModelBoxExtended boxe = (ObjModelBoxExtended) box;
				boxe.bake(iconIn);
			}
		}
	}

	public ModelRenderer addObjModel(ResourceLocation modelLocation, float offX, float offY, float offZ, int width, int height, int depth, float f) {
		this.cubeList.add(new ObjModelBoxExtended(modelLocation, this, offX, offY, offZ, width, height, depth, f));
		return this;
	}
}
