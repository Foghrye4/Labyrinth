package labyrinth.worldgen;

import static labyrinth.worldgen.LabyrinthWorldGen.TUNNEL_LOCATION;
import static labyrinth.worldgen.LabyrinthWorldGen.TUNNEL_MASK;

import java.util.Random;

import io.github.opencubicchunks.cubicchunks.api.util.CubePos;
import io.github.opencubicchunks.cubicchunks.api.world.ICube;
import labyrinth.entity.IMobLeveled;
import labyrinth.entity.ISlime;
import labyrinth.init.LabyrinthEntities;
import labyrinth.util.LevelUtil;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.chunk.NibbleArray;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;

public class TunnelCubeStructureGenerator implements ICubeStructureGenerator {

	private final static int MOB_SPAWN_RARITY = 10;
	
	private LabyrinthWorldGen generator;
	private final Random random = new Random();
	
	public TunnelCubeStructureGenerator(LabyrinthWorldGen generatorIn){
		generator = generatorIn;
	}
	
	@Override
	public DungeonCube getDungeonCubeType(CubePos cpos, World world) {
		if ((cpos.getX() & TUNNEL_MASK) == TUNNEL_LOCATION)
			return DungeonCube.TUNNEL_PROP_SOUTH_NORTH;
		return DungeonCube.TUNNEL_PROP_EAST_WEST;
	}

	@Override
	public void spawnMobs(int level, World world, CubePos pos, ExtendedBlockStorage data) {
		random.setSeed(pos.hashCode()^world.getSeed());
		if(random.nextInt(MOB_SPAWN_RARITY)!=0)
			return;
		LevelFeaturesStorage storage = generator.storage;
		ResourceLocation[] regularLoot = generator.regularLoot;
		DifficultyInstance difficulty = world.getDifficultyForLocation(pos.getCenterBlockPos());
		IEntityLivingData livingdata = null;
		int mobRandom = level < storage.levelToMob.length ? level : random.nextInt() & (storage.levelToMob.length - 1);
		Class<? extends EntityLiving>[] mobs = storage.levelToMob[mobRandom];
		EntityLiving mobEntity;
		int dy = 0;
		for (int dx = random.nextInt(8); dx < 15; dx += random.nextInt(8) + 2)
			for (int dz = random.nextInt(8); dz < 15; dz += random.nextInt(8) + 2)
				try {
					Class<? extends EntityLiving> mob = mobs[this.random.nextInt(2)];
					mobEntity = mob.getDeclaredConstructor(World.class).newInstance(world);
					mobEntity.setLocationAndAngles(pos.getMinBlockX() + dx + 0.5, pos.getMinBlockY() + dy + 1,
							pos.getMinBlockZ() + dz + 0.5, random.nextFloat() * 360.0F, 0.0F);
					if (mobEntity.isNotColliding()) {
						((IMobLeveled) mobEntity).setLevel(level);
						livingdata = mobEntity.onInitialSpawn(difficulty, livingdata);
						((IMobLeveled) mobEntity).setLootTable(regularLoot[level >= regularLoot.length ? regularLoot.length - 1 : level]);
						world.spawnEntity(mobEntity);
					} else {
						mobEntity.setDead();
					}
				} catch (Throwable e) {
					e.printStackTrace();
				}
	}

	@Override
	public void placeCube(int level, ICube cube, ExtendedBlockStorage cstorage, CubePos pos, World world, byte[] data, IBlockState[] bl, DungeonCube is) {
		for (int index = 0; index < data.length; index++) {
			int dx = index >>> 8;
			int dy = (index >>> 4) & 15;
			int dz = index & 15;
			int bstate = Byte.toUnsignedInt(data[index]);
			BlockPos bpos = new BlockPos(pos.getMinBlockX() + dx, pos.getMinBlockY() + dy, pos.getMinBlockZ() + dz);
			cstorage.set(dx, dy, dz, bl[bstate]);
			cube.getColumn().getOpacityIndex().onOpacityChange(dx, pos.getMinBlockY() + dy, dz, bl[bstate].getLightOpacity((IBlockAccess) world, bpos));
		}
		cstorage.setBlockLight(new NibbleArray(is.lightData.clone()));
	}
}
