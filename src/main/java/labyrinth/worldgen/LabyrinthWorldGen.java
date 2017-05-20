package labyrinth.worldgen;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import cubicchunks.api.worldgen.biome.CubicBiome;
import cubicchunks.api.worldgen.populator.ICubicPopulator;
import cubicchunks.util.CubePos;
import cubicchunks.world.ICubicWorld;
import labyrinth.LabyrinthMod;
import labyrinth.entity.IMobLeveled;
import labyrinth.entity.ISlime;
import labyrinth.init.LabyrinthBlocks;
import labyrinth.init.LabyrinthEntities;
import labyrinth.util.LevelUtil;
import labyrinth.util.ModIntegrationUtil;
import labyrinth.world.WorldSavedDataLabyrinthConfig;
import net.minecraft.block.BlockAnvil;
import net.minecraft.block.BlockCauldron;
import net.minecraft.block.BlockChest;
import net.minecraft.block.BlockColored;
import net.minecraft.block.BlockDoor;
import net.minecraft.block.BlockFurnace;
import net.minecraft.block.BlockLadder;
import net.minecraft.block.BlockLever;
import net.minecraft.block.BlockLog;
import net.minecraft.block.BlockLog.EnumAxis;
import net.minecraft.block.BlockPistonBase;
import net.minecraft.block.BlockPistonExtension;
import net.minecraft.block.BlockPlanks;
import net.minecraft.block.BlockPlanks.EnumType;
import net.minecraft.block.BlockPrismarine;
import net.minecraft.block.BlockQuartz;
import net.minecraft.block.BlockRedSandstone;
import net.minecraft.block.BlockSandStone;
import net.minecraft.block.BlockSkull;
import net.minecraft.block.BlockSlab;
import net.minecraft.block.BlockStainedGlassPane;
import net.minecraft.block.BlockStairs;
import net.minecraft.block.BlockStairs.EnumHalf;
import net.minecraft.block.BlockStairs.EnumShape;
import net.minecraft.block.BlockStone;
import net.minecraft.block.BlockStoneBrick;
import net.minecraft.block.BlockTorch;
import net.minecraft.block.BlockWoodSlab;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkGenerator;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.chunk.NibbleArray;

public class LabyrinthWorldGen implements ICubicPopulator {

	public enum DungeonCube {
		
		COLUMN_CEIL("column_ceil.cube_structure",false,false,false,false),
		COLUMN_CEIL_WITH_MINISTAIR("column_ceil_with_south_mini_stair.cube_structure",false,false,false,false),
		COLUMN_FLOOR("column_floor.cube_structure",false,false,false,false),
		STAIR_CEIL("stair_ceil.cube_structure",false,false,false,false),
		STAIR_FLOOR("stair_floor.cube_structure",false,false,false,false),
		COLUMN_EAST_BORDER("column_east_border.cube_structure",false,false,false,false), //5
		COLUMN_FLOOR_CEIL("column_floor_ceil.cube_structure",false,false,false,false),
		COLUMN_NORTH_BORDER("column_north_border.cube_structure",false,false,false,false),
		COLUMN_SOUTH_BORDER("column_south_border.cube_structure",false,false,false,false),
		COLUMN_WEST_BORDER("column_west_border.cube_structure",false,false,false,false),
		NODE("node.cube_structure",false,false,false,false),
		NODE_WITH_CHEST("node_with_chest.cube_structure",false,false,false,false),
		WORKSHOP("workshop_south_door.cube_structure",false,false,false,false),
		WALL_EAST_NORTH_BARS("wall_east_north_bars.cube_structure",true,false,false,true),//12
		WALL_EAST_NORTH("wall_east_north.cube_structure",true,false,false,true),
		WALL_EAST_SOUTH_BARS("wall_east_south_bars.cube_structure",true,false,true,false),
		WALL_EAST_SOUTH("wall_east_south.cube_structure",true,false,true,false),
		WALL_SOUTH_NORTH("wall_south_north.cube_structure",false,false,true,true),
		WALL_SOUTH_NORTH_DOOR("wall_south_north_door.cube_structure",false,false,true,true),
		WALL_WEST_EAST_BARS("wall_west_east_bars.cube_structure",true,true,false,false),
		WALL_WEST_EAST("wall_west_east.cube_structure",true,true,false,false),
		WALL_WEST_NORTH_BARS("wall_west_north_bars.cube_structure",false,true,false,true),
		WALL_WEST_NORTH("wall_west_north.cube_structure",false,true,false,true),
		WALL_WEST_SOUTH_BARS("wall_west_south_bars.cube_structure",false,true,true,false),
		WALL_WEST_SOUTH("wall_west_south.cube_structure",false,true,true,false),
		NOTHING("",false,false,false,false);
		
		public final String name;
		public final boolean isEastWall;
		public final boolean isWestWall;
		public final boolean isSouthWall;
		public final boolean isNorthWall;
		public final byte[] data = new byte[4096];
		
		DungeonCube(String nameIn, boolean isEastWallIn, boolean isWestWallIn, boolean isSouthWallIn, boolean isNorthWallIn){
			name=nameIn;
			isEastWall=isEastWallIn;
			isWestWall=isWestWallIn;
			isSouthWall=isSouthWallIn;
			isNorthWall=isNorthWallIn;
		}
		private void load() throws IOException {
			Minecraft.getMinecraft().getResourceManager().getResource(new ResourceLocation("labyrinth","cubes/"+name)).getInputStream().read(data);
		}

	}
	
	public DungeonCube[] randomDungeonsArray = new DungeonCube[] {
			DungeonCube.COLUMN_CEIL, // To increase chance of such structures
			DungeonCube.COLUMN_CEIL, // they are doubled in array
			DungeonCube.STAIR_CEIL,
			DungeonCube.STAIR_CEIL,
			DungeonCube.COLUMN_FLOOR_CEIL,
			DungeonCube.COLUMN_FLOOR_CEIL,
			DungeonCube.COLUMN_FLOOR_CEIL,
			DungeonCube.COLUMN_FLOOR_CEIL,
			DungeonCube.WALL_EAST_NORTH,
			DungeonCube.WALL_EAST_SOUTH,
			DungeonCube.WALL_SOUTH_NORTH,
			DungeonCube.WALL_WEST_EAST,
			DungeonCube.WALL_WEST_NORTH,
			DungeonCube.WALL_WEST_SOUTH,
			DungeonCube.NOTHING,
			DungeonCube.NOTHING,
			DungeonCube.NOTHING,
			DungeonCube.NOTHING,
			DungeonCube.NOTHING,
			DungeonCube.NOTHING,
			DungeonCube.NOTHING,
			DungeonCube.NOTHING,
			};

	public IBlockState[][] blockstateList = new IBlockState[128][256];
	
