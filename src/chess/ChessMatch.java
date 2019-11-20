package chess;

import boardgame.Board;
import boardgame.Position;
import chess.pieces.King;
import chess.pieces.Rook;

public class ChessMatch {

	private Board board;

	public ChessMatch() {
		this.board = new Board(8, 8);
		this.initialSetup();
	}
	
	public ChessPiece[][] getPieces() {
		ChessPiece[][] mat = new ChessPiece[this.board.getRows()][this.board.getColumns()];
		for (int i = 0; i < this.board.getRows(); i++) {
			for (int j = 0; j < this.board.getColumns(); j++) {
				mat[i][j] = (ChessPiece) this.board.piece(i, j);
			}
		}
		return mat;
	}
	
	private void placeNewPiece(char column, int row, ChessPiece piece) {
		this.board.placePiece(piece, new ChessPosition(column, row).toPosition());		
	}

	private void initialSetup() {
		this.placeNewPiece('a', 1, new Rook(this.board, Color.WHITE));
		this.placeNewPiece('h', 1, new Rook(this.board, Color.WHITE));
		this.placeNewPiece('e', 1, new King(this.board, Color.WHITE));
		
		this.placeNewPiece('a', 8, new Rook(this.board, Color.BLACK));
		this.placeNewPiece('h', 8, new Rook(this.board, Color.BLACK));
		this.placeNewPiece('e', 8, new King(this.board, Color.BLACK));
	}
	
}
