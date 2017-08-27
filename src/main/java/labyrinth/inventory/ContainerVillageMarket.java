package labyrinth.inventory;

import java.util.List;

import net.minecraft.entity.IMerchant;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.InventoryMerchant;
import net.minecraft.inventory.Slot;
import net.minecraft.inventory.SlotMerchantResult;
import net.minecraft.world.World;

public class ContainerVillageMarket extends Container {

	/** The current IMerchant instance in use for this specific merchant. */
	private IMerchant merchant;
	private InventoryMerchant merchantInventory;
	/** Instance of World. */
	private int selectedMerchant;
	private final List<IMerchant> merchants;
	private int merchantInput1Index = 0;
	private int merchantInput2Index = 0;
	private int merchantOutputIndex = 0;
	private final InventoryPlayer playerInventory;

	public ContainerVillageMarket(InventoryPlayer playerInventoryIn, List<IMerchant> merchantsIn, World worldIn) {
		this.merchants = merchantsIn;
		this.selectedMerchant = 0;
		this.merchant = merchants.get(this.selectedMerchant);
		this.playerInventory = playerInventoryIn;

		this.merchantInventory = new InventoryMerchant(playerInventory.player, merchant);
		Slot merchantInput1 = this.addSlotToContainer(new Slot(this.merchantInventory, 0, 23, 134));
		merchantInput1Index = this.inventorySlots.indexOf(merchantInput1);
		Slot merchantInput2 = this.addSlotToContainer(new Slot(this.merchantInventory, 1, 49, 134));
		merchantInput2Index = this.inventorySlots.indexOf(merchantInput2);
		Slot merchantOutput = this.addSlotToContainer(new SlotMerchantResult(playerInventory.player, merchant, this.merchantInventory, 2, 121, 134));
		merchantOutputIndex = this.inventorySlots.indexOf(merchantOutput);

		for (int i = 0; i < 5; ++i) {
			for (int j = 0; j < 3; ++j) {
				this.addSlotToContainer(new Slot(playerInventory, j + i * 3 + 9, 160 + j * 18, 9 + i * 18));
			}
		}

		for (int i = 0; i < 4; ++i) {
			for (int j = 0; j < 3; ++j) {
				this.addSlotToContainer(new Slot(playerInventory, j + i * 3 + 9 + 5 * 3, 160 + j * 18, 63 + i * 18));
			}
		}

		for (int k = 0; k < 5; ++k) {
			this.addSlotToContainer(new Slot(playerInventory, k, 160 + k * 18, 121));
		}

		for (int k = 0; k < 4; ++k) {
			this.addSlotToContainer(new Slot(playerInventory, k + 5, 160 + k * 18, 139));
		}
	}

	@Override
	public boolean canInteractWith(EntityPlayer playerIn) {
		return true;
	}

	public void setCurrentMerchant(int selectedMerchantIn) {
		this.selectedMerchant = selectedMerchantIn;
		this.merchant = merchants.get(this.selectedMerchant);
		this.merchantInventory = new InventoryMerchant(playerInventory.player, merchant);
		Slot merchantInput1 = this.addSlotToContainer(new Slot(this.merchantInventory, 0, 23, 134));
		this.inventorySlots.set(merchantInput1Index, merchantInput1);
		Slot merchantInput2 = this.addSlotToContainer(new Slot(this.merchantInventory, 1, 49, 134));
		this.inventorySlots.set(merchantInput2Index, merchantInput2);
		Slot merchantOutput = this.addSlotToContainer(new SlotMerchantResult(playerInventory.player, merchant, this.merchantInventory, 2, 121, 134));
		this.inventorySlots.set(merchantOutputIndex, merchantOutput);

	}

}
