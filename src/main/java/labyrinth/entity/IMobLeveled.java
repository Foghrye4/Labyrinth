package labyrinth.entity;

import net.minecraft.util.ResourceLocation;

public interface IMobLeveled {
	void setLevel(int levelIn);
	void setLootTable(ResourceLocation lootTable);
}