	private static final int CACHE_SIZE=256;
	Map<CubePos, DungeonCube> dtype_cache = new HashMap<CubePos, DungeonCube>(CACHE_SIZE+1);
	private final Random random = new Random();
	private final IBlockState AIR = Blocks.AIR.getDefaultState();
	private int max_loot_level = 7;
	public static LabyrinthWorldGen instance;
	private final IBlockState[] WALL_CANDIDATES = new IBlockState[]{
			LabyrinthBlocks.STONE.getDefaultState().withProperty(BlockStone.VARIANT, BlockStone.EnumType.ANDESITE),
			LabyrinthBlocks.STONE.getDefaultState().withProperty(BlockStone.VARIANT, BlockStone.EnumType.ANDESITE_SMOOTH),
			LabyrinthBlocks.STONE.getDefaultState().withProperty(BlockStone.VARIANT, BlockStone.EnumType.GRANITE),
			LabyrinthBlocks.STONE.getDefaultState().withProperty(BlockStone.VARIANT, BlockStone.EnumType.GRANITE_SMOOTH),
			LabyrinthBlocks.STONE.getDefaultState().withProperty(BlockStone.VARIANT, BlockStone.EnumType.DIORITE),
			LabyrinthBlocks.STONE.getDefaultState().withProperty(BlockStone.VARIANT, BlockStone.EnumType.DIORITE_SMOOTH),
			Blocks.QUARTZ_BLOCK.getDefaultState(),
			Blocks.QUARTZ_BLOCK.getDefaultState().withProperty(BlockQuartz.VARIANT, BlockQuartz.EnumType.CHISELED),
			Blocks.QUARTZ_BLOCK.getDefaultState().withProperty(BlockQuartz.VARIANT, BlockQuartz.EnumType.LINES_X),
			Blocks.QUARTZ_BLOCK.getDefaultState().withProperty(BlockQuartz.VARIANT, BlockQuartz.EnumType.LINES_Y),
			Blocks.QUARTZ_BLOCK.getDefaultState().withProperty(BlockQuartz.VARIANT, BlockQuartz.EnumType.LINES_Z),
			Blocks.COBBLESTONE.getDefaultState(),
			Blocks.SANDSTONE.getDefaultState(),
			Blocks.SANDSTONE.getDefaultState().withProperty(BlockSandStone.TYPE, BlockSandStone.EnumType.CHISELED),
			Blocks.SANDSTONE.getDefaultState().withProperty(BlockSandStone.TYPE, BlockSandStone.EnumType.SMOOTH),
			Blocks.BRICK_BLOCK.getDefaultState(),
			Blocks.MOSSY_COBBLESTONE.getDefaultState(),
			Blocks.OBSIDIAN.getDefaultState(),
			Blocks.SOUL_SAND.getDefaultState(),
			Blocks.NETHERRACK.getDefaultState(),
			Blocks.STONEBRICK.getDefaultState(),
			Blocks.STONEBRICK.getDefaultState().withProperty(BlockStoneBrick.VARIANT, BlockStoneBrick.EnumType.CHISELED),
			Blocks.STONEBRICK.getDefaultState().withProperty(BlockStoneBrick.VARIANT, BlockStoneBrick.EnumType.CRACKED),
			Blocks.STONEBRICK.getDefaultState().withProperty(BlockStoneBrick.VARIANT, BlockStoneBrick.EnumType.MOSSY),
			Blocks.NETHER_BRICK.getDefaultState(),
			Blocks.STAINED_HARDENED_CLAY.getDefaultState().withProperty(BlockColored.COLOR, EnumDyeColor.BLACK),
			Blocks.STAINED_HARDENED_CLAY.getDefaultState().withProperty(BlockColored.COLOR, EnumDyeColor.BLUE),
			Blocks.STAINED_HARDENED_CLAY.getDefaultState().withProperty(BlockColored.COLOR, EnumDyeColor.BROWN),
			Blocks.STAINED_HARDENED_CLAY.getDefaultState().withProperty(BlockColored.COLOR, EnumDyeColor.CYAN),
			Blocks.STAINED_HARDENED_CLAY.getDefaultState().withProperty(BlockColored.COLOR, EnumDyeColor.GRAY),
			Blocks.STAINED_HARDENED_CLAY.getDefaultState().withProperty(BlockColored.COLOR, EnumDyeColor.GREEN),
			Blocks.STAINED_HARDENED_CLAY.getDefaultState().withProperty(BlockColored.COLOR, EnumDyeColor.LIME),
			Blocks.STAINED_HARDENED_CLAY.getDefaultState().withProperty(BlockColored.COLOR, EnumDyeColor.ORANGE),
			Blocks.STAINED_HARDENED_CLAY.getDefaultState().withProperty(BlockColored.COLOR, EnumDyeColor.PINK),
			Blocks.STAINED_HARDENED_CLAY.getDefaultState().withProperty(BlockColored.COLOR, EnumDyeColor.RED),
			Blocks.STAINED_HARDENED_CLAY.getDefaultState().withProperty(BlockColored.COLOR, EnumDyeColor.SILVER),
			Blocks.STAINED_HARDENED_CLAY.getDefaultState().withProperty(BlockColored.COLOR, EnumDyeColor.WHITE),
			Blocks.STAINED_HARDENED_CLAY.getDefaultState().withProperty(BlockColored.COLOR, EnumDyeColor.YELLOW),
			Blocks.PRISMARINE.getDefaultState().withProperty(BlockPrismarine.VARIANT, BlockPrismarine.EnumType.BRICKS),
			Blocks.PRISMARINE.getDefaultState().withProperty(BlockPrismarine.VARIANT, BlockPrismarine.EnumType.DARK),
			Blocks.COAL_BLOCK.getDefaultState(),
			Blocks.RED_SANDSTONE.getDefaultState(),
			Blocks.RED_SANDSTONE.getDefaultState().withProperty(BlockRedSandstone.TYPE, BlockRedSandstone.EnumType.CHISELED),
			Blocks.RED_SANDSTONE.getDefaultState().withProperty(BlockRedSandstone.TYPE, BlockRedSandstone.EnumType.SMOOTH),
			Blocks.END_BRICKS.getDefaultState(),
			Blocks.NETHER_WART_BLOCK.getDefaultState(),
			Blocks.RED_NETHER_BRICK.getDefaultState(),
			Blocks.BONE_BLOCK.getDefaultState(),
	};
	
