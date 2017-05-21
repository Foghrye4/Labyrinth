package labyrinth.util;

import java.util.HashMap;
import java.util.Iterator;

public class LimitedSizeHashMap<K, V> extends HashMap<K, V> {

	private static final long serialVersionUID = 1L;
	private final int maxSize;
	private Iterator<Entry<K, V>> entryIterator;

	public LimitedSizeHashMap(int maxSizeIn) {
		super(maxSizeIn);
		maxSize = maxSizeIn;
	}

	@Override
	public V put(K key, V value) {
		V returnValue = null;
		if (this.isEmpty()) {
			returnValue = super.put(key, value);
			entryIterator = this.entrySet().iterator();
		} else if (this.size() == maxSize) {
			returnValue = super.put(key, value);
//			entryIterator.remove();
//			entryIterator.next();
		}
		return returnValue;
	}

}
