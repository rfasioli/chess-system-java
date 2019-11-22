package chess.pieces;

import boardgame.Board;
import boardgame.Position;
import chess.ChessMatch;
import chess.ChessPiece;
import chess.Color;

public class Pawn extends ChessPiece {

	private ChessMatch chessMatch;

	public Pawn(Board board, Color color, ChessMatch chessMatch) {
		super(board, color);
		this.chessMatch = chessMatch;
	}
	
	public String toString() {
		return "P";
	}

	@Override
	public boolean[][] possibleMoves() {
		boolean[][] mat = new boolean[this.getBoard().getRows()][this.getBoard().getColumns()];
		
		Position p = new Position(0, 0);
		
		if (this.getColor() == Color.WHITE) {
			// above
			p.setValues(position.getRow() - 1, position.getColumn());
			if (this.getBoard().positionExists(p) && !this.getBoard().thereIsAPiece(p)) {
				mat[p.getRow()][p.getColumn()] = true;
				//first move
				if (this.getMoveCount() == 0) {
					p.setValues(position.getRow() - 2, position.getColumn());
					if (this.getBoard().positionExists(p) && !this.getBoard().thereIsAPiece(p)) {
						mat[p.getRow()][p.getColumn()] = true;
					}
				}
			}
			
			// nw
			p.setValues(position.getRow() - 1, position.getColumn() - 1);
			if (this.getBoard().positionExists(p) && this.isThereOpponentPiece(p)) {
				mat[p.getRow()][p.getColumn()] = true;
			}
			// TODO - melhorar lógica
			// Special move en passant
			Position left = new Position(position.getRow(), position.getColumn() - 1);
			if (this.getBoard().positionExists(left) && this.isThereOpponentPiece(left) && this.getBoard().piece(left) == this.chessMatch.getEnPassantVulnerable()) {
				mat[p.getRow()][p.getColumn()] = true;
			}

			// ne
			p.setValues(position.getRow() - 1, position.getColumn() + 1);
			if (this.getBoard().positionExists(p) && this.isThereOpponentPiece(p)) {
				mat[p.getRow()][p.getColumn()] = true;
			}
			// TODO - melhorar lógica
			// Special move en passant
			Position right = new Position(position.getRow(), position.getColumn() + 1);
			if (this.getBoard().positionExists(right) && this.isThereOpponentPiece(right) && this.getBoard().piece(right) == this.chessMatch.getEnPassantVulnerable()) {
				mat[p.getRow()][p.getColumn()] = true;
			}
			
			
		} else {
			// below
			p.setValues(position.getRow() + 1, position.getColumn());
			if (this.getBoard().positionExists(p) && !this.getBoard().thereIsAPiece(p)) {
				mat[p.getRow()][p.getColumn()] = true;
				//first move
				if (this.getMoveCount() == 0) {
					p.setValues(position.getRow() + 2, position.getColumn());
					if (this.getBoard().positionExists(p) && !this.getBoard().thereIsAPiece(p)) {
						mat[p.getRow()][p.getColumn()] = true;
					}
				}
			}

			// sw
			p.setValues(position.getRow() + 1, position.getColumn() - 1);
			if (this.getBoard().positionExists(p) && this.isThereOpponentPiece(p)) {
				mat[p.getRow()][p.getColumn()] = true;
			}
			// TODO - melhorar lógica
			// Special move en passant
			Position left = new Position(position.getRow(), position.getColumn() - 1);
			if (this.getBoard().positionExists(left) && this.isThereOpponentPiece(left) && this.getBoard().piece(left) == this.chessMatch.getEnPassantVulnerable()) {
				mat[p.getRow()][p.getColumn()] = true;
			}

			// se
			p.setValues(position.getRow() + 1, position.getColumn() + 1);
			if (this.getBoard().positionExists(p) && this.isThereOpponentPiece(p)) {
				mat[p.getRow()][p.getColumn()] = true;
			}
			// TODO - melhorar lógica
			// Special move en passant
			Position right = new Position(position.getRow(), position.getColumn() + 1);
			if (this.getBoard().positionExists(right) && this.isThereOpponentPiece(right) && this.getBoard().piece(right) == this.chessMatch.getEnPassantVulnerable()) {
				mat[p.getRow()][p.getColumn()] = true;
			}
			
		}
		
		return mat;
	}

}
