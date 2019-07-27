package labyrinth.worldgen;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.JsonParseException;
import com.google.gson.stream.JsonReader;

import io.github.opencubicchunks.cubicchunks.api.util.CubePos;
import io.github.opencubicchunks.cubicchunks.api.world.ICube;
import io.github.opencubicchunks.cubicchunks.api.world.ICubicWorld;
import labyrinth.light.LightPropagator;
import labyrinth.noise.INoise;
import labyrinth.noise.ManhattanNoise;
import labyrinth.noise.SolidNoNoise;
import labyrinth.noise.VillageNoise;
import labyrinth.worldgen.generator.Decorator;
import labyrinth.worldgen.generator.SingleCubeGenerator;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTException;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.NibbleArray;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;

public class DungeonLayer {
	
	String libraryLootTable = "labyrinth:library_loot_level_0";
	String regularLootTable = "labyrinth:dungeon_loot_level_0";
	public final IBlockState[] mapping;
	
	public int priority = 0;
	private int minX = -1000000000;
	private int maxX = 1000000000;
	private int minZ = -1000000000;
	private int maxZ = 1000000000;
	private int minY = -1000000000;
	private int maxY = 0;
	private ICubeStructureGenerator generator = LabyrinthWorldGen.instance.basicCubeStructureGenerator;
	private INoise noise = new SolidNoNoise();
	private Map<DungeonCube, byte[]> lightCache = new HashMap<DungeonCube, byte[]>();
	private List<Decorator> decorators = new ArrayList<Decorator>();
	
	public DungeonLayer() {
		mapping = LevelsStorage.defaultMapping.mapping.clone();
	}

