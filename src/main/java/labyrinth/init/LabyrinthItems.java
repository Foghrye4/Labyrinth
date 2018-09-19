package labyrinth.init;

import static labyrinth.LabyrinthMod.MODID;

import labyrinth.LabyrinthMod;
import labyrinth.item.ItemBlockFiller;
import labyrinth.item.ItemEraser;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;

public class LabyrinthItems {
	public static Item ERASER;
	public static Item BLOCK_FILLER;

	public static void init() {
		ERASER = new ItemEraser();
		ERASER.setCreativeTab(LabyrinthMod.creativeTab);
		ERASER.setUnlocalizedName("ERASER");
		ERASER.setRegistryName(MODID, "ERASER");
		BLOCK_FILLER = new ItemBlockFiller();
		BLOCK_FILLER.setCreativeTab(LabyrinthMod.creativeTab);
		BLOCK_FILLER.setUnlocalizedName("block_filler");
		BLOCK_FILLER.setRegistryName(MODID, "block_filler");
	}

	public static void register() {
		registerItem(ERASER);
		registerItem(BLOCK_FILLER);
	}

	private static void registerItem(Item item) {
		RegistryEventHandler.items.add(item);
	}

	public static void registerRenders() {
		registerRender(LabyrinthItems.ERASER, 0,
				new ModelResourceLocation(new ResourceLocation(LabyrinthMod.MODID,"eraser"), "inventory"));
		registerRender(LabyrinthItems.BLOCK_FILLER, 0,
				new ModelResourceLocation(new ResourceLocation(LabyrinthMod.MODID,"block_filler"), "inventory"));
	}
	
	private static void registerRender(Item item, int metadata, ModelResourceLocation modelResourceLocation) {
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(item, metadata,
				modelResourceLocation);
	}

}
