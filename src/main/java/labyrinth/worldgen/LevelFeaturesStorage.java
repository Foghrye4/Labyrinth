package labyrinth.worldgen;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Arrays;
import java.util.Random;

import com.google.common.base.Optional;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import cubicchunks.asm.CubicChunksMixinConfig.BoolOptions;
import cubicchunks.worldgen.generator.flat.Layer;
import labyrinth.init.LabyrinthBlocks;
import labyrinth.init.LabyrinthEntities;
import labyrinth.util.ModIntegrationUtil;
import net.minecraft.block.Block;
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
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.BlockStone;
import net.minecraft.block.BlockStoneBrick;
import net.minecraft.block.BlockTorch;
import net.minecraft.block.BlockWoodSlab;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.util.EnumFacing;

public class LevelFeaturesStorage {
	public IBlockState[][] blockstateList = new IBlockState[128][256];

	private final Random random = new Random();
	private final IBlockState[] WALL_CANDIDATES = new IBlockState[] {
			LabyrinthBlocks.STONE.getDefaultState().withProperty(BlockStone.VARIANT, BlockStone.EnumType.ANDESITE),
			LabyrinthBlocks.STONE.getDefaultState().withProperty(BlockStone.VARIANT,
					BlockStone.EnumType.ANDESITE_SMOOTH),
			LabyrinthBlocks.STONE.getDefaultState().withProperty(BlockStone.VARIANT, BlockStone.EnumType.GRANITE),
			LabyrinthBlocks.STONE.getDefaultState().withProperty(BlockStone.VARIANT,
					BlockStone.EnumType.GRANITE_SMOOTH),
			LabyrinthBlocks.STONE.getDefaultState().withProperty(BlockStone.VARIANT, BlockStone.EnumType.DIORITE),
			LabyrinthBlocks.STONE.getDefaultState().withProperty(BlockStone.VARIANT,
					BlockStone.EnumType.DIORITE_SMOOTH),
			Blocks.QUARTZ_BLOCK.getDefaultState(),
			Blocks.QUARTZ_BLOCK.getDefaultState().withProperty(BlockQuartz.VARIANT, BlockQuartz.EnumType.CHISELED),
			Blocks.QUARTZ_BLOCK.getDefaultState().withProperty(BlockQuartz.VARIANT, BlockQuartz.EnumType.LINES_X),
			Blocks.QUARTZ_BLOCK.getDefaultState().withProperty(BlockQuartz.VARIANT, BlockQuartz.EnumType.LINES_Y),
			Blocks.QUARTZ_BLOCK.getDefaultState().withProperty(BlockQuartz.VARIANT, BlockQuartz.EnumType.LINES_Z),
			Blocks.COBBLESTONE.getDefaultState(), Blocks.SANDSTONE.getDefaultState(),
			Blocks.SANDSTONE.getDefaultState().withProperty(BlockSandStone.TYPE, BlockSandStone.EnumType.CHISELED),
			Blocks.SANDSTONE.getDefaultState().withProperty(BlockSandStone.TYPE, BlockSandStone.EnumType.SMOOTH),
			Blocks.BRICK_BLOCK.getDefaultState(), Blocks.MOSSY_COBBLESTONE.getDefaultState(),
			Blocks.OBSIDIAN.getDefaultState(), Blocks.SOUL_SAND.getDefaultState(), Blocks.NETHERRACK.getDefaultState(),
			Blocks.STONEBRICK.getDefaultState(),
			Blocks.STONEBRICK.getDefaultState().withProperty(BlockStoneBrick.VARIANT,
					BlockStoneBrick.EnumType.CHISELED),
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
			Blocks.COAL_BLOCK.getDefaultState(), Blocks.RED_SANDSTONE.getDefaultState(),
			Blocks.RED_SANDSTONE.getDefaultState().withProperty(BlockRedSandstone.TYPE,
					BlockRedSandstone.EnumType.CHISELED),
			Blocks.RED_SANDSTONE.getDefaultState().withProperty(BlockRedSandstone.TYPE,
					BlockRedSandstone.EnumType.SMOOTH),
			Blocks.END_BRICKS.getDefaultState(), Blocks.NETHER_WART_BLOCK.getDefaultState(),
			Blocks.RED_NETHER_BRICK.getDefaultState(), Blocks.BONE_BLOCK.getDefaultState(), };

