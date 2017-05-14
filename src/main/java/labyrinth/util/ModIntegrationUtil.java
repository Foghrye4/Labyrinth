package labyrinth.util;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.ResourceLocation;

public class ModIntegrationUtil {
	public static IBlockState getBlockDefaultStateIfNotNull(String name, IBlockState replacement){
		Block block = Block.REGISTRY.getObject(new ResourceLocation(name));
		if(block!=null && block!=Blocks.AIR)
			return block.getDefaultState();
		return replacement;
	}
}
