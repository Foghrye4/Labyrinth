package labyrinth;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.apache.logging.log4j.Logger;

import labyrinth.command.LGetStructureBlockStateCommand;
import labyrinth.command.LPlaceCubeCommand;
import labyrinth.command.LReplaceCubeFloorCommand;
import labyrinth.command.LWriteCubeCommand;
import labyrinth.entity.EntityZombieLeveled;
import labyrinth.worldgen.LabyrinthWorldGen;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.monster.IMob;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.BiomeDictionary.Type;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;

@Mod(modid = LabyrinthMod.MODID, name = LabyrinthMod.NAME, version = LabyrinthMod.VERSION, dependencies = "required-after:cubicchunks")
public class LabyrinthMod {
	public static final String MODID = "labyrinth";
	public static final String NAME = "Labyrinth";
	public static final String VERSION = "0.002";

	public static Logger log;
	@SidedProxy(clientSide = "labyrinth.ClientProxy", serverSide = "labyrinth.ServerProxy")
	public static ServerProxy proxy;
	@SidedProxy(clientSide = "labyrinth.ClientNetworkHandler", serverSide = "labyrinth.ServerNetworkHandler")
	public static ServerNetworkHandler network;
		
	@EventHandler
	public void preInit(FMLPreInitializationEvent event) throws IOException, IllegalAccessException {
		log = event.getModLog();
		network = new ServerNetworkHandler();
		GameRegistry.registerWorldGenerator(new LabyrinthWorldGen(), 0);
		proxy.preInit();
		for (Biome biome : Biome.EXPLORATION_BIOMES_LIST) {
			biome.getSpawnableList(EnumCreatureType.MONSTER).clear();
		}
		EntityRegistry.registerModEntity(new ResourceLocation(MODID, "zombie.leveled"), EntityZombieLeveled.class, "zombie.leveled", 2, this, 80, 3, true);
	}
	
	@EventHandler
	public void init(FMLPostInitializationEvent event) throws IOException, IllegalAccessException {
		proxy.load();
	}
	
    @EventHandler
    public void serverStart(FMLServerStartingEvent event)
    {
        event.registerServerCommand(new LWriteCubeCommand());
        event.registerServerCommand(new LPlaceCubeCommand());
        event.registerServerCommand(new LGetStructureBlockStateCommand());
        //event.registerServerCommand(new LReplaceCubeFloorCommand());
    }

}