	private final IBlockState[] FLOOR_CANDIDATES = new IBlockState[] { LabyrinthBlocks.STONE.getDefaultState(),
			LabyrinthBlocks.STONE.getDefaultState().withProperty(BlockStone.VARIANT, BlockStone.EnumType.ANDESITE),
			LabyrinthBlocks.STONE.getDefaultState().withProperty(BlockStone.VARIANT,
					BlockStone.EnumType.ANDESITE_SMOOTH),
			LabyrinthBlocks.STONE.getDefaultState().withProperty(BlockStone.VARIANT, BlockStone.EnumType.GRANITE),
			LabyrinthBlocks.STONE.getDefaultState().withProperty(BlockStone.VARIANT,
					BlockStone.EnumType.GRANITE_SMOOTH),
			LabyrinthBlocks.STONE.getDefaultState().withProperty(BlockStone.VARIANT, BlockStone.EnumType.DIORITE),
			LabyrinthBlocks.STONE.getDefaultState().withProperty(BlockStone.VARIANT,
					BlockStone.EnumType.DIORITE_SMOOTH),
			Blocks.IRON_BLOCK.getDefaultState(),
			Blocks.GOLD_BLOCK.getDefaultState(),
			Blocks.QUARTZ_BLOCK.getDefaultState(),
			Blocks.QUARTZ_BLOCK.getDefaultState().withProperty(BlockQuartz.VARIANT, BlockQuartz.EnumType.CHISELED),
			Blocks.QUARTZ_BLOCK.getDefaultState().withProperty(BlockQuartz.VARIANT, BlockQuartz.EnumType.LINES_X),
			Blocks.QUARTZ_BLOCK.getDefaultState().withProperty(BlockQuartz.VARIANT, BlockQuartz.EnumType.LINES_Y),
			Blocks.QUARTZ_BLOCK.getDefaultState().withProperty(BlockQuartz.VARIANT, BlockQuartz.EnumType.LINES_Z),
			Blocks.SANDSTONE.getDefaultState().withProperty(BlockSandStone.TYPE, BlockSandStone.EnumType.CHISELED),
			Blocks.OBSIDIAN.getDefaultState(), Blocks.SOUL_SAND.getDefaultState(), Blocks.NETHERRACK.getDefaultState(),
			Blocks.STONEBRICK.getDefaultState(),
			Blocks.STONEBRICK.getDefaultState().withProperty(BlockStoneBrick.VARIANT,
					BlockStoneBrick.EnumType.CHISELED),
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
			Blocks.COAL_BLOCK.getDefaultState(), Blocks.RED_SANDSTONE.getDefaultState(),
			Blocks.RED_SANDSTONE.getDefaultState().withProperty(BlockRedSandstone.TYPE,
					BlockRedSandstone.EnumType.CHISELED),
			Blocks.RED_SANDSTONE.getDefaultState().withProperty(BlockRedSandstone.TYPE,
					BlockRedSandstone.EnumType.SMOOTH),
			Blocks.END_BRICKS.getDefaultState(), Blocks.NETHER_WART_BLOCK.getDefaultState(),
			Blocks.RED_NETHER_BRICK.getDefaultState(), Blocks.BONE_BLOCK.getDefaultState(), };

	private final IBlockState[] STAIR_CANDIDATES = new IBlockState[] {
			Blocks.QUARTZ_STAIRS.getBlockState().getBaseState().withProperty(BlockStairs.SHAPE, EnumShape.STRAIGHT),
			Blocks.STONE_STAIRS.getBlockState().getBaseState().withProperty(BlockStairs.SHAPE, EnumShape.STRAIGHT),
			Blocks.STONE_BRICK_STAIRS.getBlockState().getBaseState().withProperty(BlockStairs.SHAPE,
					EnumShape.STRAIGHT),
			Blocks.NETHER_BRICK_STAIRS.getBlockState().getBaseState().withProperty(BlockStairs.SHAPE,
					EnumShape.STRAIGHT),
			Blocks.SANDSTONE_STAIRS.getBlockState().getBaseState().withProperty(BlockStairs.SHAPE, EnumShape.STRAIGHT),
			Blocks.RED_SANDSTONE_STAIRS.getBlockState().getBaseState().withProperty(BlockStairs.SHAPE,
					EnumShape.STRAIGHT), };
	private final IBlockState[] WINDOW_CANDIDATES = new IBlockState[] { 
			Blocks.STAINED_GLASS_PANE.getDefaultState().withProperty(BlockStainedGlassPane.COLOR, EnumDyeColor.RED),
			Blocks.STAINED_GLASS_PANE.getDefaultState().withProperty(BlockStainedGlassPane.COLOR, EnumDyeColor.BLACK),
			Blocks.STAINED_GLASS_PANE.getDefaultState().withProperty(BlockStainedGlassPane.COLOR, EnumDyeColor.BLUE),
			Blocks.STAINED_GLASS_PANE.getDefaultState().withProperty(BlockStainedGlassPane.COLOR, EnumDyeColor.BROWN),
			Blocks.STAINED_GLASS_PANE.getDefaultState().withProperty(BlockStainedGlassPane.COLOR, EnumDyeColor.CYAN),
			Blocks.STAINED_GLASS_PANE.getDefaultState().withProperty(BlockStainedGlassPane.COLOR, EnumDyeColor.GRAY),
			Blocks.STAINED_GLASS_PANE.getDefaultState().withProperty(BlockStainedGlassPane.COLOR, EnumDyeColor.GREEN),
			Blocks.STAINED_GLASS_PANE.getDefaultState().withProperty(BlockStainedGlassPane.COLOR, EnumDyeColor.LIME),
			Blocks.STAINED_GLASS_PANE.getDefaultState().withProperty(BlockStainedGlassPane.COLOR, EnumDyeColor.LIGHT_BLUE),
			Blocks.STAINED_GLASS_PANE.getDefaultState().withProperty(BlockStainedGlassPane.COLOR, EnumDyeColor.MAGENTA),
			Blocks.STAINED_GLASS_PANE.getDefaultState().withProperty(BlockStainedGlassPane.COLOR, EnumDyeColor.ORANGE),
			Blocks.STAINED_GLASS_PANE.getDefaultState().withProperty(BlockStainedGlassPane.COLOR, EnumDyeColor.PINK),
			Blocks.STAINED_GLASS_PANE.getDefaultState().withProperty(BlockStainedGlassPane.COLOR, EnumDyeColor.PURPLE),
			Blocks.STAINED_GLASS_PANE.getDefaultState().withProperty(BlockStainedGlassPane.COLOR, EnumDyeColor.SILVER),
			Blocks.STAINED_GLASS_PANE.getDefaultState().withProperty(BlockStainedGlassPane.COLOR, EnumDyeColor.WHITE),
			Blocks.STAINED_GLASS_PANE.getDefaultState().withProperty(BlockStainedGlassPane.COLOR, EnumDyeColor.YELLOW),
			Blocks.NETHER_BRICK_FENCE.getDefaultState(), 
			Blocks.IRON_BARS.getDefaultState()
			};
	private final IBlockState[] FENCE_CANDIDATES = new IBlockState[] { Blocks.OAK_FENCE.getDefaultState(),
			Blocks.NETHER_BRICK_FENCE.getDefaultState(), Blocks.DARK_OAK_FENCE.getDefaultState(),
			Blocks.ACACIA_FENCE.getDefaultState(), Blocks.BIRCH_FENCE.getDefaultState(),
			Blocks.COBBLESTONE_WALL.getDefaultState() };

