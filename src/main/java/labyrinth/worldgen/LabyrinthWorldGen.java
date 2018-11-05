package labyrinth.worldgen;

import java.io.IOException;

import io.github.opencubicchunks.cubicchunks.api.util.Coords;
import io.github.opencubicchunks.cubicchunks.api.util.CubePos;
import io.github.opencubicchunks.cubicchunks.api.world.ICube;
import io.github.opencubicchunks.cubicchunks.api.world.ICubicWorld;
import io.github.opencubicchunks.cubicchunks.api.worldgen.populator.event.DecorateCubeBiomeEvent;
import io.github.opencubicchunks.cubicchunks.api.worldgen.populator.event.PopulateCubeEvent;
import labyrinth.noise.ManhattanNoise;
import labyrinth.worldgen.generator.ClaustrophobicCubeStructureGenerator;
import labyrinth.worldgen.generator.LavaCubeStructureGenerator;
import labyrinth.worldgen.generator.RegularCubeStructureGenerator;
import labyrinth.worldgen.generator.VillageCubeStructureGenerator;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos.MutableBlockPos;
import net.minecraft.util.math.BlockPos.PooledMutableBlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.Event.Result;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class LabyrinthWorldGen {

	public static LabyrinthWorldGen instance;
	public LevelsStorage storage;
	public final RegularCubeStructureGenerator basicCubeStructureGenerator = new RegularCubeStructureGenerator(this);
	public final LavaCubeStructureGenerator lavaCubeStructureGenerator = new LavaCubeStructureGenerator(this);
	public final ClaustrophobicCubeStructureGenerator claustrophobicCubeStructureGenerator = new ClaustrophobicCubeStructureGenerator(this);
	public final VillageCubeStructureGenerator villageCubeStructureGenerator = new VillageCubeStructureGenerator(this);
	private DungeonLayer currentLayer;
	
	public LabyrinthWorldGen() throws IOException {
		instance = this;
		storage = new LevelsStorage();
	}
	
	@SubscribeEvent
	public void onWorldLoadEvent(WorldEvent.Load event) throws IOException{
		if(event.getWorld().provider.getDimension()!=0 || event.getWorld().isRemote)
			return;
		for (DungeonCube cube : DungeonCube.values()) {
			if (cube != DungeonCube.NOTHING && cube != DungeonCube.UNDEFINED) {
				cube.load(event.getWorld());
			}
		}
	}

	boolean debug = false;
	ManhattanNoise noise = new ManhattanNoise();
	MutableBlockPos bpos = new MutableBlockPos();
	
	@SubscribeEvent
	public void generate(DecorateCubeBiomeEvent event) {
		CubePos pos = event.getCubePos();
		DungeonCube is = getDungeonCubeType(pos, event.getWorld());
		if (is == DungeonCube.UNDEFINED) {
			throw new IllegalStateException("Dungeon cube type selector return incorrect value.");
		}
		if (is == DungeonCube.NOTHING) {
			return;
		}
		event.setResult(Result.DENY);
	}

	
	@SubscribeEvent
	public void generate(PopulateCubeEvent.Populate event) {
		World world = event.getWorld();
		if(debug) {
			event.setResult(Result.DENY);
			int x = Coords.cubeToMinBlock(event.getCubeX());
			int y = Coords.cubeToMinBlock(event.getCubeY());
			int z = Coords.cubeToMinBlock(event.getCubeZ());
			for(int ix=x;ix<=x+15;ix++)
			for(int iy=y;iy<=y+15;iy++)
			for(int iz=z;iz<=z+15;iz++) {
				bpos.setPos(ix, iy, iz);
				if(noise.canGenerateAt(ix, iy, iz, world)) {
					world.setBlockState(bpos, Blocks.CYAN_GLAZED_TERRACOTTA.getDefaultState());
				}
				else {
					world.setBlockState(bpos, Blocks.AIR.getDefaultState());
				}
			}
			return;
		}
		
		if (world.provider.getDimension() != 0)
			return;
		
		CubePos pos = new CubePos(event.getCubeX(),event.getCubeY(),event.getCubeZ());
		DungeonCube is = getDungeonCubeType(pos, world);
		if (is == DungeonCube.UNDEFINED) {
			throw new IllegalStateException("Dungeon cube type selector return incorrect value.");
		}
		if (is == DungeonCube.NOTHING) {
			return;
		}
		ICubicWorld cworld = (ICubicWorld) world;
		ICube cube = cworld.getCubeCache().getLoadedCube(pos);
		ExtendedBlockStorage cstorage = cube.getStorage();
		if (cstorage == null) {
			cube.setBlockState(cube.getCoords().getCenterBlockPos(), Blocks.AIR.getDefaultState());
			cstorage = cube.getStorage();
		}
		is.placeCube(cube, world, currentLayer);
		event.setResult(Result.DENY);
	}

	public boolean canGenerateAt(CubePos pos, World world) {
		for(DungeonLayer layer:storage.levels) {
			if(layer.isPosInside(pos, world)) {
				return true;
			}
		}
		return false;
	}

	
	public DungeonCube getDungeonCubeType(CubePos pos, World world) {
		for(DungeonLayer layer:storage.levels) {
			if(layer.isPosInside(pos, world)) {
				currentLayer = layer;
				return layer.getGenerator().getDungeonCubeType(pos, world, this);
			}
		}
		return DungeonCube.NOTHING;
	}
}
