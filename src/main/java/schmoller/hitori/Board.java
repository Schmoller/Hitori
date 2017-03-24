package schmoller.hitori;

public class Board {
	private final BoardNumber[] board;
	private final int width;
	private final int height;
	
	public Board(int width, int height, BoardNumber[] board) {
		if (board.length != (width * height)) {
			throw new IllegalArgumentException("Board size does not match data");
		}
		
		this.board = board;
		this.width = width;
		this.height = height;
	}
	
	public int getCols() {
		return width;
	}
	
	public int getRows() {
		return height;
	}
	
	public BoardNumber get(int row, int col) {
		return board[col + row * width];
	}
	
	public static class BoardNumber {
		private final int value;
		private NumberState state;
		
		public BoardNumber(int value) {
			if (value <= 0 || value > 9) {
				throw new IllegalArgumentException("Valid numbers are 1 - 9");
			}
			
			this.value = value;
			this.state = NumberState.Normal;
		}
		
		public int getValue() {
			return value;
		}
		
		public NumberState getState() {
			return state;
		}
		
		public void setState(NumberState state) {
			this.state = state;
		}
	}
	
	public static Board from(int width, int height, int... numbers) {
		BoardNumber[] data = new BoardNumber[width * height];
		for (int i = 0; i < numbers.length; ++i) {
			data[i] = new BoardNumber(numbers[i]);
		}
		
		return new Board(width, height, data);
	}
}
