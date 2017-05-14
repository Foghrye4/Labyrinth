package labyrinth.gui;

import labyrinth.LabyrinthMod;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.config.GuiConfig;

public class LabyrinthGuiConfig extends GuiConfig {

    public LabyrinthGuiConfig(GuiScreen parent) {
        super(parent, 
        		new ConfigElement(LabyrinthMod.config.configuration.getCategory(Configuration.CATEGORY_GENERAL)).getChildElements(), 
        		LabyrinthMod.MODID, 
        		false,
                false, 
                GuiConfig.getAbridgedConfigPath(LabyrinthMod.config.configuration.toString()));
    }
}
