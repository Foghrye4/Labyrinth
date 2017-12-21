package labyrinth.init;

import labyrinth.LabyrinthMod;
import labyrinth.block.BlockStoneTile;
import labyrinth.block.BlockVillageMarket;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemMultiTexture;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class LabyrinthBlocks {
	public static Block STONE;
	public static Block COUNTER;

	public static void init() {
		STONE = (new BlockStoneTile()).setHardness(1.5F).setResistance(10.0F).setUnlocalizedName("stone")
				.setRegistryName(LabyrinthMod.MODID, "stone").setCreativeTab(null);
		COUNTER = (new BlockVillageMarket(Material.WOOD)).setHardness(0.5F).setResistance(5.0F).setUnlocalizedName("counter")
				.setRegistryName(LabyrinthMod.MODID, "counter").setCreativeTab(CreativeTabs.DECORATIONS);
	}

	public static void register() {
		registerBlock(STONE, (new ItemMultiTexture(STONE, STONE, new ItemMultiTexture.Mapper() {
			public String apply(ItemStack stack) {
				return BlockStoneTile.EnumType.byMetadata(stack.getMetadata()).getUnlocalizedName();
			}
		})).setUnlocalizedName("stone"));
		registerBlock(COUNTER, (new ItemBlock(COUNTER)).setUnlocalizedName("counter"));
	}

	private static void registerBlock(Block block, Item item) {
		RegistryEventHandler.blocks.add(block);
		item.setRegistryName(block.getRegistryName());
		RegistryEventHandler.items.add(item);
	}

	public static void registerRenders() {
		for (BlockStoneTile.EnumType type : BlockStoneTile.EnumType.values()) {
			registerRender(STONE, type.getMetadata(), new ResourceLocation(LabyrinthMod.MODID, type.getName()));
		}
		registerRender(COUNTER, 0, COUNTER.getRegistryName());
	}

	private static void registerRender(Block block, int metadata, ResourceLocation modelResourceLocation) {
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(block), metadata,
				new ModelResourceLocation(modelResourceLocation, "inventory"));
	}
}
