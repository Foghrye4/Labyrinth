package labyrinth;

import java.io.File;

import labyrinth.block.BlockStoneTile;
import labyrinth.init.LabyrinthBlocks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
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
	}

	@Override
	public File getMinecraftDir() {
		return Minecraft.getMinecraft().mcDataDir;
	}
	
	@Override
	public void preInit() {
	}

}
