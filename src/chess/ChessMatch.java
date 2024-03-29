package chess;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import boardgame.Board;
import boardgame.Piece;
import boardgame.Position;
import chess.pieces.Bishop;
import chess.pieces.King;
import chess.pieces.Knight;
import chess.pieces.Pawn;
import chess.pieces.Queen;
import chess.pieces.Rook;

public class ChessMatch {

	private int turn;
	private Color currentPlayer;
	private Board board;
	private boolean check;
	private boolean checkMate;
	
	// TODO - enPassantVulnerable deve ser propriedade do peão, passar para classe pawn 
	private ChessPiece enPassantVulnerable; 

	private List<ChessPiece> piecesOnTheBoard = new ArrayList<>();
	private List<ChessPiece> capturedPieces = new ArrayList<>();

	public ChessMatch() {
		this.board = new Board(8, 8);
		turn = 1;
		currentPlayer = Color.WHITE;
		this.initialSetup();
	}

	public int getTurn() {
		return turn;
	}

	public Color getCurrentPlayer() {
		return currentPlayer;
	}

	public boolean isCheck() {
		return check;
	}

	public boolean isCheckMate() {
		return checkMate;
	}

	public ChessPiece getEnPassantVulnerable() {
		return enPassantVulnerable;
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

	// TODO - Melhorar lógica
	public ChessPiece performChessMove(ChessPosition sourcePosition, ChessPosition targetPosition) {
		Position source = sourcePosition.toPosition();
		Position target = targetPosition.toPosition();
		this.validateSourcePosition(source);
		this.validateTargetPosition(source, target);
		Piece capturedPiece = this.makeMove(source, target);

		if (this.testCheck(this.currentPlayer)) {
			this.undoMove(source, target, capturedPiece);
			throw new ChessException("Você não pode se colocar em check!");
		}

		ChessPiece movedPiece = (ChessPiece)this.board.piece(target);
		
		check = this.testCheck(this.opponent(this.currentPlayer));

		if (this.testCheckMate(this.opponent(this.currentPlayer))) {
			this.checkMate = true;
		} else {
			this.nextTurn();
		}
		
		// Special move en passant
		if (movedPiece instanceof Pawn && (Math.abs(source.getRow() - target.getRow()) == 2)) {
			this.enPassantVulnerable = movedPiece;
		}
		
		return (ChessPiece) capturedPiece;
	}

	private Piece makeMove(Position source, Position target) {
		ChessPiece p = (ChessPiece)this.board.removePiece(source);
		p.increaseMoveCount();
		Piece capturedPiece = this.board.removePiece(target);
		this.board.placePiece(p, target);
		
		// TODO - melhorar lógica, (movimentos especiais tratar nas peças? ou encapsular em função)
		if (p instanceof King) {
			// Special move castling king side rook
			if (target.getColumn() == source.getColumn() + 2) {
				Position sourceRook = new Position(source.getRow(), source.getColumn() + 3);
				Position targetRook = new Position(source.getRow(), source.getColumn() + 1);
				ChessPiece rook = (ChessPiece)this.board.removePiece(sourceRook);
				this.board.placePiece(rook, targetRook);
				rook.increaseMoveCount();
			}
			// Special move castling queen side rook
			else if (target.getColumn() == source.getColumn() - 2) {
				Position sourceRook = new Position(source.getRow(), source.getColumn() - 4);			
				Position targetRook = new Position(source.getRow(), source.getColumn() - 1);			
				ChessPiece rook = (ChessPiece)this.board.removePiece(sourceRook);
				this.board.placePiece(rook, targetRook);
				rook.increaseMoveCount();
			}
		} else if (p instanceof Pawn) {
			// Special move en passant
			if (source.getColumn() != target.getColumn() && capturedPiece == null) {
				Position pawnPosition = new Position(source.getRow(), target.getColumn());
				capturedPiece = this.board.removePiece(pawnPosition);				
			}
		}
		
		if (capturedPiece != null) {
			this.piecesOnTheBoard.remove(capturedPiece);
			this.capturedPieces.add((ChessPiece) capturedPiece);
		}
		
		return capturedPiece;
	}

	private void undoMove(Position source, Position target, Piece capturedPiece) {
		ChessPiece p = (ChessPiece)this.board.removePiece(target);
		p.decreaseMoveCount();
		this.board.placePiece(p, source);
		if (capturedPiece != null) {
			if (capturedPiece != this.enPassantVulnerable) {
				this.board.placePiece(capturedPiece, target);
			}
			this.capturedPieces.remove(capturedPiece);
			this.piecesOnTheBoard.add((ChessPiece) capturedPiece);
		}

		if (p instanceof King) {
			// Undo Special move castling king side rook
			if (target.getColumn() == source.getColumn() + 2) {
				Position sourceRook = new Position(source.getRow(), source.getColumn() + 3);
				Position targetRook = new Position(source.getRow(), source.getColumn() + 1);
				ChessPiece rook = (ChessPiece)this.board.removePiece(targetRook);
				this.board.placePiece(rook, sourceRook);
				rook.decreaseMoveCount();
			}
			// Undo Special move castling queen side rook
			else if (target.getColumn() == source.getColumn() - 2) {
				Position sourceRook = new Position(source.getRow(), source.getColumn() - 4);			
				Position targetRook = new Position(source.getRow(), source.getColumn() - 1);			
				ChessPiece rook = (ChessPiece)this.board.removePiece(targetRook);
				this.board.placePiece(rook, sourceRook);
				rook.decreaseMoveCount();
			}
		} else if (p instanceof Pawn) {
			// Special move en passant
			if (source.getColumn() != target.getColumn() && capturedPiece == this.enPassantVulnerable) {
				Position pawnPosition = new Position(source.getRow(), target.getColumn());
				this.board.placePiece(capturedPiece, pawnPosition);				
			}
		}
	}

	private Color opponent(Color color) {
		return color == Color.WHITE ? Color.BLACK : Color.WHITE;
	}

	private ChessPiece king(Color color) {
		List<ChessPiece> kings = this.piecesOnTheBoard.parallelStream()
				.filter(p -> p.getColor() == color && p instanceof King).collect(Collectors.toList());
		if (kings.isEmpty()) {
			throw new IllegalStateException("Não há rei da com " + color + " no tabuleiro.");
		}
		if (kings.size() > 1) {
			throw new IllegalStateException("Há mais de um rei " + color + " no tabuleiro.");
		}
		return kings.get(0);
	}

	private boolean testCheck(Color color) {
		Position kingPosition = king(color).getChessPosition().toPosition();
		List<ChessPiece> opponentPieces = this.piecesOnTheBoard.parallelStream()
				.filter(p -> p.getColor() == this.opponent(color)).collect(Collectors.toList());
		for (ChessPiece p : opponentPieces) {
			boolean[][] mat = p.possibleMoves();
			if (mat[kingPosition.getRow()][kingPosition.getColumn()]) {
				return true;
			}
		}
		return false;
	}

	private boolean testCheckMate(Color color) {
		if (!testCheck(color)) {
			return false;
		}
		List<ChessPiece> myPieces = this.piecesOnTheBoard.parallelStream().filter(p -> p.getColor() == color)
				.collect(Collectors.toList());
		for (ChessPiece p : myPieces) {
			boolean mat[][] = p.possibleMoves();
			for (int i = 0; i < this.board.getRows(); i++) {
				for (int j = 0; j < this.board.getColumns(); j++) {
					if (mat[i][j]) {
						Position source = p.getChessPosition().toPosition();
						Position target = new Position(i, j);
						Piece capturedPiece = this.makeMove(source, target);
						boolean testCheck = this.testCheck(color);
						this.undoMove(source, target, capturedPiece);
						if (!testCheck) {
							return false;
						}
					}
				}
			}
		}
		return true;
	}

	private void placeNewPiece(char column, int row, ChessPiece piece) {
		this.board.placePiece(piece, new ChessPosition(column, row).toPosition());
		this.piecesOnTheBoard.add(piece);
	}

	private void initialSetup() {
		this.placeNewPiece('a', 1, new Rook(this.board, Color.WHITE));
		this.placeNewPiece('b', 1, new Knight(this.board, Color.WHITE));
		this.placeNewPiece('c', 1, new Bishop(this.board, Color.WHITE));
		this.placeNewPiece('d', 1, new Queen(this.board, Color.WHITE));
		this.placeNewPiece('e', 1, new King(this.board, Color.WHITE, this));
		this.placeNewPiece('f', 1, new Bishop(this.board, Color.WHITE));
		this.placeNewPiece('g', 1, new Knight(this.board, Color.WHITE));
		this.placeNewPiece('h', 1, new Rook(this.board, Color.WHITE));

		this.placeNewPiece('a', 8, new Rook(this.board, Color.BLACK));
		this.placeNewPiece('b', 8, new Knight(this.board, Color.BLACK));
		this.placeNewPiece('c', 8, new Bishop(this.board, Color.BLACK));
		this.placeNewPiece('d', 8, new Queen(this.board, Color.BLACK));
		this.placeNewPiece('e', 8, new King(this.board, Color.BLACK, this));
		this.placeNewPiece('f', 8, new Bishop(this.board, Color.BLACK));
		this.placeNewPiece('g', 8, new Knight(this.board, Color.BLACK));
		this.placeNewPiece('h', 8, new Rook(this.board, Color.BLACK));

		for (int i = 'a'; i <= 'h'; i++) {
			this.placeNewPiece((char)i, 2, new Pawn(this.board, Color.WHITE, this));			
			this.placeNewPiece((char)i, 7, new Pawn(this.board, Color.BLACK, this));			
		}
	}

	private void validateSourcePosition(Position source) {
		if (!this.board.thereIsAPiece(source)) {
			throw new ChessException("Não existe peça na posição de origem");
		}
		if (this.currentPlayer != ((ChessPiece) board.piece(source)).getColor()) {
			throw new ChessException("A peça escolhida não é sua");
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

	private void nextTurn() {
		this.turn++;
		this.currentPlayer = this.currentPlayer == Color.WHITE ? Color.BLACK : Color.WHITE;
	}

}
