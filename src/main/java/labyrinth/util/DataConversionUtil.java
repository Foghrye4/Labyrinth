package labyrinth.util;

import java.nio.ByteBuffer;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import labyrinth.worldgen.DungeonCube;

public class DataConversionUtil {
	
	public static byte[] dungeonCubeMapToByteArray(Map<Integer, DungeonCube> dungeonCubeMap){
		Set<Entry<Integer, DungeonCube>> entrySet = dungeonCubeMap.entrySet();
		ByteBuffer bf = ByteBuffer.allocate(entrySet.size()*5);
		for(Entry<Integer, DungeonCube> entry:entrySet){
			bf.putInt(entry.getKey());
			bf.put((byte)entry.getValue().ordinal());
		}
		return bf.array();
	}
	
	public static void loadDungeonCubeMapFromByteArray(byte[] array, Map<Integer, DungeonCube> dungeonCubeMap){
		ByteBuffer bf = ByteBuffer.wrap(array);
		while(bf.hasRemaining())
			dungeonCubeMap.put(bf.getInt(), DungeonCube.values()[Byte.toUnsignedInt(bf.get())]);
	}
}
