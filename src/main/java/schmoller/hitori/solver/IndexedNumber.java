package schmoller.hitori.solver;

import schmoller.hitori.Board.BoardNumber;

class IndexedNumber {
	private final BoardNumber number;
	private final int index;
	
	public IndexedNumber(BoardNumber number, int index) {
		this.number = number;
		this.index = index;
	}
	
	public BoardNumber getNumber() {
		return number;
	}
	
	public int getIndex() {
		return index;
	}

	@Override
	public String toString() {
		return "[" + number + "@" + index + "]";
	}
}