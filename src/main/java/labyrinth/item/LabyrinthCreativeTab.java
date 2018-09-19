package labyrinth.item;

import labyrinth.init.LabyrinthItems;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;

public class LabyrinthCreativeTab extends CreativeTabs {

	public LabyrinthCreativeTab(String label) {
		super(label);
	}

	@Override
	public ItemStack getTabIconItem() {
		return new ItemStack(LabyrinthItems.BLOCK_FILLER, 1, 0);
	}
}
