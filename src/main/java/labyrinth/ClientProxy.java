package labyrinth;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import labyrinth.block.BlockStoneTile;
import labyrinth.client.renderer.RenderEntityEraserFrame;
import labyrinth.entity.EntityEraserFrame;
import labyrinth.init.LabyrinthBlocks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(value = Side.CLIENT)
public class ClientProxy extends ServerProxy {

	@Override
	public void load() {
		LabyrinthBlocks.registerRenders();
		for(BlockStoneTile.EnumType type:BlockStoneTile.EnumType.values()){
			ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(LabyrinthBlocks.STONE), type.getMetadata(), new ModelResourceLocation(new ResourceLocation(LabyrinthMod.MODID,type.getName()), "inventory"));
		}
		Minecraft.getMinecraft().getRenderManager().entityRenderMap.put(EntityEraserFrame.class, new RenderEntityEraserFrame(Minecraft.getMinecraft().getRenderManager()));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(LabyrinthMod.eraser, 0,
				new ModelResourceLocation(new ResourceLocation(LabyrinthMod.MODID,"eraser"), "inventory"));
	}

	@Override
	public File getMinecraftDir() {
		return Minecraft.getMinecraft().mcDataDir;
	}
	
	@Override
	public void preInit() {
	}
	
	@Override
	public InputStream getResourceInputStream(ResourceLocation location) throws IOException {
		return Minecraft.getMinecraft().getResourceManager().getResource(location).getInputStream();
	}


}
