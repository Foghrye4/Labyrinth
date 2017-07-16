package labyrinth;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import net.minecraft.util.ResourceLocation;

public class ServerProxy {
	public void load() {

	}

	public File getMinecraftDir() {
		return new File(".");
	}

	public void preInit() {
	}

	public InputStream getResourceInputStream(ResourceLocation resource) throws IOException {
		String resourceURLPath = "/assets/" + resource.getResourceDomain() + "/" + resource.getResourcePath();
		return LabyrinthMod.class.getResourceAsStream(resourceURLPath);
	}
}
