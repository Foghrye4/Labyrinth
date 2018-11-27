package labyrinth.worldgen;

import static labyrinth.LabyrinthMod.MODID;
import static labyrinth.LabyrinthMod.getResourceInputStream;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.stream.JsonReader;

import io.github.opencubicchunks.cubicchunks.api.util.CubePos;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import labyrinth.LabyrinthMod;
import net.minecraft.nbt.NBTException;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class LevelsStorage {
	
	public final static Object2IntMap<String> ALIASES = new Object2IntOpenHashMap<String>();
	public final List<DungeonLayer> levels = new ArrayList<DungeonLayer>();
	public final static DefaultMapping defaultMapping = new DefaultMapping();

	
	static {
		ALIASES.put("air", 0);
		ALIASES.put("walls", 1);
		ALIASES.put("floor", 2);
		ALIASES.put("chest_north", 3);
		ALIASES.put("chest_south", 4);
		ALIASES.put("chest_west", 5);
		ALIASES.put("chest_east", 6);
		ALIASES.put("furnace_north", 7);
		ALIASES.put("furnace_south", 8);
		ALIASES.put("furnace_west", 9);
		ALIASES.put("furnace_east", 10);
		ALIASES.put("crafting_table", 11);
		ALIASES.put("cauldron", 12);
		ALIASES.put("brewing_stand", 13);
		ALIASES.put("coal_block", 14);
		ALIASES.put("warehouse_shelf", 15);
		ALIASES.put("warehouse_lava_source", 16);
		ALIASES.put("warehouse_water_source", 17);
		ALIASES.put("windows", 18);
		ALIASES.put("stair_top_north", 19);
		ALIASES.put("unused_20", 20);
		ALIASES.put("unused_21", 21);
		ALIASES.put("unused_22", 22);
		ALIASES.put("unused_23", 23);
		ALIASES.put("stair_bottom_north", 24);
		ALIASES.put("stair_bottom_north_inner_right", 25);
		ALIASES.put("stair_bottom_north_inner_left", 26);
		ALIASES.put("stair_bottom_north_outer_right", 27);
		ALIASES.put("stair_bottom_north_outer_left", 28);
		ALIASES.put("stair_top_south", 29);
		ALIASES.put("unused_30", 30);
		ALIASES.put("unused_31", 31);
		ALIASES.put("unused_32", 32);
		ALIASES.put("unused_33", 33);
		ALIASES.put("stair_bottom_south", 34);
		ALIASES.put("unused_35", 35);
		ALIASES.put("unused_36", 36);
		ALIASES.put("unused_37", 37);
		ALIASES.put("unused_38", 38);
		ALIASES.put("stair_top_west", 39);
		ALIASES.put("stair_top_west_inner_right", 40);
		ALIASES.put("stair_top_west_inner_left", 41);
		ALIASES.put("unused_42", 42);
		ALIASES.put("unused_43", 43);
		ALIASES.put("stair_bottom_west", 44);
		ALIASES.put("stair_bottom_west_inner_right", 45);
		ALIASES.put("stair_bottom_west_inner_left", 46);
		ALIASES.put("unused_47", 47);
		ALIASES.put("unused_48", 48);
		ALIASES.put("stair_top_east", 49);
		ALIASES.put("stair_top_east_inner_right", 50);
		ALIASES.put("stair_top_east_inner_left", 51);
		ALIASES.put("unused_52", 52);
		ALIASES.put("unused_53", 53);
		ALIASES.put("stair_bottom_east", 54);
		ALIASES.put("stair_bottom_east_inner_right", 55);
		ALIASES.put("stair_bottom_east_inner_left", 56);
		ALIASES.put("slab_bottom", 57);
		ALIASES.put("door_west_upper_left", 58);
		ALIASES.put("door_west_upper_right", 59);
		ALIASES.put("door_west_lower_left", 60);
		ALIASES.put("door_west_lower_right", 61);
		ALIASES.put("door_east_upper_left", 62);
		ALIASES.put("door_east_upper_right", 63);
		ALIASES.put("door_east_lower_left", 64);
		ALIASES.put("door_east_lower_right", 65);
		ALIASES.put("dirt", 66);
		ALIASES.put("melon", 67);
		ALIASES.put("pumpkin", 68);
		ALIASES.put("melon_stem", 69);
		ALIASES.put("pumpkin_stem", 70);
		ALIASES.put("sand", 71);
		ALIASES.put("reeds", 72);
		ALIASES.put("potato", 73);
		ALIASES.put("carrots", 74);
		ALIASES.put("leaves", 75);
		ALIASES.put("carpet", 76);
		ALIASES.put("flower_pot", 77);
		ALIASES.put("door_east_upper_left", 78);
		ALIASES.put("door_east_upper_right", 79);
		ALIASES.put("door_east_lower_left", 80);
		ALIASES.put("door_east_lower_right", 81);
		ALIASES.put("door_west_upper_left", 82);
		ALIASES.put("door_west_upper_right", 83);
		ALIASES.put("door_west_lower_left", 84);
		ALIASES.put("door_west_lower_right", 85);
		ALIASES.put("bed_foot", 86);
		ALIASES.put("bed_head", 87);
		ALIASES.put("leaves", 88);
		ALIASES.put("sticky_piston_east", 89);
		ALIASES.put("sticky_piston_head_east", 90);
		ALIASES.put("lever_west", 91);
		ALIASES.put("sticky_piston_west", 92);
		ALIASES.put("sticky_piston_head_west", 93);
		ALIASES.put("sticky_piston_south", 94);
		ALIASES.put("repeater_west_1", 95);
		ALIASES.put("repeater_west_2", 96);
		ALIASES.put("repeater_west_3", 97);
		ALIASES.put("repeater_west_4", 98);
		ALIASES.put("repeater_north_1", 99);
		ALIASES.put("repeater_north_2", 100);
		ALIASES.put("repeater_north_3", 101);
		ALIASES.put("repeater_north_4", 102);
		ALIASES.put("unlit_redstone_torch", 103);
		ALIASES.put("wire_15", 104);
		ALIASES.put("wire_14", 105);
		ALIASES.put("wire_13", 106);
		ALIASES.put("wire_12", 107);
		ALIASES.put("wire_11", 108);
		ALIASES.put("wire_10", 109);
		ALIASES.put("wire_9", 110);
		ALIASES.put("wire_8", 111);
		ALIASES.put("wire_7", 112);
		ALIASES.put("wire_6", 113);
		ALIASES.put("wire_5", 114);
		ALIASES.put("wire_0", 115);
		ALIASES.put("anvil_south", 116);
		ALIASES.put("anvil_north", 117);
		ALIASES.put("anvil_west", 118);
		ALIASES.put("anvil_east", 119);
		ALIASES.put("village_fence", 120);
		ALIASES.put("unused_121", 121);
		ALIASES.put("unused_122", 122);
		ALIASES.put("unused_123", 123);
		ALIASES.put("unused_124", 124);
		ALIASES.put("unused_125", 125);
		ALIASES.put("unused_126", 126);
		ALIASES.put("unused_127", 127);
		ALIASES.put("unused_128", 128);
		ALIASES.put("sticky_piston_north", 129);
		ALIASES.put("sticky_piston_head_north", 130);
		ALIASES.put("redstone_torch", 131);
		ALIASES.put("unused_132", 132);
		ALIASES.put("unused_133", 133);
		ALIASES.put("unused_134", 134);
		ALIASES.put("unused_135", 135);
		ALIASES.put("unused_136", 136);
		ALIASES.put("lever_east", 137);
		ALIASES.put("repeater_1_east", 138);
		ALIASES.put("repeater_2_east", 139);
		ALIASES.put("repeater_3_east", 140);
		ALIASES.put("repeater_4_east", 141);
		ALIASES.put("repeater_1_west", 142);
		ALIASES.put("repeater_2_west", 143);
		ALIASES.put("repeater_3_west", 144);
		ALIASES.put("repeater_4_west", 145);
		ALIASES.put("unused_146", 146);
		ALIASES.put("unused_147", 147);
		ALIASES.put("unused_148", 148);
		ALIASES.put("unused_149", 149);
		ALIASES.put("unused_150", 150);
		ALIASES.put("unused_151", 151);
		ALIASES.put("unused_152", 152);
		ALIASES.put("torch_up", 153);
		ALIASES.put("torch_north", 154);
		ALIASES.put("torch_south", 155);
		ALIASES.put("torch_west", 156);
		ALIASES.put("torch_east", 157);
/*		ALIASES.put("", 158);
		ALIASES.put("", 159);
		ALIASES.put("", 160);
		ALIASES.put("", 161);
		ALIASES.put("", 162);
		ALIASES.put("", 163);
		ALIASES.put("", 164);
		ALIASES.put("", 165);
		ALIASES.put("", 166);
		ALIASES.put("", 167);
		ALIASES.put("", 168);
		ALIASES.put("", 169);
		ALIASES.put("", 170);
		ALIASES.put("", 171);
		ALIASES.put("", 172);
		ALIASES.put("", 173);
		ALIASES.put("", 174);
		ALIASES.put("", 175);
		ALIASES.put("", 176);
		ALIASES.put("", 177);
		ALIASES.put("", 178);
		ALIASES.put("", 179);
		ALIASES.put("", 180);
		ALIASES.put("", 181);
		ALIASES.put("", 182);
		ALIASES.put("", 183);
		ALIASES.put("", 184);
		ALIASES.put("", 185);
		ALIASES.put("", 186);
		ALIASES.put("", 187);
		ALIASES.put("", 188);
		ALIASES.put("", 189);
		ALIASES.put("", 190);
		ALIASES.put("", 191);
		ALIASES.put("", 192);
		ALIASES.put("", 193);
		ALIASES.put("", 194);
		ALIASES.put("", 195);
		ALIASES.put("", 196);
		ALIASES.put("", 197);
		ALIASES.put("", 198);
		ALIASES.put("", 199);
		ALIASES.put("", 200);
		ALIASES.put("", 201);
		ALIASES.put("", 202);
		ALIASES.put("", 203);
		ALIASES.put("", 204);
		ALIASES.put("", 205);
		ALIASES.put("", 206);
		ALIASES.put("", 207);
		ALIASES.put("", 208);
		ALIASES.put("", 209);
		ALIASES.put("", 210);
		ALIASES.put("", 211);
		ALIASES.put("", 212);
		ALIASES.put("", 213);
		ALIASES.put("", 214);
		ALIASES.put("", 215);
		ALIASES.put("", 216);
		ALIASES.put("", 217);
		ALIASES.put("", 218);
		ALIASES.put("", 219);
		ALIASES.put("", 220);
		ALIASES.put("", 221);
		ALIASES.put("", 222);
		ALIASES.put("", 223);
		ALIASES.put("", 224);
		ALIASES.put("", 225);
		ALIASES.put("", 226);
		ALIASES.put("", 227);
		ALIASES.put("", 228);
		ALIASES.put("", 229);
		ALIASES.put("", 230);
		ALIASES.put("", 231);
		ALIASES.put("", 232);
		ALIASES.put("", 233);
		ALIASES.put("", 234);
		ALIASES.put("", 235);
		ALIASES.put("", 236);
		ALIASES.put("", 237);
		ALIASES.put("", 238);
		ALIASES.put("", 239);
		ALIASES.put("", 240);
		ALIASES.put("", 241);
		ALIASES.put("", 242);
		ALIASES.put("", 243);
		ALIASES.put("", 244);
		ALIASES.put("", 245);
		ALIASES.put("", 246);
		ALIASES.put("", 247);
		ALIASES.put("", 248);
		ALIASES.put("", 249);
		ALIASES.put("", 250);
		ALIASES.put("", 251);
		ALIASES.put("", 252);
		ALIASES.put("", 253);
		ALIASES.put("", 254);
		ALIASES.put("", 255);*/

	}
	
	@SubscribeEvent
	public void load(WorldEvent.Load event) {
		if(!(event.getWorld() instanceof WorldServer)) {
			return;
		}
		if (event.getWorld().provider.getDimension() != 0)
			return;
		File resourceFile = new File(event.getWorld().getSaveHandler().getWorldDirectory(), "/data/" + MODID + "/" + "labyrinth_level_preset.json");
		if (!resourceFile.exists()) {
			LabyrinthMod.log.error("No such file: " + resourceFile.getAbsolutePath());
			return;
		}
		try {
			this.readConfigFromJson(new FileInputStream(resourceFile));
		} catch (IOException | NBTException e) {
			e.printStackTrace();
		}
	}
	
	private void readConfigFromJson(InputStream resourceStream) throws IOException, NBTException {
        JsonReader reader = new JsonReader(new InputStreamReader(resourceStream));
        reader.setLenient(true);
        reader.beginArray();
		while (reader.hasNext()) {
			reader.beginObject();
			{
				levels.add(new DungeonLayer().readFromJson(reader));
			}
			reader.endObject();
		}
		reader.endArray();
		reader.close();
		levels.sort(new DungeonLevelPriorityComparator());
		LabyrinthMod.log.info("Loaded "+levels.size()+" levels");
    }
}
