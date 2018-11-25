package labyrinth.worldgen;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.JsonSyntaxException;
import com.google.gson.stream.JsonReader;

import io.github.opencubicchunks.cubicchunks.api.util.CubePos;
import io.github.opencubicchunks.cubicchunks.api.world.ICube;
import io.github.opencubicchunks.cubicchunks.api.world.ICubicWorld;
import labyrinth.noise.INoise;
import labyrinth.noise.ManhattanNoise;
import labyrinth.noise.SolidNoNoise;
import labyrinth.noise.VillageNoise;
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
	public int minX = -1000000000;
	public int maxX = 1000000000;
	public int minZ = -1000000000;
	public int maxZ = 1000000000;
	public int minY = -1000000000;
	public int maxY = 0;
	private ICubeStructureGenerator generator = LabyrinthWorldGen.instance.basicCubeStructureGenerator;
	private INoise noise = new SolidNoNoise();
	private Map<DungeonCube, byte[]> lightCache = new HashMap<DungeonCube, byte[]>();
	
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
		this.spreadLight(cube, cworld);
		int[] xyz = new int[] {0,0,-1,0,0,1,0,0};
		for(int i=2;i<xyz.length;i++) {
			CubePos cubePos = cube.getCoords();
			int cubePosX = cubePos.getX()+xyz[i-2];
			int cubePosY = cubePos.getY()+xyz[i-1];
			int cubePosZ = cubePos.getZ()+xyz[i];
			ICube cube1 = cworld.getCubeCache().getLoadedCube(cubePosX, cubePosY, cubePosZ);
			if (cube1 == null || cube1.getStorage() == null)
				continue;
			this.spreadLight(cube1, cworld);
		}
	}
	
	private void spreadLight(ICube cube, ICubicWorld cworld) {
		ExtendedBlockStorage cstorage = cube.getStorage();
		NibbleArray blockLight = cstorage.getBlockLight();
		CubePos cubePos = cube.getCoords();
		int cubePosX = cubePos.getX();
		int cubePosY = cubePos.getY();
		int cubePosZ = cubePos.getZ();
		ICube cubeNX = cworld.getCubeCache().getLoadedCube(cubePosX-1, cubePosY, cubePosZ);
		if (cubeNX != null && cubeNX.getStorage() != null) {
			NibbleArray blockLightNX = cubeNX.getStorage().getBlockLight();
			for(int iy=0;iy<16;iy++) {
				for(int iz=0;iz<16;iz++) {
					int blnx = blockLightNX.get(15, iy, iz);
					int bl = blockLight.get(0, iy, iz);
					if (blnx - bl >= 2) {
						this.setLight(new BlockPos(0, iy, iz), blockLight, blnx-1, cstorage);
					}
					else if (bl - blnx >= 2) {
						this.setLight(new BlockPos(15, iy, iz), blockLightNX, bl-1, cstorage);
					}
				}
			}
		}
		ICube cubePX = cworld.getCubeCache().getLoadedCube(cubePosX+1, cubePosY, cubePosZ);
		if (cubePX != null && cubePX.getStorage() != null) {
			NibbleArray blockLightPX = cubePX.getStorage().getBlockLight();
			for(int iy=0;iy<16;iy++) {
				for(int iz=0;iz<16;iz++) {
					int blpx = blockLightPX.get(15, iy, iz);
					int bl = blockLight.get(0, iy, iz);
					if (blpx - bl >= 2) {
						this.setLight(new BlockPos(15, iy, iz), blockLight, blpx-1, cstorage);
					}
					else if (bl - blpx >= 2) {
						this.setLight(new BlockPos(0, iy, iz), blockLightPX, bl-1, cstorage);
					}
				}
			}
		}
		ICube cubeNY = cworld.getCubeCache().getLoadedCube(cubePosX, cubePosY-1, cubePosZ);
		if (cubeNY != null && cubeNY.getStorage() != null) {
			NibbleArray blockLightNY = cubeNY.getStorage().getBlockLight();
			for(int ix=0;ix<16;ix++) {
				for(int iz=0;iz<16;iz++) {
					int blny = blockLightNY.get(ix, 15, iz);
					int bl = blockLight.get(ix, 0, iz);
					if (blny - bl >= 2) {
						this.setLight(new BlockPos(ix, 0, iz), blockLight, blny-1, cstorage);
					}
					else if (bl - blny >= 2) {
						this.setLight(new BlockPos(ix, 15, iz), blockLightNY, bl-1, cstorage);
					}
				}
			}
		}
		ICube cubePY = cworld.getCubeCache().getLoadedCube(cubePosX, cubePosY+1, cubePosZ);
		if (cubePY != null && cubePY.getStorage() != null) {
			NibbleArray blockLightPY = cubePY.getStorage().getBlockLight();
			for(int ix=0;ix<16;ix++) {
				for(int iz=0;iz<16;iz++) {
					int blpy = blockLightPY.get(ix, 15, iz);
					int bl = blockLight.get(ix, 0, iz);
					if (blpy - bl >= 2) {
						this.setLight(new BlockPos(ix, 15, iz), blockLight, blpy-1, cstorage);
					}
					else if (bl - blpy >= 2) {
						this.setLight(new BlockPos(ix, 0, iz), blockLightPY, bl-1, cstorage);
					}
				}
			}
		}
		ICube cubeNZ = cworld.getCubeCache().getLoadedCube(cubePosX, cubePosY, cubePosZ-1);
		if (cubeNZ != null && cubeNZ.getStorage() != null) {
			NibbleArray blockLightNZ = cubeNZ.getStorage().getBlockLight();
			for(int ix=0;ix<16;ix++) {
				for(int iy=0;iy<16;iy++) {
					int blnz = blockLightNZ.get(ix, iy, 15);
					int bl = blockLight.get(ix, iy, 0);
					if (blnz - bl >= 2) {
						this.setLight(new BlockPos(ix, iy, 0), blockLight, blnz-1, cstorage);
					}
					else if (bl - blnz >= 2) {
						this.setLight(new BlockPos(ix, iy, 15), blockLightNZ, bl-1, cstorage);
					}
				}
			}
		}
		ICube cubePZ = cworld.getCubeCache().getLoadedCube(cubePosX, cubePosY, cubePosZ+1);
		if (cubePZ != null && cubePZ.getStorage() != null) {
			NibbleArray blockLightPZ = cubePZ.getStorage().getBlockLight();
			for(int ix=0;ix<16;ix++) {
				for(int iy=0;iy<16;iy++) {
					int blpz = blockLightPZ.get(ix, iy, 15);
					int bl = blockLight.get(ix, iy, 0);
					if (blpz - bl >= 2) {
						this.setLight(new BlockPos(ix, iy, 15), blockLight, blpz-1, cstorage);
					}
					else if (bl - blpz >= 2) {
						this.setLight(new BlockPos(ix, iy, 0), blockLightPZ, bl-1, cstorage);
					}
				}
			}
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
	
	@SuppressWarnings("deprecation")
	private void setLight(BlockPos lightPos, NibbleArray lightNibbleArray, int lightValue, ExtendedBlockStorage data) {
		if(lightValue<=0 ||
			lightPos.getX() < 0 ||
			lightPos.getY() < 0 ||
			lightPos.getZ() < 0 ||
			lightPos.getX() > 15 ||
			lightPos.getY() > 15 ||
			lightPos.getZ() > 15){
			return;
		}
		IBlockState bstate = data.get(lightPos.getX(), lightPos.getY(), lightPos.getZ());
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
}
