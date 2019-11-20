package chess;

import boardgame.Board;
import boardgame.Piece;
import boardgame.Position;
import chess.pieces.Bishop;
import chess.pieces.Horse;
import chess.pieces.King;
import chess.pieces.Pawn;
import chess.pieces.Queen;
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
	
	public boolean[][] possibleMoves(ChessPosition sourcePosition) {
		Position source = sourcePosition.toPosition();
		this.validateSourcePosition(source);
		return this.board.piece(source).possibleMoves();
	}
	
	public ChessPiece performChessMove(ChessPosition sourcePosition, ChessPosition targetPosition) {
		Position source = sourcePosition.toPosition();
		Position target = targetPosition.toPosition();
		this.validateSourcePosition(source);
		this.validateTargetPosition(source, target);
		Piece capturedPiece = this.makeMove(source, target);
		return (ChessPiece)capturedPiece;
	}

	private void placeNewPiece(char column, int row, ChessPiece piece) {
		this.board.placePiece(piece, new ChessPosition(column, row).toPosition());		
	}

	private void initialSetup() {
		this.placeNewPiece('a', 1, new Rook(this.board, Color.WHITE));
//		this.placeNewPiece('b', 1, new Horse(this.board, Color.WHITE));
//		this.placeNewPiece('c', 1, new Bishop(this.board, Color.WHITE));
//		this.placeNewPiece('d', 1, new Queen(this.board, Color.WHITE));
		this.placeNewPiece('e', 1, new King(this.board, Color.WHITE));
//		this.placeNewPiece('f', 1, new Bishop(this.board, Color.WHITE));
//		this.placeNewPiece('g', 1, new Horse(this.board, Color.WHITE));
		this.placeNewPiece('h', 1, new Rook(this.board, Color.WHITE));

		this.placeNewPiece('a', 8, new Rook(this.board, Color.BLACK));
//		this.placeNewPiece('b', 8, new Horse(this.board, Color.BLACK));
//		this.placeNewPiece('c', 8, new Bishop(this.board, Color.BLACK));
//		this.placeNewPiece('d', 8, new Queen(this.board, Color.BLACK));
		this.placeNewPiece('e', 8, new King(this.board, Color.BLACK));
//		this.placeNewPiece('f', 8, new Bishop(this.board, Color.BLACK));
//		this.placeNewPiece('g', 8, new Horse(this.board, Color.BLACK));
		this.placeNewPiece('h', 8, new Rook(this.board, Color.BLACK));
		
//		for (int i = 'a'; i <= 'h'; i++) {
//			this.placeNewPiece((char)i, 2, new Pawn(this.board, Color.WHITE));			
//			this.placeNewPiece((char)i, 7, new Pawn(this.board, Color.BLACK));			
//		}
	}
	
	private void validateSourcePosition(Position source) {
		if (!this.board.thereIsAPiece(source)) {
			throw new ChessException("Não existe peça na posição de origem");
		}
		if (!this.board.piece(source).isThereAnyPossibleMove()) {
			throw new ChessException("Não existe movimentos possíveis para a peça escolhida");			
		}
	}
	
	private void validateTargetPosition(Position source, Position target) {
		if (!this.board.piece(source).possibleMove(target)) {
			throw new ChessException("A peça escolhida não pode ser movida para a posição de destino");
		}
	}
	
	private Piece makeMove(Position source, Position target) {
		 Piece p = this.board.removePiece(source);
		 Piece capturedPiece = this.board.removePiece(target);
		 this.board.placePiece(p, target);
		 return capturedPiece;
	}
		
}
