package labyrinth.worldgen;

import java.io.IOException;

import com.google.gson.JsonSyntaxException;
import com.google.gson.stream.JsonReader;

import io.github.opencubicchunks.cubicchunks.api.util.CubePos;
import labyrinth.noise.INoise;
import labyrinth.noise.ManhattanNoise;
import labyrinth.noise.VillageNoise;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTException;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.world.World;

public class DungeonLayer {
	
	String libraryLootTable = "labyrinth:library_loot_level_0";
	String regularLootTable = "labyrinth:dungeon_loot_level_0";
	public final IBlockState[] mapping;
	
	public int priority = 0;
	public int minX = -1000000000;
	public int maxX = 1000000000;
	public int minZ = -1000000000;
	public int maxZ = 1000000000;
	public int minY = -1000000000;
	public int maxY = 0;
	private ICubeStructureGenerator generator = LabyrinthWorldGen.instance.basicCubeStructureGenerator;
	public INoise noise;
	
	public DungeonLayer() {
		mapping = LevelsStorage.defaultMapping.mapping.clone();
	}

	public DungeonLayer readFromJson(JsonReader reader) throws IOException {
		while (reader.hasNext()) {
			String key = reader.nextName();
			if (key.equals("library_loot_table")) {
				libraryLootTable = reader.nextString();
			} else if (key.equals("regular_loot_table")) {
				regularLootTable = reader.nextString();
			} else if (key.equals("minX")) {
				minX = reader.nextInt();
			} else if (key.equals("maxX")) {
				maxX = reader.nextInt();
			} else if (key.equals("minY")) {
				minY = reader.nextInt();
			} else if (key.equals("maxY")) {
				maxY = reader.nextInt();
			} else if (key.equals("minZ")) {
				minZ = reader.nextInt();
			} else if (key.equals("maxZ")) {
				maxZ = reader.nextInt();
			} else if (key.equals("priority")) {
				priority = reader.nextInt();
			} else if (key.equals("generator_type")) {
				String generatorName = reader.nextString();
				if(generatorName.equalsIgnoreCase("basic")) {
					generator = LabyrinthWorldGen.instance.basicCubeStructureGenerator;
					noise = new ManhattanNoise();
				}
				else if(generatorName.equalsIgnoreCase("lava")) {
					generator = LabyrinthWorldGen.instance.lavaCubeStructureGenerator;
					noise = new ManhattanNoise();
				}
				else if(generatorName.equalsIgnoreCase("village")) {
					generator = LabyrinthWorldGen.instance.villageCubeStructureGenerator;
					noise = new VillageNoise();
				}
				else if(generatorName.equalsIgnoreCase("claustrophobic")) {
					generator = LabyrinthWorldGen.instance.claustrophobicCubeStructureGenerator;
					noise = new ManhattanNoise();
				}
			} else if (key.equals("mapping")) {
				reader.beginObject();
				while (reader.hasNext()) {
					String name = reader.nextName();
					int index = 0;
					if (LevelsStorage.ALIASES.containsKey(name)) {
						index = LevelsStorage.ALIASES.getInt(name);
					} else {
						index = Integer.parseInt(name);
					}
					NBTTagCompound tag;
					try {
						tag = JsonToNBT.getTagFromJson(reader.nextString());
					} catch (NBTException e) {
						throw new JsonSyntaxException(e);
					}
					IBlockState bstate = NBTUtil.readBlockState(tag);
					mapping[index] = bstate;
				}
				reader.endObject();
			}
		}
		return this;
	}

	public boolean isPosInside(CubePos pos, World world) {
		if(minX<=pos.getX() && pos.getX()<maxX 
			&& minY<=pos.getY() && pos.getY()<maxY 
			&& minZ<=pos.getZ() && pos.getZ()<maxZ) {
			boolean canGenerate = noise.canGenerateAt(pos, world);
			return canGenerate;
		}
		return false;
	}

	public ICubeStructureGenerator getGenerator() {
		return generator;
	}
}
