package labyrinth;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import labyrinth.block.BlockStoneTile;
import labyrinth.client.renderer.RenderEntityEraserFrame;
import labyrinth.client.renderer.SpecialRendererRegistry;
import labyrinth.client.renderer.TileEntityVillageMarketRenderer;
import labyrinth.entity.EntityEraserFrame;
import labyrinth.init.LabyrinthBlocks;
import labyrinth.tileentity.TileEntityVillageMarket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(value = Side.CLIENT)
public class ClientProxy extends ServerProxy {
	
	private ClientNetworkHandler networkHandler = new ClientNetworkHandler();

	@Override
	public void load() {
		MinecraftForge.EVENT_BUS.register(SpecialRendererRegistry.instance);
		LabyrinthBlocks.registerRenders();
		for(BlockStoneTile.EnumType type:BlockStoneTile.EnumType.values()){
			ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(LabyrinthBlocks.STONE), type.getMetadata(), new ModelResourceLocation(new ResourceLocation(LabyrinthMod.MODID,type.getName()), "inventory"));
		}
		Minecraft.getMinecraft().getRenderManager().entityRenderMap.put(EntityEraserFrame.class, new RenderEntityEraserFrame(Minecraft.getMinecraft().getRenderManager()));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(LabyrinthMod.eraser, 0,
				new ModelResourceLocation(new ResourceLocation(LabyrinthMod.MODID,"eraser"), "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(LabyrinthMod.blockFiller, 0,
				new ModelResourceLocation(new ResourceLocation(LabyrinthMod.MODID,"block_filler"), "inventory"));
		
		TileEntityVillageMarketRenderer vmr = new TileEntityVillageMarketRenderer();
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityVillageMarket.class, vmr);
		MinecraftForge.EVENT_BUS.register(vmr);
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
	
	@Override
	public ServerNetworkHandler getNetwork() {
		return networkHandler;
	}
}
