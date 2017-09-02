package labyrinth;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import labyrinth.block.BlockStoneTile;
import labyrinth.client.renderer.RenderEntityEraserFrame;
import labyrinth.client.renderer.SpecialRendererRegistry;
import labyrinth.client.renderer.TileEntityVillageMarketRenderer;
import labyrinth.entity.EntityEraserFrame;
import labyrinth.init.LabyrinthBlocks;
import labyrinth.init.LabyrinthItems;
import labyrinth.tileentity.TileEntityVillageMarket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;

public class ClientProxy extends ServerProxy {
	
	private ClientNetworkHandler networkHandler = new ClientNetworkHandler();
	TileEntityVillageMarketRenderer vmr = new TileEntityVillageMarketRenderer();
	
	@Override
	public void load() {
		LabyrinthBlocks.registerRenders();
		LabyrinthItems.registerRenders();
		for(BlockStoneTile.EnumType type:BlockStoneTile.EnumType.values()){
			ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(LabyrinthBlocks.STONE), type.getMetadata(), new ModelResourceLocation(new ResourceLocation(LabyrinthMod.MODID,type.getName()), "inventory"));
		}
		Minecraft.getMinecraft().getRenderManager().entityRenderMap.put(EntityEraserFrame.class, new RenderEntityEraserFrame(Minecraft.getMinecraft().getRenderManager()));
		vmr.setRenders();
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityVillageMarket.class, vmr);
	}

	@Override
	public File getMinecraftDir() {
		return Minecraft.getMinecraft().mcDataDir;
	}
	
	@Override
	public void preInit() {
		MinecraftForge.EVENT_BUS.register(SpecialRendererRegistry.instance);
		MinecraftForge.EVENT_BUS.register(vmr);
	}
	
	@Override
	public InputStream getResourceInputStream(ResourceLocation location) throws IOException {
		return Minecraft.getMinecraft().getResourceManager().getResource(location).getInputStream();
	}
	
	@Override
	public ServerNetworkHandler getNetwork() {
		return networkHandler;
	}
}