	private final IBlockState[] FLOOR_CANDIDATES = new IBlockState[]{
			LabyrinthBlocks.STONE.getDefaultState(),
			LabyrinthBlocks.STONE.getDefaultState().withProperty(BlockStone.VARIANT, BlockStone.EnumType.ANDESITE),
			LabyrinthBlocks.STONE.getDefaultState().withProperty(BlockStone.VARIANT, BlockStone.EnumType.ANDESITE_SMOOTH),
			LabyrinthBlocks.STONE.getDefaultState().withProperty(BlockStone.VARIANT, BlockStone.EnumType.GRANITE),
			LabyrinthBlocks.STONE.getDefaultState().withProperty(BlockStone.VARIANT, BlockStone.EnumType.GRANITE_SMOOTH),
			LabyrinthBlocks.STONE.getDefaultState().withProperty(BlockStone.VARIANT, BlockStone.EnumType.DIORITE),
			LabyrinthBlocks.STONE.getDefaultState().withProperty(BlockStone.VARIANT, BlockStone.EnumType.DIORITE_SMOOTH),
			Blocks.QUARTZ_BLOCK.getDefaultState(),
			Blocks.QUARTZ_BLOCK.getDefaultState().withProperty(BlockQuartz.VARIANT, BlockQuartz.EnumType.CHISELED),
			Blocks.QUARTZ_BLOCK.getDefaultState().withProperty(BlockQuartz.VARIANT, BlockQuartz.EnumType.LINES_X),
			Blocks.QUARTZ_BLOCK.getDefaultState().withProperty(BlockQuartz.VARIANT, BlockQuartz.EnumType.LINES_Y),
			Blocks.QUARTZ_BLOCK.getDefaultState().withProperty(BlockQuartz.VARIANT, BlockQuartz.EnumType.LINES_Z),
			Blocks.SANDSTONE.getDefaultState().withProperty(BlockSandStone.TYPE, BlockSandStone.EnumType.CHISELED),
			Blocks.OBSIDIAN.getDefaultState(),
			Blocks.SOUL_SAND.getDefaultState(),
			Blocks.NETHERRACK.getDefaultState(),
			Blocks.STONEBRICK.getDefaultState(),
			Blocks.STONEBRICK.getDefaultState().withProperty(BlockStoneBrick.VARIANT, BlockStoneBrick.EnumType.CHISELED),
			Blocks.STONEBRICK.getDefaultState().withProperty(BlockStoneBrick.VARIANT, BlockStoneBrick.EnumType.CRACKED),
			Blocks.STONEBRICK.getDefaultState().withProperty(BlockStoneBrick.VARIANT, BlockStoneBrick.EnumType.MOSSY),
			Blocks.NETHER_BRICK.getDefaultState(),
			Blocks.STAINED_HARDENED_CLAY.getDefaultState().withProperty(BlockColored.COLOR, EnumDyeColor.BLUE),
			Blocks.STAINED_HARDENED_CLAY.getDefaultState().withProperty(BlockColored.COLOR, EnumDyeColor.CYAN),
			Blocks.STAINED_HARDENED_CLAY.getDefaultState().withProperty(BlockColored.COLOR, EnumDyeColor.GRAY),
			Blocks.STAINED_HARDENED_CLAY.getDefaultState().withProperty(BlockColored.COLOR, EnumDyeColor.ORANGE),
			Blocks.STAINED_HARDENED_CLAY.getDefaultState().withProperty(BlockColored.COLOR, EnumDyeColor.RED),
			Blocks.STAINED_HARDENED_CLAY.getDefaultState().withProperty(BlockColored.COLOR, EnumDyeColor.SILVER),
			Blocks.STAINED_HARDENED_CLAY.getDefaultState().withProperty(BlockColored.COLOR, EnumDyeColor.WHITE),
			Blocks.PRISMARINE.getDefaultState(),
			Blocks.PRISMARINE.getDefaultState().withProperty(BlockPrismarine.VARIANT, BlockPrismarine.EnumType.BRICKS),
			Blocks.PRISMARINE.getDefaultState().withProperty(BlockPrismarine.VARIANT, BlockPrismarine.EnumType.DARK),
			Blocks.COAL_BLOCK.getDefaultState(),
			Blocks.RED_SANDSTONE.getDefaultState(),
			Blocks.RED_SANDSTONE.getDefaultState().withProperty(BlockRedSandstone.TYPE, BlockRedSandstone.EnumType.CHISELED),
			Blocks.RED_SANDSTONE.getDefaultState().withProperty(BlockRedSandstone.TYPE, BlockRedSandstone.EnumType.SMOOTH),
			Blocks.END_BRICKS.getDefaultState(),
			Blocks.NETHER_WART_BLOCK.getDefaultState(),
			Blocks.RED_NETHER_BRICK.getDefaultState(),
			Blocks.BONE_BLOCK.getDefaultState(),
	};

	private final IBlockState[] STAIR_CANDIDATES = new IBlockState[]{
			Blocks.QUARTZ_STAIRS.getBlockState().getBaseState().withProperty(BlockStairs.SHAPE, EnumShape.STRAIGHT),
			Blocks.STONE_STAIRS.getBlockState().getBaseState().withProperty(BlockStairs.SHAPE, EnumShape.STRAIGHT),
			Blocks.STONE_BRICK_STAIRS.getBlockState().getBaseState().withProperty(BlockStairs.SHAPE, EnumShape.STRAIGHT),
			Blocks.NETHER_BRICK_STAIRS.getBlockState().getBaseState().withProperty(BlockStairs.SHAPE, EnumShape.STRAIGHT),
			Blocks.SANDSTONE_STAIRS.getBlockState().getBaseState().withProperty(BlockStairs.SHAPE, EnumShape.STRAIGHT),
			Blocks.RED_SANDSTONE_STAIRS.getBlockState().getBaseState().withProperty(BlockStairs.SHAPE, EnumShape.STRAIGHT),
	};
	private final IBlockState[] WINDOW_CANDIDATES = new IBlockState[]{
			Blocks.OAK_FENCE.getDefaultState(),
			Blocks.NETHER_BRICK_FENCE.getDefaultState(),
			Blocks.DARK_OAK_FENCE.getDefaultState(),
			Blocks.ACACIA_FENCE.getDefaultState(),
			Blocks.BIRCH_FENCE.getDefaultState(),
			Blocks.IRON_BARS.getDefaultState(),
			Blocks.BIRCH_FENCE.getDefaultState()
	};
	private final IBlockState[] FENCE_CANDIDATES = new IBlockState[]{
			Blocks.OAK_FENCE.getDefaultState(),
			Blocks.NETHER_BRICK_FENCE.getDefaultState(),
			Blocks.DARK_OAK_FENCE.getDefaultState(),
			Blocks.ACACIA_FENCE.getDefaultState(),
			Blocks.BIRCH_FENCE.getDefaultState(),
			Blocks.COBBLESTONE_WALL.getDefaultState()
	};
	
	@SuppressWarnings("unchecked")
	private final Class<? extends EntityLivingBase>[] MOB_CANDIDATES_FIRST = new Class[] {
		LabyrinthEntities.ZOMBIE,
		LabyrinthEntities.CAVE_SPIDER,
		LabyrinthEntities.CREEPER,
		LabyrinthEntities.ENDERMAN,
		LabyrinthEntities.ENDERMITE,
		LabyrinthEntities.MAGMA_CUBE,
		LabyrinthEntities.PIG_ZOMBIE,
		LabyrinthEntities.SPIDER,
		LabyrinthEntities.SLIME,
		LabyrinthEntities.WITHER_SKELETON
	};
	
	@SuppressWarnings("unchecked")
	private final Class<? extends EntityLivingBase>[] MOB_CANDIDATES_SECOND = new Class[] {
		LabyrinthEntities.BLAZE,
		LabyrinthEntities.SKELETON,
		LabyrinthEntities.STRAY,
		LabyrinthEntities.VINDICATOR,
		LabyrinthEntities.WITCH,
		LabyrinthEntities.VEX
	};
	@SuppressWarnings("unchecked")
	private final Class<? extends EntityLivingBase>[][] levelToMob = new Class[128][2];

	private WorldSavedDataLabyrinthConfig config;

