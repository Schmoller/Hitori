package schmoller.hitori.solver;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class TabledSet implements Set<IndexedNumber> {
	private final Set<IndexedNumber> backingSet;
	private final IndexedNumber[] data;
	
	private final int rows;
	private final int cols;
	
	public TabledSet(int rows, int cols) {
		this.rows = rows;
		this.cols = cols;
		
		data = new IndexedNumber[rows * cols];
		backingSet = new HashSet<>();
	}
	
	public int getRows() {
		return rows;
	}
	
	public int getCols() {
		return cols;
	}
	
	@Override
	public int size() {
		return backingSet.size();
	}

	@Override
	public boolean isEmpty() {
		return backingSet.isEmpty();
	}
	
	public IndexedNumber get(int row, int col) {
		return data[col + row * cols];
	}
	
	@Override
	public boolean contains(Object o) {
		return backingSet.contains(o);
	}

	@Override
	public Iterator<IndexedNumber> iterator() {
		return backingSet.iterator();
	}

	@Override
	public Object[] toArray() {
		return backingSet.toArray();
	}

	@Override
	public <T> T[] toArray(T[] a) {
		return backingSet.toArray(a);
	}

	@Override
	public boolean add(IndexedNumber e) {
		if (backingSet.add(e)) {
			data[e.getIndex()] = e;
			return true;
		} else {
			return false;
		}
	}

	@Override
	public boolean remove(Object o) {
		if (backingSet.remove(o)) {
			data[((IndexedNumber)o).getIndex()] = null;
			return true;
		} else {
			return false;
		}
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		return backingSet.containsAll(c);
	}

	@Override
	public boolean addAll(Collection<? extends IndexedNumber> c) {
		boolean success = false;
		for (IndexedNumber n : c) {
			if (add(n)) {
				success = true;
			}
		}
		
		return success;
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		boolean changed = false;
		for (Object o : c) {
			if (remove(o)) {
				changed = true;
			}
		}
		
		return changed;
	}

	@Override
	public void clear() {
		Arrays.fill(data, null);
		backingSet.clear();
	}
}