	@SuppressWarnings("unchecked")
	private final Class<? extends EntityLivingBase>[] MOB_CANDIDATES_FIRST = new Class[] { LabyrinthEntities.ZOMBIE,
			LabyrinthEntities.CAVE_SPIDER, LabyrinthEntities.CREEPER, LabyrinthEntities.ENDERMAN,
			LabyrinthEntities.ENDERMITE, LabyrinthEntities.MAGMA_CUBE, LabyrinthEntities.PIG_ZOMBIE,
			LabyrinthEntities.SPIDER, LabyrinthEntities.SLIME, LabyrinthEntities.WITHER_SKELETON };

	@SuppressWarnings("unchecked")
	private final Class<? extends EntityLivingBase>[] MOB_CANDIDATES_SECOND = new Class[] { LabyrinthEntities.BLAZE,
			LabyrinthEntities.SKELETON, LabyrinthEntities.STRAY, LabyrinthEntities.VINDICATOR, LabyrinthEntities.WITCH,
			LabyrinthEntities.VEX };
	@SuppressWarnings("unchecked")
	final Class<? extends EntityLivingBase>[][] levelToMob = new Class[128][2];
	
	public LevelFeaturesStorage(){
		for (Class<? extends EntityLivingBase>[] levelMobs : levelToMob) {
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
		blockstateList[0][2] = Blocks.QUARTZ_BLOCK.getDefaultState().withProperty(BlockQuartz.VARIANT,
				BlockQuartz.EnumType.CHISELED);
		blockstateList[0][3] = Blocks.CHEST.getDefaultState().withProperty(BlockChest.FACING, EnumFacing.NORTH);
		blockstateList[0][4] = Blocks.CHEST.getDefaultState().withProperty(BlockChest.FACING, EnumFacing.SOUTH);
		blockstateList[0][5] = Blocks.CHEST.getDefaultState().withProperty(BlockChest.FACING, EnumFacing.WEST);
		blockstateList[0][6] = Blocks.CHEST.getDefaultState().withProperty(BlockChest.FACING, EnumFacing.EAST);
		blockstateList[0][7] = Blocks.FURNACE.getDefaultState().withProperty(BlockFurnace.FACING, EnumFacing.NORTH);
		blockstateList[0][8] = Blocks.FURNACE.getDefaultState().withProperty(BlockFurnace.FACING, EnumFacing.SOUTH);
		blockstateList[0][9] = Blocks.FURNACE.getDefaultState().withProperty(BlockFurnace.FACING, EnumFacing.WEST);
		blockstateList[0][10] = Blocks.FURNACE.getDefaultState().withProperty(BlockFurnace.FACING, EnumFacing.EAST);
		blockstateList[0][11] = Blocks.CRAFTING_TABLE.getDefaultState();
		blockstateList[0][12] = ModIntegrationUtil.getBlockDefaultStateIfNotNull("aov:blocks/blockangelic",
				Blocks.CAULDRON.getBlockState().getBaseState().withProperty(BlockCauldron.LEVEL, Integer.valueOf(3)));
		blockstateList[0][13] = Blocks.BREWING_STAND.getDefaultState();
		blockstateList[0][14] = Blocks.COAL_BLOCK.getDefaultState();
		blockstateList[0][15] = Blocks.WOODEN_SLAB.getDefaultState()
				.withProperty(BlockSlab.HALF, BlockSlab.EnumBlockHalf.TOP)
				.withProperty(BlockWoodSlab.VARIANT, BlockPlanks.EnumType.OAK);
		blockstateList[0][16] = Blocks.LAVA.getDefaultState();
		blockstateList[0][17] = Blocks.WATER.getDefaultState();
		blockstateList[0][18] = Blocks.STAINED_GLASS_PANE.getDefaultState().withProperty(BlockStainedGlassPane.COLOR,
				EnumDyeColor.RED);
		IBlockState stair = Blocks.QUARTZ_STAIRS.getDefaultState().withProperty(BlockStairs.SHAPE, EnumShape.STRAIGHT);
		addStairs(blockstateList[0], stair);
		blockstateList[0][57] = Blocks.WOODEN_SLAB.getDefaultState()
				.withProperty(BlockSlab.HALF, BlockSlab.EnumBlockHalf.BOTTOM)
				.withProperty(BlockWoodSlab.VARIANT, BlockPlanks.EnumType.OAK);

		blockstateList[0][58] = Blocks.DARK_OAK_DOOR.getDefaultState().withProperty(BlockDoor.FACING, EnumFacing.WEST)
				.withProperty(BlockDoor.HALF, BlockDoor.EnumDoorHalf.UPPER)
				.withProperty(BlockDoor.HINGE, BlockDoor.EnumHingePosition.LEFT)
				.withProperty(BlockDoor.OPEN, Boolean.valueOf(false))
				.withProperty(BlockDoor.POWERED, Boolean.valueOf(false));
		blockstateList[0][59] = Blocks.DARK_OAK_DOOR.getDefaultState().withProperty(BlockDoor.FACING, EnumFacing.WEST)
				.withProperty(BlockDoor.HALF, BlockDoor.EnumDoorHalf.UPPER)
				.withProperty(BlockDoor.HINGE, BlockDoor.EnumHingePosition.RIGHT)
				.withProperty(BlockDoor.OPEN, Boolean.valueOf(false))
				.withProperty(BlockDoor.POWERED, Boolean.valueOf(false));
		blockstateList[0][60] = Blocks.DARK_OAK_DOOR.getDefaultState().withProperty(BlockDoor.FACING, EnumFacing.WEST)
				.withProperty(BlockDoor.HALF, BlockDoor.EnumDoorHalf.LOWER)
				.withProperty(BlockDoor.HINGE, BlockDoor.EnumHingePosition.LEFT)
				.withProperty(BlockDoor.OPEN, Boolean.valueOf(false))
				.withProperty(BlockDoor.POWERED, Boolean.valueOf(false));
		blockstateList[0][61] = Blocks.DARK_OAK_DOOR.getDefaultState().withProperty(BlockDoor.FACING, EnumFacing.WEST)
				.withProperty(BlockDoor.HALF, BlockDoor.EnumDoorHalf.LOWER)
				.withProperty(BlockDoor.HINGE, BlockDoor.EnumHingePosition.RIGHT)
				.withProperty(BlockDoor.OPEN, Boolean.valueOf(false))
				.withProperty(BlockDoor.POWERED, Boolean.valueOf(false));
		blockstateList[0][62] = Blocks.DARK_OAK_DOOR.getDefaultState().withProperty(BlockDoor.FACING, EnumFacing.EAST)
				.withProperty(BlockDoor.HALF, BlockDoor.EnumDoorHalf.UPPER)
				.withProperty(BlockDoor.HINGE, BlockDoor.EnumHingePosition.LEFT)
				.withProperty(BlockDoor.OPEN, Boolean.valueOf(false))
				.withProperty(BlockDoor.POWERED, Boolean.valueOf(false));
		blockstateList[0][63] = Blocks.DARK_OAK_DOOR.getDefaultState().withProperty(BlockDoor.FACING, EnumFacing.EAST)
				.withProperty(BlockDoor.HALF, BlockDoor.EnumDoorHalf.UPPER)
				.withProperty(BlockDoor.HINGE, BlockDoor.EnumHingePosition.RIGHT)
				.withProperty(BlockDoor.OPEN, Boolean.valueOf(false))
				.withProperty(BlockDoor.POWERED, Boolean.valueOf(false));
		blockstateList[0][64] = Blocks.DARK_OAK_DOOR.getDefaultState().withProperty(BlockDoor.FACING, EnumFacing.EAST)
				.withProperty(BlockDoor.HALF, BlockDoor.EnumDoorHalf.LOWER)
				.withProperty(BlockDoor.HINGE, BlockDoor.EnumHingePosition.LEFT)
				.withProperty(BlockDoor.OPEN, Boolean.valueOf(false))
				.withProperty(BlockDoor.POWERED, Boolean.valueOf(false));
		blockstateList[0][65] = Blocks.DARK_OAK_DOOR.getDefaultState().withProperty(BlockDoor.FACING, EnumFacing.EAST)
				.withProperty(BlockDoor.HALF, BlockDoor.EnumDoorHalf.LOWER)
				.withProperty(BlockDoor.HINGE, BlockDoor.EnumHingePosition.RIGHT)
				.withProperty(BlockDoor.OPEN, Boolean.valueOf(false))
				.withProperty(BlockDoor.POWERED, Boolean.valueOf(false));

		blockstateList[0][116] = Blocks.ANVIL.getDefaultState().withProperty(BlockAnvil.FACING, EnumFacing.NORTH)
				.withProperty(BlockAnvil.DAMAGE, Integer.valueOf(0));
		blockstateList[0][117] = Blocks.ANVIL.getDefaultState().withProperty(BlockAnvil.FACING, EnumFacing.SOUTH)
				.withProperty(BlockAnvil.DAMAGE, Integer.valueOf(0));
		blockstateList[0][118] = Blocks.ANVIL.getDefaultState().withProperty(BlockAnvil.FACING, EnumFacing.WEST)
				.withProperty(BlockAnvil.DAMAGE, Integer.valueOf(0));
		blockstateList[0][119] = Blocks.ANVIL.getDefaultState().withProperty(BlockAnvil.FACING, EnumFacing.EAST)
				.withProperty(BlockAnvil.DAMAGE, Integer.valueOf(0));
		blockstateList[0][120] = Blocks.ACACIA_FENCE.getDefaultState();
		blockstateList[0][129] = Blocks.STICKY_PISTON.getDefaultState()
				.withProperty(BlockPistonBase.EXTENDED, Boolean.valueOf(true))
				.withProperty(BlockPistonBase.FACING, EnumFacing.NORTH);
		blockstateList[0][130] = Blocks.PISTON_HEAD.getDefaultState()
				.withProperty(BlockPistonExtension.TYPE, BlockPistonExtension.EnumPistonType.STICKY)
				.withProperty(BlockPistonExtension.FACING, EnumFacing.NORTH)
				.withProperty(BlockPistonExtension.SHORT, Boolean.valueOf(false));
		blockstateList[0][137] = Blocks.LEVER.getDefaultState()
				.withProperty(BlockLever.FACING, BlockLever.EnumOrientation.EAST)
				.withProperty(BlockLever.POWERED, Boolean.valueOf(true));
		blockstateList[0][153] = Blocks.TORCH.getDefaultState().withProperty(BlockTorch.FACING, EnumFacing.UP);
		blockstateList[0][154] = Blocks.TORCH.getDefaultState().withProperty(BlockTorch.FACING, EnumFacing.NORTH);
		blockstateList[0][155] = Blocks.TORCH.getDefaultState().withProperty(BlockTorch.FACING, EnumFacing.SOUTH);
		blockstateList[0][156] = Blocks.TORCH.getDefaultState().withProperty(BlockTorch.FACING, EnumFacing.WEST);
		blockstateList[0][157] = Blocks.TORCH.getDefaultState().withProperty(BlockTorch.FACING, EnumFacing.EAST);
		blockstateList[0][158] = Blocks.DARK_OAK_DOOR.getDefaultState().withProperty(BlockDoor.FACING, EnumFacing.NORTH)
				.withProperty(BlockDoor.HALF, BlockDoor.EnumDoorHalf.UPPER)
				.withProperty(BlockDoor.HINGE, BlockDoor.EnumHingePosition.LEFT)
				.withProperty(BlockDoor.OPEN, Boolean.valueOf(false))
				.withProperty(BlockDoor.POWERED, Boolean.valueOf(false));
		blockstateList[0][159] = Blocks.DARK_OAK_DOOR.getDefaultState().withProperty(BlockDoor.FACING, EnumFacing.NORTH)
				.withProperty(BlockDoor.HALF, BlockDoor.EnumDoorHalf.UPPER)
				.withProperty(BlockDoor.HINGE, BlockDoor.EnumHingePosition.RIGHT)
				.withProperty(BlockDoor.OPEN, Boolean.valueOf(false))
				.withProperty(BlockDoor.POWERED, Boolean.valueOf(false));
		blockstateList[0][160] = Blocks.DARK_OAK_DOOR.getDefaultState().withProperty(BlockDoor.FACING, EnumFacing.NORTH)
				.withProperty(BlockDoor.HALF, BlockDoor.EnumDoorHalf.LOWER)
				.withProperty(BlockDoor.HINGE, BlockDoor.EnumHingePosition.LEFT)
				.withProperty(BlockDoor.OPEN, Boolean.valueOf(false))
				.withProperty(BlockDoor.POWERED, Boolean.valueOf(false));
		blockstateList[0][161] = Blocks.DARK_OAK_DOOR.getDefaultState().withProperty(BlockDoor.FACING, EnumFacing.NORTH)
				.withProperty(BlockDoor.HALF, BlockDoor.EnumDoorHalf.LOWER)
				.withProperty(BlockDoor.HINGE, BlockDoor.EnumHingePosition.RIGHT)
				.withProperty(BlockDoor.OPEN, Boolean.valueOf(false))
				.withProperty(BlockDoor.POWERED, Boolean.valueOf(false));
		blockstateList[0][162] = Blocks.DARK_OAK_DOOR.getDefaultState().withProperty(BlockDoor.FACING, EnumFacing.SOUTH)
				.withProperty(BlockDoor.HALF, BlockDoor.EnumDoorHalf.UPPER)
				.withProperty(BlockDoor.HINGE, BlockDoor.EnumHingePosition.LEFT)
				.withProperty(BlockDoor.OPEN, Boolean.valueOf(false))
				.withProperty(BlockDoor.POWERED, Boolean.valueOf(false));
		blockstateList[0][163] = Blocks.DARK_OAK_DOOR.getDefaultState().withProperty(BlockDoor.FACING, EnumFacing.SOUTH)
				.withProperty(BlockDoor.HALF, BlockDoor.EnumDoorHalf.UPPER)
				.withProperty(BlockDoor.HINGE, BlockDoor.EnumHingePosition.RIGHT)
				.withProperty(BlockDoor.OPEN, Boolean.valueOf(false))
				.withProperty(BlockDoor.POWERED, Boolean.valueOf(false));
		blockstateList[0][164] = Blocks.DARK_OAK_DOOR.getDefaultState().withProperty(BlockDoor.FACING, EnumFacing.SOUTH)
				.withProperty(BlockDoor.HALF, BlockDoor.EnumDoorHalf.LOWER)
				.withProperty(BlockDoor.HINGE, BlockDoor.EnumHingePosition.LEFT)
				.withProperty(BlockDoor.OPEN, Boolean.valueOf(false))
				.withProperty(BlockDoor.POWERED, Boolean.valueOf(false));
		blockstateList[0][165] = Blocks.DARK_OAK_DOOR.getDefaultState().withProperty(BlockDoor.FACING, EnumFacing.SOUTH)
				.withProperty(BlockDoor.HALF, BlockDoor.EnumDoorHalf.LOWER)
				.withProperty(BlockDoor.HINGE, BlockDoor.EnumHingePosition.RIGHT)
				.withProperty(BlockDoor.OPEN, Boolean.valueOf(false))
				.withProperty(BlockDoor.POWERED, Boolean.valueOf(false));
		blockstateList[0][166] = Blocks.BOOKSHELF.getDefaultState();
		blockstateList[0][167] = Blocks.LADDER.getDefaultState().withProperty(BlockLadder.FACING, EnumFacing.SOUTH);
		blockstateList[0][171] = Blocks.ENCHANTING_TABLE.getDefaultState();
		blockstateList[0][172] = Blocks.PLANKS.getDefaultState().withProperty(BlockPlanks.VARIANT, EnumType.OAK);
		blockstateList[0][173] = Blocks.OAK_STAIRS.getDefaultState().withProperty(BlockStairs.FACING, EnumFacing.SOUTH)
				.withProperty(BlockStairs.HALF, EnumHalf.BOTTOM);
		blockstateList[0][174] = Blocks.LOG.getDefaultState().withProperty(BlockLog.LOG_AXIS, EnumAxis.X);
		blockstateList[0][175] = Blocks.LOG.getDefaultState().withProperty(BlockLog.LOG_AXIS, EnumAxis.Y);
		blockstateList[0][176] = Blocks.LOG.getDefaultState().withProperty(BlockLog.LOG_AXIS, EnumAxis.Z);
		blockstateList[0][177] = Blocks.OAK_STAIRS.getDefaultState().withProperty(BlockStairs.FACING, EnumFacing.NORTH)
				.withProperty(BlockStairs.HALF, EnumHalf.BOTTOM);
		blockstateList[0][255] = Blocks.AIR.getDefaultState();

		for (int i = 1; i < blockstateList.length; i++)
			blockstateList[i] = Arrays.copyOf(blockstateList[0], 256);

		blockstateList[1][1] = LabyrinthBlocks.STONE.getDefaultState().withProperty(BlockStone.VARIANT,
				BlockStone.EnumType.GRANITE_SMOOTH);
		blockstateList[1][2] = Blocks.QUARTZ_BLOCK.getDefaultState().withProperty(BlockQuartz.VARIANT,
				BlockQuartz.EnumType.CHISELED);
		blockstateList[1][18] = Blocks.OAK_FENCE.getDefaultState();
		blockstateList[1][120] = Blocks.OAK_FENCE.getDefaultState();

		blockstateList[2][1] = LabyrinthBlocks.STONE.getDefaultState().withProperty(BlockStone.VARIANT,
				BlockStone.EnumType.DIORITE_SMOOTH);
		blockstateList[2][2] = LabyrinthBlocks.STONE.getDefaultState().withProperty(BlockStone.VARIANT,
				BlockStone.EnumType.DIORITE);
		blockstateList[2][18] = Blocks.IRON_BARS.getDefaultState();
		blockstateList[2][120] = Blocks.DARK_OAK_FENCE.getDefaultState();
		stair = Blocks.RED_SANDSTONE_STAIRS.getBlockState().getBaseState().withProperty(BlockStairs.SHAPE,
				EnumShape.STRAIGHT);
		addStairs(blockstateList[2], stair);

		blockstateList[3][1] = Blocks.STAINED_HARDENED_CLAY.getDefaultState().withProperty(BlockColored.COLOR,
				EnumDyeColor.LIGHT_BLUE);
		blockstateList[3][2] = Blocks.STAINED_HARDENED_CLAY.getDefaultState().withProperty(BlockColored.COLOR,
				EnumDyeColor.WHITE);
		blockstateList[3][18] = Blocks.IRON_BARS.getDefaultState();
		blockstateList[3][120] = Blocks.DARK_OAK_FENCE.getDefaultState();

		blockstateList[4][1] = Blocks.STONEBRICK.getDefaultState();
		blockstateList[4][2] = LabyrinthBlocks.STONE.getDefaultState();
		blockstateList[4][18] = Blocks.IRON_BARS.getDefaultState();
		blockstateList[4][120] = Blocks.COBBLESTONE_WALL.getDefaultState();
		stair = Blocks.STONE_BRICK_STAIRS.getBlockState().getBaseState().withProperty(BlockStairs.SHAPE,
				EnumShape.STRAIGHT);
		addStairs(blockstateList[4], stair);

		blockstateList[5][1] = Blocks.COBBLESTONE.getDefaultState();
		blockstateList[5][2] = LabyrinthBlocks.STONE.getDefaultState();
		blockstateList[5][18] = Blocks.IRON_BARS.getDefaultState();
		blockstateList[5][120] = Blocks.COBBLESTONE_WALL.getDefaultState();
		stair = Blocks.STONE_STAIRS.getBlockState().getBaseState().withProperty(BlockStairs.SHAPE, EnumShape.STRAIGHT);
		addStairs(blockstateList[5], stair);

		blockstateList[6][1] = Blocks.NETHER_BRICK.getDefaultState();
		blockstateList[6][2] = LabyrinthBlocks.STONE.getDefaultState().withProperty(BlockStone.VARIANT,
				BlockStone.EnumType.GRANITE_SMOOTH);
		blockstateList[6][18] = Blocks.IRON_BARS.getDefaultState();
		blockstateList[6][120] = Blocks.NETHER_BRICK_FENCE.getDefaultState();
		stair = Blocks.NETHER_BRICK_STAIRS.getBlockState().getBaseState().withProperty(BlockStairs.SHAPE,
				EnumShape.STRAIGHT);
		addStairs(blockstateList[6], stair);

		blockstateList[7][1] = Blocks.RED_NETHER_BRICK.getDefaultState();
		blockstateList[7][2] = Blocks.MAGMA.getDefaultState();
		blockstateList[7][18] = Blocks.NETHER_BRICK_FENCE.getDefaultState();
		blockstateList[7][120] = Blocks.NETHER_BRICK_FENCE.getDefaultState();
		stair = Blocks.NETHER_BRICK_STAIRS.getBlockState().getBaseState().withProperty(BlockStairs.SHAPE,
				EnumShape.STRAIGHT);
		addStairs(blockstateList[7], stair);

		for (int i = 8; i < blockstateList.length; i++) {
			blockstateList[i][1] = WALL_CANDIDATES[random.nextInt(WALL_CANDIDATES.length)];
			blockstateList[i][2] = FLOOR_CANDIDATES[random.nextInt(FLOOR_CANDIDATES.length)];
			blockstateList[i][18] = WINDOW_CANDIDATES[random.nextInt(WINDOW_CANDIDATES.length)];
			blockstateList[i][120] = FENCE_CANDIDATES[random.nextInt(FENCE_CANDIDATES.length)];
			stair = STAIR_CANDIDATES[random.nextInt(STAIR_CANDIDATES.length)];
			addStairs(blockstateList[i], stair);
		}
		
        File folder = new File(".", "config");
        folder.mkdirs();
        File configFile = new File(folder, "labyrinth_level_features_config.json");
        try {
            if (!configFile.exists())
                this.writeConfigToJson(configFile);
            this.readConfigFromJson(configFile);
        } catch (IOException e) {
            e.printStackTrace();
        }

	}

	private void addStairs(IBlockState[] blockstateIn, IBlockState stair) {
		blockstateIn[19] = stair.withProperty(BlockStairs.FACING, EnumFacing.NORTH).withProperty(BlockStairs.HALF,
				EnumHalf.TOP);
		blockstateIn[24] = stair.withProperty(BlockStairs.FACING, EnumFacing.NORTH).withProperty(BlockStairs.HALF,
				EnumHalf.BOTTOM);
		blockstateIn[25] = stair.withProperty(BlockStairs.FACING, EnumFacing.NORTH)
				.withProperty(BlockStairs.HALF, EnumHalf.BOTTOM).withProperty(BlockStairs.SHAPE, EnumShape.INNER_RIGHT);
		blockstateIn[26] = stair.withProperty(BlockStairs.FACING, EnumFacing.NORTH)
				.withProperty(BlockStairs.HALF, EnumHalf.BOTTOM).withProperty(BlockStairs.SHAPE, EnumShape.INNER_LEFT);
		blockstateIn[27] = stair.withProperty(BlockStairs.FACING, EnumFacing.NORTH)
				.withProperty(BlockStairs.HALF, EnumHalf.BOTTOM).withProperty(BlockStairs.SHAPE, EnumShape.OUTER_RIGHT);
		blockstateIn[28] = stair.withProperty(BlockStairs.FACING, EnumFacing.NORTH)
				.withProperty(BlockStairs.HALF, EnumHalf.BOTTOM).withProperty(BlockStairs.SHAPE, EnumShape.OUTER_LEFT);
		blockstateIn[29] = stair.withProperty(BlockStairs.FACING, EnumFacing.SOUTH).withProperty(BlockStairs.HALF,
				EnumHalf.TOP);
		blockstateIn[34] = stair.withProperty(BlockStairs.FACING, EnumFacing.SOUTH).withProperty(BlockStairs.HALF,
				EnumHalf.BOTTOM);
		blockstateIn[39] = stair.withProperty(BlockStairs.FACING, EnumFacing.WEST).withProperty(BlockStairs.HALF,
				EnumHalf.TOP);
		blockstateIn[40] = stair.withProperty(BlockStairs.FACING, EnumFacing.WEST)
				.withProperty(BlockStairs.HALF, EnumHalf.TOP).withProperty(BlockStairs.SHAPE, EnumShape.INNER_RIGHT);
		blockstateIn[41] = stair.withProperty(BlockStairs.FACING, EnumFacing.WEST)
				.withProperty(BlockStairs.HALF, EnumHalf.TOP).withProperty(BlockStairs.SHAPE, EnumShape.INNER_LEFT);
		blockstateIn[44] = stair.withProperty(BlockStairs.FACING, EnumFacing.WEST).withProperty(BlockStairs.HALF,
				EnumHalf.BOTTOM);
		blockstateIn[45] = stair.withProperty(BlockStairs.FACING, EnumFacing.WEST)
				.withProperty(BlockStairs.HALF, EnumHalf.BOTTOM).withProperty(BlockStairs.SHAPE, EnumShape.INNER_RIGHT);
		blockstateIn[46] = stair.withProperty(BlockStairs.FACING, EnumFacing.WEST)
				.withProperty(BlockStairs.HALF, EnumHalf.BOTTOM).withProperty(BlockStairs.SHAPE, EnumShape.INNER_LEFT);
		blockstateIn[49] = stair.withProperty(BlockStairs.FACING, EnumFacing.EAST).withProperty(BlockStairs.HALF,
				EnumHalf.TOP);
		blockstateIn[50] = stair.withProperty(BlockStairs.FACING, EnumFacing.EAST)
				.withProperty(BlockStairs.HALF, EnumHalf.TOP).withProperty(BlockStairs.SHAPE, EnumShape.INNER_RIGHT);
		blockstateIn[51] = stair.withProperty(BlockStairs.FACING, EnumFacing.EAST)
				.withProperty(BlockStairs.HALF, EnumHalf.TOP).withProperty(BlockStairs.SHAPE, EnumShape.INNER_LEFT);
		blockstateIn[54] = stair.withProperty(BlockStairs.FACING, EnumFacing.EAST).withProperty(BlockStairs.HALF,
				EnumHalf.BOTTOM);
		blockstateIn[55] = stair.withProperty(BlockStairs.FACING, EnumFacing.EAST)
				.withProperty(BlockStairs.HALF, EnumHalf.BOTTOM).withProperty(BlockStairs.SHAPE, EnumShape.INNER_RIGHT);
		blockstateIn[56] = stair.withProperty(BlockStairs.FACING, EnumFacing.EAST)
				.withProperty(BlockStairs.HALF, EnumHalf.BOTTOM).withProperty(BlockStairs.SHAPE, EnumShape.INNER_LEFT);
	}

	
	private void writeConfigToJson(File configFile) throws IOException {
		JsonWriter writer = new JsonWriter(new FileWriter(configFile));
		writer.setIndent(" ");
		writer.beginArray();
		for (int level = 0; level < 1; level++) {
			writer.beginObject();
			{
				writer.name("level");
				writer.value(level);
				writer.name("hint");
				writer.value("0 -air, 1 - walls, 2 - floor, 18 - windows, 120 - fence");
				writer.name("entries");
				writer.beginArray();
				for (int num_place = 0; num_place < 57; num_place++) {
					writer.beginObject();
					{
						writer.name("index_space");
						writer.value(num_place);
						IBlockState bstate = this.blockstateList[level][num_place];
						String blockRegistryName = Block.REGISTRY.getNameForObject(bstate.getBlock()).toString();
						writer.name(blockRegistryName);
						writer.beginObject();
						bstate.getProperties().forEach((p, v) -> {
							try {
								writer.name(p.getName());
								writer.value(getValueName(p, v));
							} catch (IOException e) {
								throw new UncheckedIOException("Input error while converting to Json a BlockState instance of block " + blockRegistryName
										+ " for config of flat cube type world.", e);
							}
						});
						writer.endObject();
					}
					writer.endObject();
				}
				writer.endArray();
			}
			writer.endObject();
		}
		writer.endArray();
		writer.close();
	}
    
    @SuppressWarnings({"rawtypes", "unchecked"})
	private void readConfigFromJson(File configFile) throws IOException {
        JsonReader reader = new JsonReader(new FileReader(configFile));
        reader.beginArray();
		while (reader.hasNext()) {
			reader.beginObject();
			{
				reader.nextName();
				int level = reader.nextInt();
				String name = reader.nextName();
				if(name.equals("hint")) {
					reader.skipValue();
					reader.nextName();
				}
				reader.beginArray();
				while (reader.hasNext()) {
					reader.beginObject();
					reader.nextName();
					int index_space = reader.nextInt();
					String blockRegistryName = reader.nextName();
			        Block block = Block.getBlockFromName(blockRegistryName);
			        IBlockState blockState = block.getBlockState().getBaseState();
					reader.beginObject();
					while (reader.hasNext()) {
			            IProperty property = block.getBlockState().getProperty(reader.nextName());
			            blockState = blockState.withProperty(property, findPropertyValueByName(property, reader.nextString()));
			        }
					this.blockstateList[level][index_space]=blockState;
					reader.endObject();
					reader.endObject();
				}
				reader.endArray();
			}
			reader.endObject();
		}
		reader.endArray();
		reader.close();
    }
    
    @SuppressWarnings({"rawtypes", "unchecked"})
	private String getValueName(IProperty property, Comparable v) {
        return property.getName(v);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
	private Comparable findPropertyValueByName(IProperty property, String valueIn) {
        Optional<Comparable> value = property.parseValue(valueIn);
        if (value.isPresent()) {
            return value.get();
        } else {
            for (Object v : property.getAllowedValues()) {
                if (isValueEqualTo(property, (Comparable) v, valueIn)) {
                    return (Comparable) v;
                }
            }
        }
        return null;
    }

    @SuppressWarnings("rawtypes")
	private boolean isValueEqualTo(IProperty property, Comparable value, String valueIn) {
        return getValueName(property, value).equals(valueIn);
    }
}