	public LabyrinthWorldGen() throws IOException {
		instance=this;
		
		for(Class<? extends EntityLivingBase>[] levelMobs:levelToMob){
			levelMobs[0] = MOB_CANDIDATES_FIRST[random.nextInt(MOB_CANDIDATES_FIRST.length)];
			levelMobs[1] = MOB_CANDIDATES_SECOND[random.nextInt(MOB_CANDIDATES_SECOND.length)];
		}
		levelToMob[0][0] = LabyrinthEntities.ZOMBIE;
		levelToMob[0][1] = LabyrinthEntities.ZOMBIE;
		levelToMob[1][0] = LabyrinthEntities.ZOMBIE;
		levelToMob[1][1] = LabyrinthEntities.SLIME;
		levelToMob[2][0] = LabyrinthEntities.ZOMBIE;
		levelToMob[2][1] = LabyrinthEntities.SKELETON;
		levelToMob[3][0] = LabyrinthEntities.CREEPER;
		levelToMob[3][1] = LabyrinthEntities.SKELETON;
		levelToMob[7][0] = LabyrinthEntities.MAGMA_CUBE;
		levelToMob[7][1] = LabyrinthEntities.BLAZE;
		
		Arrays.fill(blockstateList[0], Blocks.AIR.getDefaultState());
		blockstateList[0][1] = Blocks.QUARTZ_BLOCK.getDefaultState();
		blockstateList[0][2] = Blocks.QUARTZ_BLOCK.getDefaultState().withProperty(BlockQuartz.VARIANT, BlockQuartz.EnumType.CHISELED);
		blockstateList[0][3] = Blocks.CHEST.getDefaultState().withProperty(BlockChest.FACING, EnumFacing.NORTH);
		blockstateList[0][4] = Blocks.CHEST.getDefaultState().withProperty(BlockChest.FACING, EnumFacing.SOUTH);
		blockstateList[0][5] = Blocks.CHEST.getDefaultState().withProperty(BlockChest.FACING, EnumFacing.WEST);
		blockstateList[0][6] = Blocks.CHEST.getDefaultState().withProperty(BlockChest.FACING, EnumFacing.EAST);
		blockstateList[0][7] = Blocks.FURNACE.getDefaultState().withProperty(BlockFurnace.FACING, EnumFacing.NORTH);
		blockstateList[0][8] = Blocks.FURNACE.getDefaultState().withProperty(BlockFurnace.FACING, EnumFacing.SOUTH);
		blockstateList[0][9] = Blocks.FURNACE.getDefaultState().withProperty(BlockFurnace.FACING, EnumFacing.WEST);
		blockstateList[0][10] = Blocks.FURNACE.getDefaultState().withProperty(BlockFurnace.FACING, EnumFacing.EAST);
		blockstateList[0][11] = Blocks.CRAFTING_TABLE.getDefaultState();
		blockstateList[0][12] = ModIntegrationUtil.getBlockDefaultStateIfNotNull("aov:blocks/blockangelic", Blocks.CAULDRON.getBlockState().getBaseState().withProperty(BlockCauldron.LEVEL, Integer.valueOf(3)));
		blockstateList[0][13] = Blocks.BREWING_STAND.getDefaultState();
		blockstateList[0][14] = Blocks.COAL_BLOCK.getDefaultState();
		blockstateList[0][15] = Blocks.WOODEN_SLAB.getDefaultState().withProperty(BlockSlab.HALF, BlockSlab.EnumBlockHalf.TOP).withProperty(BlockWoodSlab.VARIANT, BlockPlanks.EnumType.OAK);
		blockstateList[0][16] = Blocks.LAVA.getDefaultState();
		blockstateList[0][17] = Blocks.WATER.getDefaultState();
		blockstateList[0][18] = Blocks.STAINED_GLASS_PANE.getDefaultState().withProperty(BlockStainedGlassPane.COLOR, EnumDyeColor.RED);
		IBlockState stair = Blocks.QUARTZ_STAIRS.getDefaultState().withProperty(BlockStairs.SHAPE, EnumShape.STRAIGHT);
		addStairs(blockstateList[0], stair);
		blockstateList[0][57] = Blocks.WOODEN_SLAB.getDefaultState().withProperty(BlockSlab.HALF, BlockSlab.EnumBlockHalf.BOTTOM).withProperty(BlockWoodSlab.VARIANT, BlockPlanks.EnumType.OAK);
		blockstateList[0][116] = Blocks.ANVIL.getDefaultState().withProperty(BlockAnvil.FACING, EnumFacing.NORTH).withProperty(BlockAnvil.DAMAGE, Integer.valueOf(0));
		blockstateList[0][117] = Blocks.ANVIL.getDefaultState().withProperty(BlockAnvil.FACING, EnumFacing.SOUTH).withProperty(BlockAnvil.DAMAGE, Integer.valueOf(0));
		blockstateList[0][118] = Blocks.ANVIL.getDefaultState().withProperty(BlockAnvil.FACING, EnumFacing.WEST).withProperty(BlockAnvil.DAMAGE, Integer.valueOf(0));
		blockstateList[0][119] = Blocks.ANVIL.getDefaultState().withProperty(BlockAnvil.FACING, EnumFacing.EAST).withProperty(BlockAnvil.DAMAGE, Integer.valueOf(0));
		blockstateList[0][120] = Blocks.ACACIA_FENCE.getDefaultState();
		blockstateList[0][129] = Blocks.STICKY_PISTON.getDefaultState().withProperty(BlockPistonBase.EXTENDED, Boolean.valueOf(true)).withProperty(BlockPistonBase.FACING, EnumFacing.NORTH);
		blockstateList[0][130] = Blocks.PISTON_HEAD.getDefaultState().withProperty(BlockPistonExtension.TYPE, BlockPistonExtension.EnumPistonType.STICKY).withProperty(BlockPistonExtension.FACING, EnumFacing.NORTH).withProperty(BlockPistonExtension.SHORT, Boolean.valueOf(false));
		blockstateList[0][137] = Blocks.LEVER.getDefaultState().withProperty(BlockLever.FACING, BlockLever.EnumOrientation.EAST).withProperty(BlockLever.POWERED, Boolean.valueOf(true));
		blockstateList[0][153] = Blocks.TORCH.getDefaultState().withProperty(BlockTorch.FACING, EnumFacing.UP);
		blockstateList[0][154] = Blocks.TORCH.getDefaultState().withProperty(BlockTorch.FACING, EnumFacing.NORTH);
		blockstateList[0][155] = Blocks.TORCH.getDefaultState().withProperty(BlockTorch.FACING, EnumFacing.SOUTH);
		blockstateList[0][156] = Blocks.TORCH.getDefaultState().withProperty(BlockTorch.FACING, EnumFacing.WEST);
		blockstateList[0][157] = Blocks.TORCH.getDefaultState().withProperty(BlockTorch.FACING, EnumFacing.EAST);
		blockstateList[0][158] = Blocks.DARK_OAK_DOOR.getDefaultState().withProperty(BlockDoor.FACING, EnumFacing.NORTH).withProperty(BlockDoor.HALF, BlockDoor.EnumDoorHalf.UPPER).withProperty(BlockDoor.HINGE, BlockDoor.EnumHingePosition.LEFT).withProperty(BlockDoor.OPEN, Boolean.valueOf(false)).withProperty(BlockDoor.POWERED, Boolean.valueOf(false));
		blockstateList[0][159] = Blocks.DARK_OAK_DOOR.getDefaultState().withProperty(BlockDoor.FACING, EnumFacing.NORTH).withProperty(BlockDoor.HALF, BlockDoor.EnumDoorHalf.UPPER).withProperty(BlockDoor.HINGE, BlockDoor.EnumHingePosition.RIGHT).withProperty(BlockDoor.OPEN, Boolean.valueOf(false)).withProperty(BlockDoor.POWERED, Boolean.valueOf(false));
		blockstateList[0][160] = Blocks.DARK_OAK_DOOR.getDefaultState().withProperty(BlockDoor.FACING, EnumFacing.NORTH).withProperty(BlockDoor.HALF, BlockDoor.EnumDoorHalf.LOWER).withProperty(BlockDoor.HINGE, BlockDoor.EnumHingePosition.LEFT).withProperty(BlockDoor.OPEN, Boolean.valueOf(false)).withProperty(BlockDoor.POWERED, Boolean.valueOf(false));
		blockstateList[0][161] = Blocks.DARK_OAK_DOOR.getDefaultState().withProperty(BlockDoor.FACING, EnumFacing.NORTH).withProperty(BlockDoor.HALF, BlockDoor.EnumDoorHalf.LOWER).withProperty(BlockDoor.HINGE, BlockDoor.EnumHingePosition.RIGHT).withProperty(BlockDoor.OPEN, Boolean.valueOf(false)).withProperty(BlockDoor.POWERED, Boolean.valueOf(false));
		blockstateList[0][162] = Blocks.DARK_OAK_DOOR.getDefaultState().withProperty(BlockDoor.FACING, EnumFacing.SOUTH).withProperty(BlockDoor.HALF, BlockDoor.EnumDoorHalf.UPPER).withProperty(BlockDoor.HINGE, BlockDoor.EnumHingePosition.LEFT).withProperty(BlockDoor.OPEN, Boolean.valueOf(false)).withProperty(BlockDoor.POWERED, Boolean.valueOf(false));
		blockstateList[0][163] = Blocks.DARK_OAK_DOOR.getDefaultState().withProperty(BlockDoor.FACING, EnumFacing.SOUTH).withProperty(BlockDoor.HALF, BlockDoor.EnumDoorHalf.UPPER).withProperty(BlockDoor.HINGE, BlockDoor.EnumHingePosition.RIGHT).withProperty(BlockDoor.OPEN, Boolean.valueOf(false)).withProperty(BlockDoor.POWERED, Boolean.valueOf(false));
		blockstateList[0][164] = Blocks.DARK_OAK_DOOR.getDefaultState().withProperty(BlockDoor.FACING, EnumFacing.SOUTH).withProperty(BlockDoor.HALF, BlockDoor.EnumDoorHalf.LOWER).withProperty(BlockDoor.HINGE, BlockDoor.EnumHingePosition.LEFT).withProperty(BlockDoor.OPEN, Boolean.valueOf(false)).withProperty(BlockDoor.POWERED, Boolean.valueOf(false));
		blockstateList[0][165] = Blocks.DARK_OAK_DOOR.getDefaultState().withProperty(BlockDoor.FACING, EnumFacing.SOUTH).withProperty(BlockDoor.HALF, BlockDoor.EnumDoorHalf.LOWER).withProperty(BlockDoor.HINGE, BlockDoor.EnumHingePosition.RIGHT).withProperty(BlockDoor.OPEN, Boolean.valueOf(false)).withProperty(BlockDoor.POWERED, Boolean.valueOf(false));
		blockstateList[0][166] = Blocks.BOOKSHELF.getDefaultState();
		blockstateList[0][167] = Blocks.LADDER.getDefaultState().withProperty(BlockLadder.FACING, EnumFacing.SOUTH);
		blockstateList[0][171] = Blocks.ENCHANTING_TABLE.getDefaultState();
		blockstateList[0][172] = Blocks.PLANKS.getDefaultState().withProperty(BlockPlanks.VARIANT, EnumType.OAK);
		blockstateList[0][173] = Blocks.OAK_STAIRS.getDefaultState().withProperty(BlockStairs.FACING, EnumFacing.SOUTH).withProperty(BlockStairs.HALF, EnumHalf.BOTTOM);
		blockstateList[0][174] = Blocks.LOG.getDefaultState().withProperty(BlockLog.LOG_AXIS, EnumAxis.X);
		blockstateList[0][175] = Blocks.LOG.getDefaultState().withProperty(BlockLog.LOG_AXIS, EnumAxis.Y);
		blockstateList[0][176] = Blocks.LOG.getDefaultState().withProperty(BlockLog.LOG_AXIS, EnumAxis.Z);
		blockstateList[0][177] = Blocks.OAK_STAIRS.getDefaultState().withProperty(BlockStairs.FACING, EnumFacing.NORTH).withProperty(BlockStairs.HALF, EnumHalf.BOTTOM);
		blockstateList[0][255] = Blocks.SKULL.getDefaultState().withProperty(BlockSkull.FACING, EnumFacing.UP).withProperty(BlockSkull.NODROP, Boolean.valueOf(false));

		for(int i=1;i<blockstateList.length;i++)
			blockstateList[i]=Arrays.copyOf(blockstateList[0], 256);
		
		blockstateList[1][1] = LabyrinthBlocks.STONE.getDefaultState().withProperty(BlockStone.VARIANT, BlockStone.EnumType.GRANITE_SMOOTH);
		blockstateList[1][2] = Blocks.QUARTZ_BLOCK.getDefaultState().withProperty(BlockQuartz.VARIANT, BlockQuartz.EnumType.CHISELED);
		blockstateList[1][18] = Blocks.OAK_FENCE.getDefaultState();
		blockstateList[1][120] = Blocks.OAK_FENCE.getDefaultState();
		
		blockstateList[2][1] = LabyrinthBlocks.STONE.getDefaultState().withProperty(BlockStone.VARIANT, BlockStone.EnumType.DIORITE_SMOOTH);
		blockstateList[2][2] = LabyrinthBlocks.STONE.getDefaultState().withProperty(BlockStone.VARIANT, BlockStone.EnumType.DIORITE);
		blockstateList[2][18] = Blocks.IRON_BARS.getDefaultState();
		blockstateList[2][120] = Blocks.DARK_OAK_FENCE.getDefaultState();
		stair = Blocks.RED_SANDSTONE_STAIRS.getBlockState().getBaseState().withProperty(BlockStairs.SHAPE, EnumShape.STRAIGHT);
		addStairs(blockstateList[2], stair);
		
		blockstateList[3][1] = Blocks.STAINED_HARDENED_CLAY.getDefaultState().withProperty(BlockColored.COLOR, EnumDyeColor.LIGHT_BLUE);
		blockstateList[3][2] = Blocks.STAINED_HARDENED_CLAY.getDefaultState().withProperty(BlockColored.COLOR, EnumDyeColor.WHITE);
		blockstateList[3][18] = Blocks.IRON_BARS.getDefaultState();
		blockstateList[3][120] = Blocks.DARK_OAK_FENCE.getDefaultState();

		blockstateList[4][1] = Blocks.STONEBRICK.getDefaultState();
		blockstateList[4][2] = LabyrinthBlocks.STONE.getDefaultState();
		blockstateList[4][18] = Blocks.IRON_BARS.getDefaultState();
		blockstateList[4][120] = Blocks.COBBLESTONE_WALL.getDefaultState();
		stair = Blocks.STONE_BRICK_STAIRS.getBlockState().getBaseState().withProperty(BlockStairs.SHAPE, EnumShape.STRAIGHT);
		addStairs(blockstateList[4], stair);

		blockstateList[5][1] = Blocks.COBBLESTONE.getDefaultState();
		blockstateList[5][2] = LabyrinthBlocks.STONE.getDefaultState();
		blockstateList[5][18] = Blocks.IRON_BARS.getDefaultState();
		blockstateList[5][120] = Blocks.COBBLESTONE_WALL.getDefaultState();
		stair = Blocks.STONE_STAIRS.getBlockState().getBaseState().withProperty(BlockStairs.SHAPE, EnumShape.STRAIGHT);
		addStairs(blockstateList[5], stair);

		blockstateList[6][1] = Blocks.NETHER_BRICK.getDefaultState();
		blockstateList[6][2] = LabyrinthBlocks.STONE.getDefaultState().withProperty(BlockStone.VARIANT, BlockStone.EnumType.GRANITE_SMOOTH);
		blockstateList[6][18] = Blocks.IRON_BARS.getDefaultState();
		blockstateList[6][120] = Blocks.NETHER_BRICK_FENCE.getDefaultState();
		stair = Blocks.NETHER_BRICK_STAIRS.getBlockState().getBaseState().withProperty(BlockStairs.SHAPE, EnumShape.STRAIGHT);
		addStairs(blockstateList[6], stair);

		blockstateList[7][1] = Blocks.RED_NETHER_BRICK.getDefaultState();
		blockstateList[7][2] = Blocks.MAGMA.getDefaultState();
		blockstateList[7][18] = Blocks.NETHER_BRICK_FENCE.getDefaultState();
		blockstateList[7][120] = Blocks.NETHER_BRICK_FENCE.getDefaultState();
		stair = Blocks.NETHER_BRICK_STAIRS.getBlockState().getBaseState().withProperty(BlockStairs.SHAPE, EnumShape.STRAIGHT);
		addStairs(blockstateList[7], stair);
		
		for(int i=8;i<blockstateList.length;i++){
			blockstateList[i][1] = WALL_CANDIDATES[random.nextInt(WALL_CANDIDATES.length)];
			blockstateList[i][2] = FLOOR_CANDIDATES[random.nextInt(FLOOR_CANDIDATES.length)];
			blockstateList[i][18] = WINDOW_CANDIDATES[random.nextInt(WINDOW_CANDIDATES.length)];
			blockstateList[i][120] = FENCE_CANDIDATES[random.nextInt(FENCE_CANDIDATES.length)];
			stair = STAIR_CANDIDATES[random.nextInt(STAIR_CANDIDATES.length)];
			addStairs(blockstateList[i], stair);
		}
		for(DungeonCube cube: DungeonCube.values()) {
			if(cube!=DungeonCube.NOTHING) {
				cube.load();
			}
		}
}

