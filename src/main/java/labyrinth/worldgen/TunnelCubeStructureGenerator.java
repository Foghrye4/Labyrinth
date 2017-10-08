package labyrinth.worldgen;

import static labyrinth.worldgen.LabyrinthWorldGen.TUNNEL_MASK;
import static labyrinth.worldgen.LabyrinthWorldGen.TUNNEL_LOCATION;

import java.util.Random;

import cubicchunks.util.CubePos;
import cubicchunks.world.ICubicWorld;
import cubicchunks.world.cube.Cube;
import labyrinth.entity.IMobLeveled;
import labyrinth.entity.ISlime;
import labyrinth.init.LabyrinthEntities;
import labyrinth.util.LevelUtil;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLiving;
import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.chunk.NibbleArray;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;

public class TunnelCubeStructureGenerator implements ICubeStructureGenerator {

	private final static int MOB_SPAWN_RARITY = 12;
	
	private LabyrinthWorldGen generator;
	private final Random random = new Random();
	
	public TunnelCubeStructureGenerator(LabyrinthWorldGen generatorIn){
		generator = generatorIn;
	}
	
	@Override
	public DungeonCube getDungeonCubeType(CubePos cpos, ICubicWorld world) {
		if ((cpos.getX() & TUNNEL_MASK) == TUNNEL_LOCATION)
			return DungeonCube.TUNNEL_PROP_SOUTH_NORTH;
		return DungeonCube.TUNNEL_PROP_EAST_WEST;
	}

	@Override
	public void spawnMobs(int level, ICubicWorld world, CubePos pos, ExtendedBlockStorage data) {
		random.setSeed(pos.hashCode()^world.getSeed());
		if(random.nextInt(MOB_SPAWN_RARITY)!=0)
			return;
		LevelFeaturesStorage storage = generator.storage;
		ResourceLocation[] regularLoot = generator.regularLoot;
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
					if (mob == LabyrinthEntities.STRAY || mob == LabyrinthEntities.SKELETON)
						mobEntity.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, new ItemStack(Items.BOW));
					else if (mob == LabyrinthEntities.SLIME || mob == LabyrinthEntities.MAGMA_CUBE)
						((ISlime) mobEntity).setSlimeSize(LevelUtil.getSlimeSize(level));
					else if (mob == LabyrinthEntities.VINDICATOR)
						mobEntity.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, new ItemStack(Items.IRON_AXE));
					((IMobLeveled) mobEntity).setLevel(level);
					((IMobLeveled) mobEntity).setLootTable(regularLoot[level >= regularLoot.length ? regularLoot.length - 1 : level]);
					if(mobEntity.isNotColliding())
						world.spawnEntity(mobEntity);
					else
						mobEntity.setDead();
						
				} catch (Throwable e) {
					e.printStackTrace();
				}
	}

	@Override
	public void placeCube(int level, Cube cube, ExtendedBlockStorage cstorage, CubePos pos, ICubicWorld world, byte[] data, IBlockState[] bl, DungeonCube is) {
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
