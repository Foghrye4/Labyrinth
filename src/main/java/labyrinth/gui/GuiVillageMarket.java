package labyrinth.gui;

import java.io.IOException;
import java.util.List;

import javax.annotation.Nonnull;

import labyrinth.ClientNetworkHandler;
import labyrinth.LabyrinthMod;
import labyrinth.inventory.ContainerVillageMarket;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.IMerchant;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.village.MerchantRecipe;
import net.minecraft.village.MerchantRecipeList;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiVillageMarket extends GuiContainer {

	/** The GUI texture for the villager merchant GUI. */
	private static final ResourceLocation GUI_TEXTURE = new ResourceLocation(LabyrinthMod.MODID, "textures/gui/village_market.png");
	/** The current IMerchant instance in use for this specific merchant. */
	private IMerchant merchant;
	/** The button which proceeds to the next available merchant. */
	private GuiButton nextButton;
	/** Returns to the previous merchant. */
	private GuiButton previousButton;
	/**
	 * The integer value corresponding to the currently selected merchant
	 * recipe.
	 */
	private int selectedMerchant;
	private final List<IMerchant> merchants;
	/** Merchant display name. */
	private ITextComponent chatComponent;

	@SuppressWarnings({"rawtypes", "unchecked"})
	public GuiVillageMarket(@Nonnull InventoryPlayer player, @Nonnull List villagers, World worldIn) {
		super(new ContainerVillageMarket(player, villagers, worldIn));
		this.merchants = villagers;
		this.selectedMerchant = 0;
		this.merchant = merchants.get(this.selectedMerchant);
		this.chatComponent = merchant.getDisplayName();
		this.xSize = 256;
		this.ySize = 163;
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		String s = this.chatComponent.getUnformattedText();
		this.fontRenderer.drawString(s, this.xSize / 2 - this.fontRenderer.getStringWidth(s) / 2, 6, 4210752);
	}

	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		super.drawScreen(mouseX, mouseY, partialTicks);
		MerchantRecipeList merchantrecipelist = this.merchant.getRecipes(this.mc.player);
		int rowHeight = 18;
		if (merchantrecipelist != null && !merchantrecipelist.isEmpty()) {
			int i = (this.width - this.xSize) / 2;
			int j = (this.height - this.ySize) / 2;
			int verticalMargin = 18;
			int input1PosX = 23;
			int input2PosX = 49;
			int outputPosX = 121;
			GlStateManager.pushMatrix();
			RenderHelper.enableGUIStandardItemLighting();
			GlStateManager.disableLighting();
			GlStateManager.enableRescaleNormal();
			GlStateManager.enableColorMaterial();
			GlStateManager.enableLighting();
			for (int k = 0; k < merchantrecipelist.size(); k++) {

				MerchantRecipe merchantrecipe = (MerchantRecipe) merchantrecipelist.get(k);
				
				if (merchantrecipe.isRecipeDisabled()) {
					this.mc.getTextureManager().bindTexture(GUI_TEXTURE);
					this.drawTexturedModalRect(i + input1PosX, j + rowHeight * k + verticalMargin, 212, 163, 28, 21);
				}
				
				ItemStack itemstack = merchantrecipe.getItemToBuy();
				ItemStack itemstack1 = merchantrecipe.getSecondItemToBuy();
				ItemStack itemstack2 = merchantrecipe.getItemToSell();
				this.itemRender.zLevel = 100.0F;
				this.itemRender.renderItemAndEffectIntoGUI(itemstack, i + input1PosX, j + rowHeight * k + verticalMargin);
				this.itemRender.renderItemOverlays(this.fontRenderer, itemstack, i + input1PosX, j + rowHeight * k + verticalMargin);

				if (!itemstack1.isEmpty()) {
					this.itemRender.renderItemAndEffectIntoGUI(itemstack1, i + input2PosX, j + rowHeight * k + verticalMargin);
					this.itemRender.renderItemOverlays(this.fontRenderer, itemstack1, i + input2PosX, j + rowHeight * k + verticalMargin);
				}

				this.itemRender.renderItemAndEffectIntoGUI(itemstack2, i + outputPosX, j + rowHeight * k + verticalMargin);
				this.itemRender.renderItemOverlays(this.fontRenderer, itemstack2, i + outputPosX, j + rowHeight * k + verticalMargin);
				this.itemRender.zLevel = 0.0F;
				GlStateManager.disableLighting();

				if (this.isPointInRegion(input1PosX, rowHeight * k + verticalMargin, 16, 16, mouseX, mouseY) && !itemstack.isEmpty()) {
					this.renderToolTip(itemstack, mouseX, mouseY);
				} else if (!itemstack1.isEmpty() && this.isPointInRegion(input2PosX, rowHeight * k + verticalMargin, 16, 16, mouseX, mouseY) && !itemstack1.isEmpty()) {
					this.renderToolTip(itemstack1, mouseX, mouseY);
				} else if (!itemstack2.isEmpty() && this.isPointInRegion(outputPosX, rowHeight * k + verticalMargin, 16, 16, mouseX, mouseY) && !itemstack2.isEmpty()) {
					this.renderToolTip(itemstack2, mouseX, mouseY);
				} else if (merchantrecipe.isRecipeDisabled() && (this.isPointInRegion(83, 21, 28, 21, mouseX, mouseY) || this.isPointInRegion(83, 51, 28, 21, mouseX, mouseY))) {
					this.drawHoveringText(I18n.format("merchant.deprecated", new Object[0]), mouseX, mouseY);
				}
			}

			GlStateManager.popMatrix();
			GlStateManager.enableLighting();
			GlStateManager.enableDepth();
			RenderHelper.enableStandardItemLighting();
		}
	}

	public void updateScreen() {
		super.updateScreen();
		this.nextButton.enabled = this.selectedMerchant < merchants.size() - 1;
		this.previousButton.enabled = this.selectedMerchant > 0;
	}

	public void initGui() {
		super.initGui();
		int i = (this.width - this.xSize) / 2;
		int j = (this.height - this.ySize) / 2;
		this.nextButton = this.addButton(new GuiButton(1, i + 120 + 27, j + 24 - 1, 10, 20, ">"));
		this.previousButton = this.addButton(new GuiButton(2, i + 36 - 19, j + 24 - 1, 10, 20, "<"));
		this.nextButton.enabled = false;
		this.previousButton.enabled = false;
	}

	@Override
	protected void actionPerformed(GuiButton button) throws IOException {
		boolean updateContainer = false;

		if (button == this.nextButton) {
			++this.selectedMerchant;
			if (this.selectedMerchant >= merchants.size()) {
				this.selectedMerchant = merchants.size() - 1;
			}

			updateContainer = true;
		} else if (button == this.previousButton) {
			--this.selectedMerchant;

			if (this.selectedMerchant < 0) {
				this.selectedMerchant = 0;
			}

			updateContainer = true;
		}

		if (updateContainer) {

			this.merchant = merchants.get(this.selectedMerchant);
			this.chatComponent = merchant.getDisplayName();

			((ContainerVillageMarket) this.inventorySlots).setCurrentMerchant(this.selectedMerchant);
			((ClientNetworkHandler) LabyrinthMod.proxy.getNetwork()).sendVilageMarketSelectedMerchant(this.selectedMerchant);
		}
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        this.mc.getTextureManager().bindTexture(GUI_TEXTURE);
        int i = (this.width - this.xSize) / 2;
        int j = (this.height - this.ySize) / 2;
        this.drawTexturedModalRect(i, j, 0, 0, this.xSize, this.ySize);
	}

}
