package labyrinth.worldgen;

import java.util.Comparator;

public class DungeonLevelPriorityComparator implements Comparator<DungeonLayer> {

	@Override
	public int compare(DungeonLayer o1, DungeonLayer o2) {
		return o2.priority - o1.priority;
	}
}
