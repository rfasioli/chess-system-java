package boardgame;

public abstract class Piece {
	protected Position position;
	private Board board;
	
	public Piece(Board board) {
		super();
		this.board = board;
	}

	protected Board getBoard() {
		return board;
	}
	
	public abstract boolean[][] possibleMoves();
	
	public boolean possibleMove(Position position) {
		return this.possibleMoves()[position.getRow()][position.getColumn()];
	}
	
	public boolean isThereAnyPossibleMove() {
		boolean[][] possibleMoves = this.possibleMoves();
		for (boolean[] row : possibleMoves) {
			for(boolean possible: row) {
				if (possible) {
					return true;
				}
			}
		}
		return false;
	}

}
