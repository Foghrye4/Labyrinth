package labyrinth;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import labyrinth.entity.EntityZombieLeveled;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.BiomeDictionary.Type;
import net.minecraftforge.fml.common.registry.EntityRegistry;

public class ServerProxy {
	public void load() {

	}

	public File getMinecraftDir() {
		return new File(".");
	}

	public void preInit() {
	}
}
