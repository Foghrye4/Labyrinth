package labyrinth.light;

import java.util.HashSet;
import java.util.Set;

import io.github.opencubicchunks.cubicchunks.api.util.CubePos;
import io.github.opencubicchunks.cubicchunks.api.world.CubeWatchEvent;
import io.github.opencubicchunks.cubicchunks.api.world.ICube;
import io.github.opencubicchunks.cubicchunks.api.world.ICubicWorld;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.chunk.NibbleArray;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class LightPropagator {
	public final static Set<CubePos> schleduledLightPropagate = new HashSet<CubePos>();
	
	@SubscribeEvent
	public void onCubeWatch(CubeWatchEvent event) {
		if(schleduledLightPropagate.remove(event.getCubePos())) {
			spreadLight(event.getCube(), event.getWorld());
		}
	}
	
	public static void spreadLight(ICube cube, ICubicWorld cworld) {
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
						setLight(new BlockPos(0, iy, iz), blockLight, blnx-1, cstorage);
					}
					else if (bl - blnx >= 2) {
						setLight(new BlockPos(15, iy, iz), blockLightNX, bl-1, cstorage);
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
						setLight(new BlockPos(15, iy, iz), blockLight, blpx-1, cstorage);
					}
					else if (bl - blpx >= 2) {
						setLight(new BlockPos(0, iy, iz), blockLightPX, bl-1, cstorage);
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
						setLight(new BlockPos(ix, 0, iz), blockLight, blny-1, cstorage);
					}
					else if (bl - blny >= 2) {
						setLight(new BlockPos(ix, 15, iz), blockLightNY, bl-1, cstorage);
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
						setLight(new BlockPos(ix, 15, iz), blockLight, blpy-1, cstorage);
					}
					else if (bl - blpy >= 2) {
						setLight(new BlockPos(ix, 0, iz), blockLightPY, bl-1, cstorage);
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
						setLight(new BlockPos(ix, iy, 0), blockLight, blnz-1, cstorage);
					}
					else if (bl - blnz >= 2) {
						setLight(new BlockPos(ix, iy, 15), blockLightNZ, bl-1, cstorage);
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
						setLight(new BlockPos(ix, iy, 15), blockLight, blpz-1, cstorage);
					}
					else if (bl - blpz >= 2) {
						setLight(new BlockPos(ix, iy, 0), blockLightPZ, bl-1, cstorage);
					}
				}
			}
		}
	}

	@SuppressWarnings("deprecation")
	private static void setLight(BlockPos lightPos, NibbleArray lightNibbleArray, int lightValue, ExtendedBlockStorage data) {
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