	private void addStairs(IBlockState[] blockstateIn, IBlockState stair){
		blockstateIn[19] = stair.withProperty(BlockStairs.FACING, EnumFacing.NORTH).withProperty(BlockStairs.HALF, EnumHalf.TOP);
		blockstateIn[24] = stair.withProperty(BlockStairs.FACING, EnumFacing.NORTH).withProperty(BlockStairs.HALF, EnumHalf.BOTTOM);
		blockstateIn[25] = stair.withProperty(BlockStairs.FACING, EnumFacing.NORTH).withProperty(BlockStairs.HALF, EnumHalf.BOTTOM).withProperty(BlockStairs.SHAPE,EnumShape.INNER_RIGHT);
		blockstateIn[26] = stair.withProperty(BlockStairs.FACING, EnumFacing.NORTH).withProperty(BlockStairs.HALF, EnumHalf.BOTTOM).withProperty(BlockStairs.SHAPE,EnumShape.INNER_LEFT);
		blockstateIn[27] = stair.withProperty(BlockStairs.FACING, EnumFacing.NORTH).withProperty(BlockStairs.HALF, EnumHalf.BOTTOM).withProperty(BlockStairs.SHAPE,EnumShape.OUTER_RIGHT);
		blockstateIn[28] = stair.withProperty(BlockStairs.FACING, EnumFacing.NORTH).withProperty(BlockStairs.HALF, EnumHalf.BOTTOM).withProperty(BlockStairs.SHAPE,EnumShape.OUTER_LEFT);
		blockstateIn[29] = stair.withProperty(BlockStairs.FACING, EnumFacing.SOUTH).withProperty(BlockStairs.HALF, EnumHalf.TOP);
		blockstateIn[34] = stair.withProperty(BlockStairs.FACING, EnumFacing.SOUTH).withProperty(BlockStairs.HALF, EnumHalf.BOTTOM);
		blockstateIn[39] = stair.withProperty(BlockStairs.FACING, EnumFacing.WEST).withProperty(BlockStairs.HALF, EnumHalf.TOP);
		blockstateIn[40] = stair.withProperty(BlockStairs.FACING, EnumFacing.WEST).withProperty(BlockStairs.HALF, EnumHalf.TOP).withProperty(BlockStairs.SHAPE,EnumShape.INNER_RIGHT);
		blockstateIn[41] = stair.withProperty(BlockStairs.FACING, EnumFacing.WEST).withProperty(BlockStairs.HALF, EnumHalf.TOP).withProperty(BlockStairs.SHAPE,EnumShape.INNER_LEFT);
		blockstateIn[44] = stair.withProperty(BlockStairs.FACING, EnumFacing.WEST).withProperty(BlockStairs.HALF, EnumHalf.BOTTOM);
		blockstateIn[45] = stair.withProperty(BlockStairs.FACING, EnumFacing.WEST).withProperty(BlockStairs.HALF, EnumHalf.BOTTOM).withProperty(BlockStairs.SHAPE,EnumShape.INNER_RIGHT);
		blockstateIn[46] = stair.withProperty(BlockStairs.FACING, EnumFacing.WEST).withProperty(BlockStairs.HALF, EnumHalf.BOTTOM).withProperty(BlockStairs.SHAPE,EnumShape.INNER_LEFT);
		blockstateIn[49] = stair.withProperty(BlockStairs.FACING, EnumFacing.EAST).withProperty(BlockStairs.HALF, EnumHalf.TOP);
		blockstateIn[50] = stair.withProperty(BlockStairs.FACING, EnumFacing.EAST).withProperty(BlockStairs.HALF, EnumHalf.TOP).withProperty(BlockStairs.SHAPE,EnumShape.INNER_RIGHT);
		blockstateIn[51] = stair.withProperty(BlockStairs.FACING, EnumFacing.EAST).withProperty(BlockStairs.HALF, EnumHalf.TOP).withProperty(BlockStairs.SHAPE,EnumShape.INNER_LEFT);
		blockstateIn[54] = stair.withProperty(BlockStairs.FACING, EnumFacing.EAST).withProperty(BlockStairs.HALF, EnumHalf.BOTTOM);
		blockstateIn[55] = stair.withProperty(BlockStairs.FACING, EnumFacing.EAST).withProperty(BlockStairs.HALF, EnumHalf.BOTTOM).withProperty(BlockStairs.SHAPE,EnumShape.INNER_RIGHT);
		blockstateIn[56] = stair.withProperty(BlockStairs.FACING, EnumFacing.EAST).withProperty(BlockStairs.HALF, EnumHalf.BOTTOM).withProperty(BlockStairs.SHAPE,EnumShape.INNER_LEFT);
	}
	
