package schmoller.hitori;

public enum NumberState {
	Normal,
	Shaded,
	Marked,
	LAShaded,
	LAMarked;
	
	public boolean isClear() {
		return this == Normal || this == Marked || this == LAMarked;
	}
}
