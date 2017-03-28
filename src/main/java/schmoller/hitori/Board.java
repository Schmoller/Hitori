package schmoller.hitori;

import schmoller.hitori.solver.FloodFill;

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
	
	public BoardState getBoardState() {
		// 1. No more than 1 of each number in each row
		// 2. No adjacent shaded areas
		// 3. Unshaded areas must be continuous
		
		boolean isIncomplete = false;
		// Check rule 1 and 2 for rows
		for (int row = 0; row < height; ++row) {
			long mask = 0;
			boolean lastShaded = false;
			
			for (int col = 0; col < width; ++col) {
				int value = board[col + row * width].getValue();
				boolean isShaded = !board[col + row * width].getState().isClear();
				
				if (!isShaded) {
					// Check for no duplicate values
					if ((mask & (1 << value)) != 0) {
						// Error:
						isIncomplete = true;
					} else {
						mask |= (1 << value);
					}
				} else if (lastShaded) {
					return BoardState.Invalid;
				}
				
				lastShaded = isShaded;
			}
		}
		
		// Check rule 1 and 2 for cols
		for (int col = 0; col < width; ++col) {
			long mask = 0;
			boolean lastShaded = false;
			
			for (int row = 0; row < height; ++row) {
				int value = board[col + row * width].getValue();
				boolean isShaded = !board[col + row * width].getState().isClear();
				
				if (!isShaded) {
					// Check for no duplicate values
					if ((mask & (1 << value)) != 0) {
						// Error:
						isIncomplete = true;
					} else {
						mask |= (1 << value);
					}
				} else if (lastShaded) {
					return BoardState.Invalid;
				}
				
				lastShaded = isShaded;
			}
		}
		
		// Check for rule 3
		FloodFill fill = new FloodFill(this);
		if (!fill.flood()) {
			return BoardState.Invalid;
		}
		
		if (isIncomplete) {
			return BoardState.Incomplete;
		}
		
		return BoardState.Complete;
	}
	
	public static class BoardNumber {
		private final int value;
		private final int index;
		private NumberState state;
		
		public BoardNumber(int value, int index) {
			if (value <= 0 || value > 9) {
				throw new IllegalArgumentException("Valid numbers are 1 - 9");
			}
			
			this.value = value;
			this.index = index;
			this.state = NumberState.Normal;
		}
		
		public int getValue() {
			return value;
		}
		
		public int getIndex() {
			return index;
		}
		
		public NumberState getState() {
			return state;
		}
		
		public void setState(NumberState state) {
			this.state = state;
		}
		
		public String toString() {
			return value + "@" + index;
		}
	}
	
	public static Board from(int width, int height, int... numbers) {
		BoardNumber[] data = new BoardNumber[width * height];
		for (int i = 0; i < numbers.length; ++i) {
			data[i] = new BoardNumber(numbers[i], i);
		}
		
		return new Board(width, height, data);
	}
	
	public enum BoardState {
		Incomplete,
		Complete,
		Invalid
	}
}
