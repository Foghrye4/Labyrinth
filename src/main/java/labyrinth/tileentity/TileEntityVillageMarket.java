package labyrinth.tileentity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.stats.StatList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.village.MerchantRecipe;
import net.minecraft.village.MerchantRecipeList;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class TileEntityVillageMarket extends TileEntity {

	@Nonnull
	public ItemStack displayedItem = ItemStack.EMPTY;
	@Nonnull
	public ItemStack priceItem1 = ItemStack.EMPTY;
	@Nonnull
	public ItemStack priceItem2 = ItemStack.EMPTY;

	public static final Set<EntityVillager> occupiedVillagers = new HashSet<EntityVillager>();
	public static final Set<MerchantRecipe> occupiedRecipes = new HashSet<MerchantRecipe>();
	public static final List<TileEntityVillageMarket> eventListeners = new ArrayList<TileEntityVillageMarket>();

	private EntityVillager merchant = null;
	private MerchantRecipe recipe = null;
	
	@SideOnly(Side.CLIENT)
	private int iconId = 0;
	@SideOnly(Side.CLIENT)
	public boolean needRenderUpdate = true;

	public ItemStack getDisplayedItem() {
		return displayedItem;
	}

	public void validate() {
		if (!world.isRemote) {
			eventListeners.add(this);
			this.updateMarket();
		}
		super.validate();
	}

	public void invalidate() {
		if (!world.isRemote) {
			eventListeners.remove(this);
			occupiedVillagers.remove(merchant);
			occupiedRecipes.remove(recipe);
		}
		super.invalidate();
	}

	public void updateMarket() {
		boolean sendUpdate = false; 
		if ((merchant != null && merchant.isDead) ||
				(recipe != null && recipe.isRecipeDisabled())) {
			occupiedVillagers.remove(merchant);
			occupiedRecipes.remove(recipe);
			merchant = null;
			recipe = null;
			this.displayedItem = ItemStack.EMPTY;
			this.priceItem1 = ItemStack.EMPTY;
			this.priceItem2 = ItemStack.EMPTY;
			sendUpdate = true;
		}

		if (merchant != null)
			return;
		EntityPlayer player = this.world.getClosestPlayer(pos.getX(), pos.getY(), pos.getZ(), 256, false);

		AxisAlignedBB aabb = new AxisAlignedBB(this.pos.add(-64, -8, -64), this.pos.add(64, 8, 64));
		List<EntityVillager> villagers = this.world.getEntitiesWithinAABB(EntityVillager.class, aabb);
		for (EntityVillager villager : villagers) {
			if (occupiedVillagers.contains(villager))
				continue;
			MerchantRecipeList recipes = villager.getRecipes(player);
			int recipeLastIndex = recipes.size() - 1;
			for (int index = 0; index <= recipeLastIndex; index++) {
				MerchantRecipe recipeIn = recipes.get(index);
				if (occupiedRecipes.contains(recipeIn) || recipeIn.isRecipeDisabled())
					continue;
				merchant = villager;
				recipe = recipeIn;
				this.displayedItem = recipe.getItemToSell().copy();
				this.priceItem1 = recipe.getItemToBuy();
				this.priceItem2 = recipe.getSecondItemToBuy();
				occupiedRecipes.add(recipeIn);
				this.sendUpdatePacket();
				return;
			}
			occupiedVillagers.add(villager);
		}
		if(sendUpdate)
			this.sendUpdatePacket();
	}

	public void sendUpdatePacket() {
		for (Object player : world.playerEntities) {
			if (player instanceof EntityPlayerMP) {
				EntityPlayerMP playerMP = (EntityPlayerMP) player;
				playerMP.connection.sendPacket(this.getUpdatePacket());
			}
		}
	}

	public void tryTrade(EntityPlayer player) {
		if (this.trade(player, false)) {
			for (TileEntityVillageMarket market : eventListeners)
				market.updateMarket();
		}
	}

	private boolean areItemStacksAcceptable(ItemStack stack1, ItemStack recipeStack) {
		return ItemStack.areItemsEqual(stack1, recipeStack) && (!recipeStack.hasTagCompound() || stack1.hasTagCompound() && NBTUtil.areNBTEquals(recipeStack.getTagCompound(), stack1.getTagCompound(), false)) && stack1.getCount() >= recipeStack.getCount();
	}

	public boolean trade(EntityPlayer player, boolean checkOnly) {
		ItemStack stack = recipe.getItemToSell().copy();
		stack.onCrafting(player.world, player, stack.getCount());

		ItemStack itemstack = recipe.getItemToBuy();
		ItemStack itemstack1 = recipe.getSecondItemToBuy();

		boolean correctStack = itemstack.isEmpty();
		boolean correctStack1 = itemstack1.isEmpty();

		for (EnumHand hand : EnumHand.values()) {
			ItemStack itemstackIn = player.getHeldItem(hand);
			if (correctStack && correctStack1)
				break;
			if (!correctStack && this.areItemStacksAcceptable(itemstackIn, itemstack)) {
				itemstack = itemstackIn;
				correctStack = true;
			}
			if (!correctStack1 && this.areItemStacksAcceptable(itemstackIn, itemstack1)) {
				itemstack1 = itemstackIn;
				correctStack1 = true;
			}
		}

		for (ItemStack itemstackIn : player.inventory.mainInventory) {
			if (correctStack && correctStack1)
				break;
			if (!correctStack && this.areItemStacksAcceptable(itemstackIn, itemstack)) {
				itemstack = itemstackIn;
				correctStack = true;
			}
			if (!correctStack1 && this.areItemStacksAcceptable(itemstackIn, itemstack1)) {
				itemstack1 = itemstackIn;
				correctStack1 = true;
			}
		}
		if (checkOnly)
			return correctStack && correctStack1;

		if (!correctStack || !correctStack1)
			return false;

		if (this.doTrade(recipe, itemstack, itemstack1) || this.doTrade(recipe, itemstack1, itemstack)) {
			merchant.useRecipe(recipe);
			player.addStat(StatList.TRADED_WITH_VILLAGER);
			player.inventory.addItemStackToInventory(stack);
			return true;
		}

		return false;
	}

	private boolean doTrade(MerchantRecipe trade, ItemStack firstItem, ItemStack secondItem) {
		ItemStack itemstack = trade.getItemToBuy();
		ItemStack itemstack1 = trade.getSecondItemToBuy();

		if (firstItem.getItem() == itemstack.getItem() && firstItem.getCount() >= itemstack.getCount()) {
			if (!itemstack1.isEmpty() && !secondItem.isEmpty() && itemstack1.getItem() == secondItem.getItem() && secondItem.getCount() >= itemstack1.getCount()) {
				firstItem.shrink(itemstack.getCount());
				secondItem.shrink(itemstack1.getCount());
				return true;
			}

			if (itemstack1.isEmpty() && secondItem.isEmpty()) {
				firstItem.shrink(itemstack.getCount());
				return true;
			}
		}

		return false;
	}

	@Nullable
	@Override
	public SPacketUpdateTileEntity getUpdatePacket() {
		return new SPacketUpdateTileEntity(this.pos, 255, this.getUpdateTag());
	}

	public void readFromNBT(NBTTagCompound compound) {
		super.readFromNBT(compound);
		this.readItems(compound);
	}

	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		NBTTagCompound nbt = super.writeToNBT(compound);
		this.writeDisplayItemToNBT(nbt);
		return nbt;
	}

	@Override
	public NBTTagCompound getUpdateTag() {
		NBTTagCompound nbt = super.getUpdateTag();
		this.writeDisplayItemToNBT(nbt);
		return nbt;
	}
	
	private void readItems(NBTTagCompound compound) {
		displayedItem = new ItemStack(compound.getCompoundTag("displayedItem"));
		priceItem1 = new ItemStack(compound.getCompoundTag("priceItem1"));
		priceItem2 = new ItemStack(compound.getCompoundTag("priceItem2"));
		needRenderUpdate = true;
	}

	private void writeDisplayItemToNBT(NBTTagCompound nbt) {
		NBTTagCompound stackNBT1 = new NBTTagCompound();
		displayedItem.writeToNBT(stackNBT1);
		NBTTagCompound stackNBT2 = new NBTTagCompound();
		priceItem1.writeToNBT(stackNBT2);
		NBTTagCompound stackNBT3 = new NBTTagCompound();
		priceItem2.writeToNBT(stackNBT3);
		nbt.setTag("displayedItem", stackNBT1);
		nbt.setTag("priceItem1", stackNBT2);
		nbt.setTag("priceItem2", stackNBT3);
	}

	@Override
	public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
		this.readItems(pkt.getNbtCompound());
	}

	@SideOnly(Side.CLIENT)
	public void setIconId(int iconIdIn) {
		iconId = iconIdIn;
	}
	
	@SideOnly(Side.CLIENT)
	public int getIconId() {
		return iconId;
	}

}