	public DungeonLayer readFromJson(JsonReader reader) throws IOException, NBTException {
		DungeonCube monotonicCubeType = null;
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
			} else if (key.equals("monotonic_cube")) {
				monotonicCubeType = this.getCubeByName(reader.nextString());
			} else if (key.equals("generator_type")) {
				String generatorName = reader.nextString();
				if(generatorName.equalsIgnoreCase("basic")) {
					generator = LabyrinthWorldGen.instance.basicCubeStructureGenerator;
				}
				else if(generatorName.equalsIgnoreCase("lava")) {
					generator = LabyrinthWorldGen.instance.lavaCubeStructureGenerator;
				}
				else if(generatorName.equalsIgnoreCase("village")) {
					generator = LabyrinthWorldGen.instance.villageCubeStructureGenerator;
				}
				else if(generatorName.equalsIgnoreCase("claustrophobic")) {
					generator = LabyrinthWorldGen.instance.claustrophobicCubeStructureGenerator;
				}
				else if(generatorName.equalsIgnoreCase("monolith")) {
					generator = LabyrinthWorldGen.instance.monolithCubeStructureGenerator;
				}
				else if(generatorName.equalsIgnoreCase("monotonic")) {
					generator = new SingleCubeGenerator();
				}
			} else if (key.equals("noise_type")) {
				String noiseName = reader.nextString();
				if(noiseName.equalsIgnoreCase("solid")) {
					noise = new SolidNoNoise();
				}
				else if(noiseName.equalsIgnoreCase("cell")) {
					noise = new ManhattanNoise();
				}
				else if(noiseName.equalsIgnoreCase("village")) {
					noise = new VillageNoise();
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
					NBTTagCompound tag = JsonToNBT.getTagFromJson(reader.nextString());
					IBlockState bstate = NBTUtil.readBlockState(tag);
					mapping[index] = bstate;
				}
				reader.endObject();
			}
			else if (key.equals("decorators")) {
				reader.beginArray();
				while (reader.hasNext()) {
					reader.beginObject();
					Decorator decorator = new Decorator();
					while (reader.hasNext()) {
						String name = reader.nextName();
						if(name.equals("chance")) {
							decorator.chance = (float) reader.nextDouble();
						}
						else if(name.equals("nbt")) {
							decorator.nbt = JsonToNBT.getTagFromJson(reader.nextString());
						}
						else if(name.equals("blockstate")) {
							NBTTagCompound tag = JsonToNBT.getTagFromJson(reader.nextString());
							decorator.state = NBTUtil.readBlockState(tag);
						}
						else if(name.equals("attach_to_floor")) {
							decorator.attachToFloor = reader.nextBoolean();
						}
						else if(name.equals("attach_to_ceiling")) {
							decorator.attachToCeiling = reader.nextBoolean();
						}
						else if(name.equals("attach_to_north_wall")) {
							decorator.attachToNorthWall = reader.nextBoolean();
						}
						else if(name.equals("attach_to_south_wall")) {
							decorator.attachToSouthWall = reader.nextBoolean();
						}
						else if(name.equals("attach_to_east_wall")) {
							decorator.attachToEastWall = reader.nextBoolean();
						}
						else if(name.equals("attach_to_west_wall")) {
							decorator.attachToWestWall = reader.nextBoolean();
						}
						else if(name.equals("can_attach_to_itself")) {
							decorator.canAttachToItself = reader.nextBoolean();
						}
						else if(name.equals("replace_element")) {
							String elemenet = reader.nextString();
							int index = 0;
							if (LevelsStorage.ALIASES.containsKey(elemenet)) {
								index = LevelsStorage.ALIASES.getInt(elemenet);
							} else {
								index = Integer.parseInt(elemenet);
							}
							decorator.replaceBlock = index;
						}
						else if(name.equals("cluster_size")) {
							decorator.clusterSize = reader.nextInt();
						}
						else {
							reader.skipValue();
						}
					}
					decorators.add(decorator);
					reader.endObject();
				}
				reader.endArray();
			}
		}
		if(generator instanceof SingleCubeGenerator) {
			((SingleCubeGenerator) generator).setCube(monotonicCubeType);
		}
		if(minX==maxX || minY==maxY || minY==maxY) {
			throw new JsonParseException("Layer 'max' and 'min' borders should not be equal! "+toString());
		}
		return this;
	}

	private DungeonCube getCubeByName(String name) {
		if (!name.endsWith(".cube_structure"))
			name = name + ".cube_structure";
		for (DungeonCube cube : DungeonCube.values) {
			if (cube.name.equalsIgnoreCase(name))
				return cube;
		}
		return new DungeonCube(name);
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
	
	public void doInitialBlockLighting(ICubicWorld cworld, ICube cube, DungeonCube is) {
		byte[] lightData = this.lightCache.get(is);
		if(lightData == null) {
			lightData = this.precalculateLight(is);
			this.lightCache.put(is, lightData);
		}
		ExtendedBlockStorage cstorage = cube.getStorage();
		NibbleArray blockLight = cstorage.getBlockLight();
		byte[] blockLightData = blockLight.getData();
		System.arraycopy(lightData, 0, blockLightData, 0, lightData.length);
		LightPropagator.spreadLight(cube, cworld);
		int[] xyz = new int[] {0,0,-1,0,0,1,0,0};
		for(int i=2;i<xyz.length;i++) {
			CubePos cubePos = cube.getCoords();
			int cubePosX = cubePos.getX()+xyz[i-2];
			int cubePosY = cubePos.getY()+xyz[i-1];
			int cubePosZ = cubePos.getZ()+xyz[i];
			ICube cube1 = cworld.getCubeCache().getLoadedCube(cubePosX, cubePosY, cubePosZ);
			if (cube1 == null) {
				LightPropagator.schleduledLightPropagate.add(new CubePos(cubePosX, cubePosY, cubePosZ));
				continue;
			}
			if(cube1.getStorage() == null)
				continue;
			LightPropagator.spreadLight(cube1, cworld);
		}
	}
	
	@SuppressWarnings("deprecation")
	byte[] precalculateLight(DungeonCube is) {
		byte[] data = is.data;
		byte[] lightData = new byte[2048];
		NibbleArray lightNibbleArray = new NibbleArray(lightData);
		for (int index = 0; index < data.length; index++) {
			int bstateI = Byte.toUnsignedInt(data[index]);
			IBlockState bstate = mapping[bstateI];
			if (bstate.getLightValue() > 0) {
				int dx = index >>> 8;
				int dy = (index >>> 4) & 15;
				int dz = index & 15;
				setLight(new BlockPos(dx, dy, dz), lightNibbleArray, bstate.getLightValue(), data);
			}
		}
		return lightData;
	}
	
	@SuppressWarnings("deprecation")
	private void setLight(BlockPos lightPos, NibbleArray lightNibbleArray, int lightValue, byte[] data) {
		int index = lightPos.getX()<<8|lightPos.getY()<<4|lightPos.getZ();
		if(index<0 || 
				index>=4096 || 
				lightValue<=0 ||
				lightPos.getX() < 0 ||
				lightPos.getY() < 0 ||
				lightPos.getZ() < 0 ||
				lightPos.getX() > 15 ||
				lightPos.getY() > 15 ||
				lightPos.getZ() > 15){
			return;
		}
		int bstateI = Byte.toUnsignedInt(data[index]);
		IBlockState bstate = mapping[bstateI];
		if (bstate.getLightValue()==0 && bstate.getLightOpacity() >= 255) {
			return;
		}
		if(lightNibbleArray.get(lightPos.getX(), lightPos.getY(), lightPos.getZ()) < lightValue){
			lightNibbleArray.set(lightPos.getX(), lightPos.getY(), lightPos.getZ(),lightValue);
			setLight(lightPos.up(), lightNibbleArray, lightValue-1, data);
			setLight(lightPos.down(), lightNibbleArray, lightValue-1, data);
			setLight(lightPos.north(), lightNibbleArray, lightValue-1, data);
			setLight(lightPos.south(), lightNibbleArray, lightValue-1, data);
			setLight(lightPos.west(), lightNibbleArray, lightValue-1, data);
			setLight(lightPos.east(), lightNibbleArray, lightValue-1, data);
		}
	}

	public void decorate(ICube cube, DungeonCube is) {
		if(decorators.size()==0)
			return;
		decorators.get(cube.getWorld().rand.nextInt(decorators.size())).decorateWithChance(cube, is);
	}
	
	@Override
	public String toString() {
		return "DungeonLayer:{"+minX+";"+minY+";"+minZ+"}{"+maxX+";"+maxY+";"+maxZ+"}";
	}
}