	@Override
	public void generate(Random random, int chunkX, int chunkZ, World world, IChunkGenerator chunkGenerator,
			IChunkProvider chunkProvider) {
	}

	@Override
	public void generate(ICubicWorld world, Random random, CubePos pos, CubicBiome biome) {
		float biomeHeightBase = biome.getBiome().getBaseHeight();
		if (biomeHeightBase < config.dungeonBiomeHeightLowerBound || 
				biomeHeightBase > config.dungeonBiomeHeightUpperBound)
			return;
		int level = config.getLevel(pos);
		if (level>=0) {
			random.setSeed(level<<8^world.getSeed());
			IBlockState[] bl = this.blockstateList[random.nextInt(this.blockstateList.length)];
			if(level < this.blockstateList.length) {
				bl = this.blockstateList[level];
			}
			int mobRandom = level<this.levelToMob.length?level:random.nextInt() & (this.levelToMob.length-1);
			DungeonCube is = getDungeonCubeType(pos, world, 0);
			if(is.equals(DungeonCube.NOTHING)){
				return;
			}
			byte[] data = is.data;
			Class<? extends EntityLivingBase>[] mobs = this.levelToMob[mobRandom];
			for(int index=0;index<data.length;index++) {
				int dx = index >>> 8;
				int dy = (index >>> 4) & 15;
				int dz = index & 15;
				int bstate = Byte.toUnsignedInt(data[index]);
				BlockPos bpos = new BlockPos(pos.getMinBlockX()+dx,pos.getMinBlockY()+dy,pos.getMinBlockZ()+dz);
				if (bstate == 255) {
					world.setBlockState(bpos, AIR, 0);
					int mobRandom2 = this.random.nextFloat()>0.7f?1:0;
					Class<? extends EntityLivingBase> mob = mobs[mobRandom2];
					EntityLivingBase mobEntity;
					try {
						mobEntity = mob.getDeclaredConstructor(World.class).newInstance(world);
						mobEntity.setPosition(pos.getMinBlockX()+dx+0.5, pos.getMinBlockY()+dy, pos.getMinBlockZ()+dz+0.5);
						if(mob == LabyrinthEntities.STRAY || mob == LabyrinthEntities.SKELETON)
							mobEntity.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, new ItemStack(Items.BOW));
						else if(mob == LabyrinthEntities.SLIME || mob == LabyrinthEntities.MAGMA_CUBE)
							((ISlime)mobEntity).setSlimeSize(LevelUtil.getSlimeSize(level));
						else if(mob == LabyrinthEntities.VINDICATOR)
							mobEntity.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, new ItemStack(Items.IRON_AXE));
						((IMobLeveled) mobEntity).setLevel(level);
						((IMobLeveled) mobEntity).setLootTable(new ResourceLocation(LabyrinthMod.MODID+":dungeon_loot_level_"+(level > max_loot_level ? max_loot_level : level)));
						world.spawnEntity(mobEntity);
					} catch (Throwable e) {
						e.printStackTrace();
					}
				} else {
					world.setBlockState(bpos, bl[bstate], 0);
					if(bstate>=3 && bstate<=6){
						TileEntityChest chest = new TileEntityChest();
						NBTTagCompound compound = new NBTTagCompound();
						compound.setString("LootTable",LabyrinthMod.MODID+":dungeon_loot_level_"+(level > max_loot_level ? max_loot_level : level));
						chest.readFromNBT(compound);
						chest.markDirty();
						chest.setPos(bpos);
						world.setTileEntity(bpos, chest);
					}
				}
			}
		}
	}

	private DungeonCube getDungeonCubeType(CubePos cpos, ICubicWorld world, int deep) {
		if(deep++ > 8)
			return DungeonCube.NOTHING;
		DungeonCube cached_value = dtype_cache.get(cpos);
		if(cached_value!=null) {
			return cached_value;
		}
		random.setSeed(world.getSeed()^cpos.getX()<<8^cpos.getY()<<4^cpos.getZ());
		int typedefiner = random.nextInt(randomDungeonsArray.length);
		if(dtype_cache.size()>CACHE_SIZE){
			dtype_cache.clear();
		}
		DungeonCube d_down = getDungeonCubeType(cpos.sub(0, 1, 0), world, deep);
		DungeonCube d_up = getDungeonCubeType(cpos.add(0, 1, 0), world, deep);
		DungeonCube d_east = getDungeonCubeType(cpos.add(1, 0, 0), world, deep);
		DungeonCube d_west = getDungeonCubeType(cpos.sub(1, 0, 0), world, deep);
		DungeonCube d_south = getDungeonCubeType(cpos.add(0, 0, 1), world, deep);
		DungeonCube d_north = getDungeonCubeType(cpos.sub(0, 0, 1), world, deep);
		
		//Down
		if (d_down == DungeonCube.COLUMN_FLOOR) {
			dtype_cache.put(cpos, DungeonCube.COLUMN_CEIL);
			return DungeonCube.COLUMN_CEIL;
		} else if (d_down == DungeonCube.STAIR_FLOOR) {
			dtype_cache.put(cpos, DungeonCube.STAIR_CEIL);
			return DungeonCube.STAIR_CEIL;
		}
		
		//Up
		if (d_up == DungeonCube.COLUMN_CEIL) {
			dtype_cache.put(cpos, DungeonCube.COLUMN_FLOOR);
			return DungeonCube.COLUMN_FLOOR;
		} else if (d_up == DungeonCube.STAIR_CEIL) {
			dtype_cache.put(cpos, DungeonCube.STAIR_FLOOR);
			return DungeonCube.STAIR_FLOOR;
		}
		
		int pn = 0;
		if(d_east == DungeonCube.COLUMN_CEIL)
			pn+=2;
		if(d_west == DungeonCube.COLUMN_CEIL)
			pn+=2;
		if(d_south == DungeonCube.COLUMN_CEIL)
			pn+=2;
		if(d_north == DungeonCube.COLUMN_CEIL)
			pn+=2;
		if(d_east.isWestWall)
			pn++;
		if(d_west.isEastWall)
			pn++;
		if(d_south.isNorthWall)
			pn++;
		if(d_north.isSouthWall)
			pn++;
		if(pn>2){
			switch (typedefiner % 3) {
			case 0:
				if(d_south != DungeonCube.COLUMN_CEIL) {
					dtype_cache.put(cpos, DungeonCube.WORKSHOP);
					return DungeonCube.WORKSHOP;
				}
			case 1:
				dtype_cache.put(cpos, DungeonCube.NODE_WITH_CHEST);
				return DungeonCube.NODE_WITH_CHEST;
			case 2:
				dtype_cache.put(cpos, DungeonCube.NODE);
				return DungeonCube.NODE;
			}
		}
		//East
		if (d_east == DungeonCube.COLUMN_CEIL) {
			dtype_cache.put(cpos, DungeonCube.COLUMN_EAST_BORDER);
			return DungeonCube.COLUMN_EAST_BORDER;
		} else if (d_east.isWestWall) {
			if(d_west.isEastWall){
				dtype_cache.put(cpos, DungeonCube.WALL_WEST_EAST_BARS);
				return DungeonCube.WALL_WEST_EAST_BARS;
			}
			else if(d_south.isNorthWall){
				dtype_cache.put(cpos, DungeonCube.WALL_EAST_SOUTH_BARS);
				return DungeonCube.WALL_EAST_SOUTH_BARS;
			}
			else if(d_north.isSouthWall){
				dtype_cache.put(cpos, DungeonCube.WALL_EAST_NORTH_BARS);
				return DungeonCube.WALL_EAST_NORTH_BARS;
			}
			switch (typedefiner % 6) {
			case 0:
				dtype_cache.put(cpos, DungeonCube.WALL_EAST_NORTH);
				return DungeonCube.WALL_EAST_SOUTH;
			case 1:
				dtype_cache.put(cpos, DungeonCube.WALL_EAST_NORTH_BARS);
				return DungeonCube.WALL_EAST_NORTH_BARS;
			case 2:
				dtype_cache.put(cpos, DungeonCube.WALL_EAST_SOUTH);
				return DungeonCube.WALL_EAST_SOUTH;
			case 3:
				dtype_cache.put(cpos, DungeonCube.WALL_EAST_SOUTH_BARS);
				return DungeonCube.WALL_EAST_SOUTH_BARS;
			case 4:
				dtype_cache.put(cpos, DungeonCube.WALL_WEST_EAST);
				return DungeonCube.WALL_WEST_EAST;
			case 5:
				dtype_cache.put(cpos, DungeonCube.WALL_WEST_EAST_BARS);
				return DungeonCube.WALL_WEST_EAST_BARS;
			}
		}
		//West
		if (d_west == DungeonCube.COLUMN_CEIL) {
			dtype_cache.put(cpos, DungeonCube.COLUMN_WEST_BORDER);
			return DungeonCube.COLUMN_WEST_BORDER;
		} else if (d_west.isEastWall) {
			if(d_east.isWestWall){
				dtype_cache.put(cpos, DungeonCube.WALL_WEST_EAST_BARS);
				return DungeonCube.WALL_WEST_EAST_BARS;
			}
			else if(d_south.isNorthWall){
				dtype_cache.put(cpos, DungeonCube.WALL_WEST_SOUTH_BARS);
				return DungeonCube.WALL_EAST_SOUTH_BARS;
			}
			else if(d_north.isSouthWall){
				dtype_cache.put(cpos, DungeonCube.WALL_WEST_NORTH_BARS);
				return DungeonCube.WALL_WEST_NORTH_BARS;
			}
			switch (typedefiner % 6) {
			case 0:
				dtype_cache.put(cpos, DungeonCube.WALL_WEST_NORTH);
				return DungeonCube.WALL_WEST_NORTH;
			case 1:
				dtype_cache.put(cpos, DungeonCube.WALL_WEST_NORTH_BARS);
				return DungeonCube.WALL_WEST_NORTH_BARS;
			case 2:
				dtype_cache.put(cpos, DungeonCube.WALL_WEST_SOUTH);
				return DungeonCube.WALL_WEST_SOUTH;
			case 3:
				dtype_cache.put(cpos, DungeonCube.WALL_WEST_SOUTH_BARS);
				return DungeonCube.WALL_WEST_SOUTH_BARS;
			case 4:
				dtype_cache.put(cpos, DungeonCube.WALL_WEST_EAST);
				return DungeonCube.WALL_WEST_EAST;
			case 5:
				dtype_cache.put(cpos, DungeonCube.WALL_WEST_EAST_BARS);
				return DungeonCube.WALL_WEST_EAST_BARS;
			}
		}
		//South
		if (d_south == DungeonCube.COLUMN_CEIL) {
			dtype_cache.put(cpos, DungeonCube.COLUMN_SOUTH_BORDER);
			return DungeonCube.COLUMN_SOUTH_BORDER;
		} else if (d_south.isNorthWall) {
			if(d_east.isWestWall){
				dtype_cache.put(cpos, DungeonCube.WALL_WEST_NORTH_BARS);
				return DungeonCube.WALL_WEST_NORTH_BARS;
			}
			else if(d_west.isEastWall){
				dtype_cache.put(cpos, DungeonCube.WALL_EAST_NORTH_BARS);
				return DungeonCube.WALL_EAST_NORTH_BARS;
			}
			else if(d_north.isSouthWall){
				dtype_cache.put(cpos, DungeonCube.WALL_SOUTH_NORTH_DOOR);
				return DungeonCube.WALL_SOUTH_NORTH_DOOR;
			}
			switch (typedefiner % 6) {
			case 0:
				dtype_cache.put(cpos, DungeonCube.WALL_SOUTH_NORTH);
				return DungeonCube.WALL_SOUTH_NORTH;
			case 1:
				dtype_cache.put(cpos, DungeonCube.WALL_EAST_SOUTH_BARS);
				return DungeonCube.WALL_EAST_SOUTH_BARS;
			case 2:
				dtype_cache.put(cpos, DungeonCube.WALL_SOUTH_NORTH_DOOR);
				return DungeonCube.WALL_SOUTH_NORTH_DOOR;
			case 3:
				dtype_cache.put(cpos, DungeonCube.WALL_WEST_SOUTH_BARS);
				return DungeonCube.WALL_WEST_SOUTH_BARS;
			case 4:
				dtype_cache.put(cpos, DungeonCube.WALL_WEST_SOUTH);
				return DungeonCube.WALL_WEST_SOUTH;
			case 5:
				dtype_cache.put(cpos, DungeonCube.WALL_EAST_SOUTH);
				return DungeonCube.WALL_EAST_SOUTH;
			}
		}
		//North
		if (d_north == DungeonCube.COLUMN_CEIL) {
			dtype_cache.put(cpos, DungeonCube.COLUMN_NORTH_BORDER);
			return DungeonCube.COLUMN_NORTH_BORDER;
		} else if (d_north.isSouthWall) {
			if(d_east.isWestWall){
				dtype_cache.put(cpos, DungeonCube.WALL_WEST_SOUTH_BARS);
				return DungeonCube.WALL_WEST_SOUTH_BARS;
			}
			else if(d_west.isEastWall){
				dtype_cache.put(cpos, DungeonCube.WALL_EAST_SOUTH_BARS);
				return DungeonCube.WALL_EAST_SOUTH_BARS;
			}
			else if(d_south.isNorthWall){
				dtype_cache.put(cpos, DungeonCube.WALL_SOUTH_NORTH_DOOR);
				return DungeonCube.WALL_SOUTH_NORTH_DOOR;
			}
			switch (typedefiner % 7) {
			case 0:
				dtype_cache.put(cpos, DungeonCube.WALL_SOUTH_NORTH);
				return DungeonCube.WALL_SOUTH_NORTH;
			case 1:
				dtype_cache.put(cpos, DungeonCube.WALL_EAST_NORTH_BARS);
				return DungeonCube.WALL_EAST_NORTH_BARS;
			case 2:
				dtype_cache.put(cpos, DungeonCube.WALL_SOUTH_NORTH_DOOR);
				return DungeonCube.WALL_SOUTH_NORTH_DOOR;
			case 3:
				dtype_cache.put(cpos, DungeonCube.WALL_WEST_NORTH_BARS);
				return DungeonCube.WALL_WEST_NORTH_BARS;
			case 4:
				dtype_cache.put(cpos, DungeonCube.WALL_WEST_NORTH);
				return DungeonCube.WALL_WEST_NORTH;
			case 5:
				dtype_cache.put(cpos, DungeonCube.WALL_EAST_NORTH);
				return DungeonCube.WALL_EAST_NORTH;
			}
		}
		return randomDungeonsArray[typedefiner];
	}

	public void setConfig(WorldSavedDataLabyrinthConfig worldgenConfigIn) {
		this.config=worldgenConfigIn;
	}
}
