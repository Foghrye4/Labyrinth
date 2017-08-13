package labyrinth.gui;

import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiVillageMarket extends GuiInventory{

	public GuiVillageMarket(EntityPlayer player) {
		super(player);
	}

}
