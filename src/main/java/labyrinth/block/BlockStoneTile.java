package labyrinth.block;

import net.minecraft.block.BlockStone;
import net.minecraft.block.SoundType;

/**
 * Class to replace vanilla stone and prevent Minecraft and other mods to
 * generate ores in it.
 **/
public class BlockStoneTile extends BlockStone {
	public BlockStoneTile(){
		setSoundType(SoundType.STONE);
	}
}
