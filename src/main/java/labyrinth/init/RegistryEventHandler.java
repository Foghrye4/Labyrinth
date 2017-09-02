package labyrinth.init;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class RegistryEventHandler {
	
	public static final List<Block> blocks = new ArrayList<Block>();
	public static final List<Item> items = new ArrayList<Item>();
	
	@SubscribeEvent
	public void registerBlocks(RegistryEvent.Register<Block> event) {
		for(Block block:blocks)
			event.getRegistry().register(block);
	}
	
	@SubscribeEvent
	public void registerItems(RegistryEvent.Register<Item> event) {
		for(Item item:items)
			event.getRegistry().register(item);
	}

}
