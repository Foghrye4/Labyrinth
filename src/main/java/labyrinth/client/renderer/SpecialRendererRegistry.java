package labyrinth.client.renderer;

import java.util.HashSet;
import java.util.Set;

import labyrinth.client.Icon;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class SpecialRendererRegistry {
	public static SpecialRendererRegistry instance = new SpecialRendererRegistry();
	private final Set<Icon> icons = new HashSet<Icon>();
	
	public static Icon registerIcon(ResourceLocation location) {
		Icon icon = new Icon(location);
		instance.icons.add(icon);
		return icon;
	}
	
	@SubscribeEvent
	public void onTextureStitchEvent(TextureStitchEvent.Pre event) {
		for (Icon icon : icons) {
			icon.registerIcon(event.getMap());
		}
	}
}
