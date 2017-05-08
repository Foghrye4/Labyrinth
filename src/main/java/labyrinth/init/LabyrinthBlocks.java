package labyrinth.init;

import labyrinth.LabyrinthMod;
import labyrinth.block.BlockStoneTile;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraft.item.ItemMultiTexture;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class LabyrinthBlocks {
	public static Block STONE;

	public static void init() {
		STONE = (new BlockStoneTile()).setHardness(1.5F).setResistance(10.0F).setUnlocalizedName("stone")
				.setRegistryName(LabyrinthMod.MODID, "stone");
	}

	public static void register() {
		registerBlock(STONE, (new ItemMultiTexture(STONE, STONE, new ItemMultiTexture.Mapper() {
			public String apply(ItemStack stack) {
				return BlockStoneTile.EnumType.byMetadata(stack.getMetadata()).getUnlocalizedName();
			}
		})).setUnlocalizedName("stone"));

	}

	private static void registerBlock(Block block) {
		registerBlock(block, new ItemBlock(block));
	}

	private static void registerBlock(Block block, Item item) {
		GameRegistry.register(block);
		item.setRegistryName(block.getRegistryName());
		GameRegistry.register(item);
	}

	public static void registerRenders() {
		registerRender(STONE);
	}

	private static void registerRender(Block block) {
		for(BlockStoneTile.EnumType type:BlockStoneTile.EnumType.values()){
			Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(block), type.getMetadata(),
					new ModelResourceLocation(new ResourceLocation(LabyrinthMod.MODID,type.getName()), "inventory"));
		}
	}
}
