/**
 * Chess Game Implementation
 * This file contains the main game logic, board management, and UI interaction
 */

class ChessGame {
    constructor() {
        this.board = this.initializeBoard();
        this.currentPlayer = 'white';
        this.selectedSquare = null;
        this.possibleMoves = [];
        this.moveHistory = [];
        this.capturedPieces = { white: [], black: [] };
        this.gameStatus = 'playing'; // 'playing', 'check', 'checkmate', 'stalemate'
        this.kings = {
            white: [7, 4],
            black: [0, 4]
        };
        
        this.initializeUI();
        this.renderBoard();
        this.updateGameStatus();
    }

    // Initialize the chess board with starting positions
    initializeBoard() {
        const board = Array(8).fill(null).map(() => Array(8).fill(null));

        // Set up black pieces
        board[0] = [
            new Rook('black'), new Knight('black'), new Bishop('black'), new Queen('black'),
            new King('black'), new Bishop('black'), new Knight('black'), new Rook('black')
        ];
        board[1] = Array(8).fill(null).map(() => new Pawn('black'));

        // Set up white pieces
        board[6] = Array(8).fill(null).map(() => new Pawn('white'));
        board[7] = [
            new Rook('white'), new Knight('white'), new Bishop('white'), new Queen('white'),
            new King('white'), new Bishop('white'), new Knight('white'), new Rook('white')
        ];

        return board;
    }

    // Initialize UI event listeners
    initializeUI() {
        document.getElementById('new-game-btn').addEventListener('click', () => this.newGame());
        document.getElementById('undo-move-btn').addEventListener('click', () => this.undoMove());
        
        // Use event delegation for chess board clicks
        document.getElementById('chess-board').addEventListener('click', (e) => {
            const square = e.target.closest('.square');
            if (square) {
                const row = parseInt(square.dataset.row);
                const col = parseInt(square.dataset.col);
                this.handleSquareClick(row, col);
            }
        });
    }

    // Render the chess board
    renderBoard() {
        const boardElement = document.getElementById('chess-board');
        boardElement.innerHTML = '';

        for (let row = 0; row < 8; row++) {
            for (let col = 0; col < 8; col++) {
                const square = document.createElement('div');
                square.className = `square ${(row + col) % 2 === 0 ? 'light' : 'dark'}`;
                square.dataset.row = row;
                square.dataset.col = col;

                // Add piece if present
                const piece = this.board[row][col];
                if (piece) {
                    const pieceElement = document.createElement('span');
                    pieceElement.className = `piece ${piece.color}`;
                    pieceElement.textContent = piece.symbol;
                    square.appendChild(pieceElement);
                }

                // Add click event listener - removed since we're using event delegation
                // square.addEventListener('click', () => this.handleSquareClick(row, col));

                boardElement.appendChild(square);
            }
        }

        this.highlightSquares();
    }

    // Handle square click events
    handleSquareClick(row, col) {
        if (this.gameStatus === 'checkmate' || this.gameStatus === 'stalemate') {
            return;
        }

        const piece = this.board[row][col];

        // If no square is selected
        if (!this.selectedSquare) {
            if (piece && piece.color === this.currentPlayer) {
                this.selectSquare(row, col);
            }
            return;
        }

        const [selectedRow, selectedCol] = this.selectedSquare;

        // If clicking the same square, deselect
        if (row === selectedRow && col === selectedCol) {
            this.deselectSquare();
            return;
        }

        // If clicking another piece of the same color, select it instead
        if (piece && piece.color === this.currentPlayer) {
            this.selectSquare(row, col);
            return;
        }

        // Try to make a move
        if (this.isValidMove(this.selectedSquare, [row, col])) {
            this.makeMove(this.selectedSquare, [row, col]);
        } else {
            this.deselectSquare();
        }
    }

    // Select a square and show possible moves
    selectSquare(row, col) {
        this.selectedSquare = [row, col];
        const piece = this.board[row][col];
        this.possibleMoves = this.getValidMoves(piece, [row, col]);
        this.renderBoard();
    }

    // Deselect the current square
    deselectSquare() {
        this.selectedSquare = null;
        this.possibleMoves = [];
        this.renderBoard();
    }

    // Get valid moves for a piece (excluding moves that would put king in check)
    getValidMoves(piece, position) {
        const possibleMoves = piece.getPossibleMoves(position, this.board);
        const validMoves = [];

        for (const move of possibleMoves) {
            if (this.wouldMoveBeValid(position, move)) {
                validMoves.push(move);
            }
        }

        return validMoves;
    }

    // Check if a move would be valid (doesn't put own king in check)
    wouldMoveBeValid(from, to) {
        const [fromRow, fromCol] = from;
        const [toRow, toCol] = to;
        
        // Make a temporary move
        const originalPiece = this.board[toRow][toCol];
        const movingPiece = this.board[fromRow][fromCol];
        
        this.board[toRow][toCol] = movingPiece;
        this.board[fromRow][fromCol] = null;

        // Update king position if king moved
        if (movingPiece.constructor.name === 'King') {
            this.kings[movingPiece.color] = [toRow, toCol];
        }

        // Check if king is in check after the move
        const isValid = !this.isKingInCheck(movingPiece.color);

        // Restore the board
        this.board[fromRow][fromCol] = movingPiece;
        this.board[toRow][toCol] = originalPiece;

        // Restore king position
        if (movingPiece.constructor.name === 'King') {
            this.kings[movingPiece.color] = [fromRow, fromCol];
        }

        return isValid;
    }

    // Check if a move is valid
    isValidMove(from, to) {
        return this.possibleMoves.some(move => move[0] === to[0] && move[1] === to[1]);
    }

    // Make a move
    makeMove(from, to) {
        const [fromRow, fromCol] = from;
        const [toRow, toCol] = to;
        const movingPiece = this.board[fromRow][fromCol];
        const capturedPiece = this.board[toRow][toCol];

        // Record the move
        const moveNotation = this.getMoveNotation(from, to, movingPiece, capturedPiece);
        
        // Handle captured piece
        if (capturedPiece) {
            this.capturedPieces[capturedPiece.color].push(capturedPiece);
        }

        // Make the move
        this.board[toRow][toCol] = movingPiece;
        this.board[fromRow][fromCol] = null;
        movingPiece.hasMoved = true;

        // Update king position if king moved
        if (movingPiece.constructor.name === 'King') {
            this.kings[movingPiece.color] = [toRow, toCol];
        }

        // Add to move history
        this.moveHistory.push({
            from,
            to,
            piece: movingPiece,
            capturedPiece,
            notation: moveNotation
        });

        // Switch players
        this.currentPlayer = this.currentPlayer === 'white' ? 'black' : 'white';

        // Update game state
        this.checkGameStatus();
        this.deselectSquare();
        this.updateUI();
    }

    // Generate move notation
    getMoveNotation(from, to, piece, capturedPiece) {
        const [fromRow, fromCol] = from;
        const [toRow, toCol] = to;
        const fromSquare = String.fromCharCode(97 + fromCol) + (8 - fromRow);
        const toSquare = String.fromCharCode(97 + toCol) + (8 - toRow);
        
        let notation = piece.constructor.name.charAt(0);
        if (notation === 'P') notation = ''; // Don't show P for pawn
        if (piece.constructor.name === 'Knight') notation = 'N'; // Knight uses N to avoid confusion with King
        
        if (capturedPiece) notation += 'x';
        notation += toSquare;
        
        return notation;
    }

    // Check if king is in check
    isKingInCheck(color) {
        const kingPosition = this.kings[color];
        const enemyColor = color === 'white' ? 'black' : 'white';

        // Check if any enemy piece can attack the king
        for (let row = 0; row < 8; row++) {
            for (let col = 0; col < 8; col++) {
                const piece = this.board[row][col];
                if (piece && piece.color === enemyColor) {
                    const moves = piece.getPossibleMoves([row, col], this.board);
                    if (moves.some(move => move[0] === kingPosition[0] && move[1] === kingPosition[1])) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    // Check game status (check, checkmate, stalemate)
    checkGameStatus() {
        const isInCheck = this.isKingInCheck(this.currentPlayer);
        const hasValidMoves = this.hasValidMoves(this.currentPlayer);

        if (isInCheck && !hasValidMoves) {
            this.gameStatus = 'checkmate';
        } else if (!isInCheck && !hasValidMoves) {
            this.gameStatus = 'stalemate';
        } else if (isInCheck) {
            this.gameStatus = 'check';
        } else {
            this.gameStatus = 'playing';
        }
    }

    // Check if player has any valid moves
    hasValidMoves(color) {
        for (let row = 0; row < 8; row++) {
            for (let col = 0; col < 8; col++) {
                const piece = this.board[row][col];
                if (piece && piece.color === color) {
                    const validMoves = this.getValidMoves(piece, [row, col]);
                    if (validMoves.length > 0) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    // Highlight selected square and possible moves
    highlightSquares() {
        // Highlight selected square
        if (this.selectedSquare) {
            const [row, col] = this.selectedSquare;
            const square = document.querySelector(`[data-row="${row}"][data-col="${col}"]`);
            square.classList.add('selected');
        }

        // Highlight possible moves
        for (const [row, col] of this.possibleMoves) {
            const square = document.querySelector(`[data-row="${row}"][data-col="${col}"]`);
            if (this.board[row][col]) {
                square.classList.add('possible-capture');
            } else {
                square.classList.add('possible-move');
            }
        }

        // Highlight king if in check
        if (this.gameStatus === 'check') {
            const [row, col] = this.kings[this.currentPlayer];
            const square = document.querySelector(`[data-row="${row}"][data-col="${col}"]`);
            square.classList.add('in-check');
        }
    }

    // Update UI elements
    updateUI() {
        this.updateGameStatus();
        this.updateMoveHistory();
        this.updateCapturedPieces();
    }

    // Update game status display
    updateGameStatus() {
        document.getElementById('current-player').textContent = 
            this.currentPlayer.charAt(0).toUpperCase() + this.currentPlayer.slice(1);
        
        const messageElement = document.getElementById('game-message');
        switch (this.gameStatus) {
            case 'check':
                messageElement.textContent = 'Check!';
                break;
            case 'checkmate':
                const winner = this.currentPlayer === 'white' ? 'Black' : 'White';
                messageElement.textContent = `Checkmate! ${winner} wins!`;
                break;
            case 'stalemate':
                messageElement.textContent = 'Stalemate! Game is a draw.';
                break;
            default:
                messageElement.textContent = '';
        }
    }

    // Update move history display
    updateMoveHistory() {
        const moveListElement = document.getElementById('move-list');
        moveListElement.innerHTML = '';
        
        this.moveHistory.forEach((move, index) => {
            const moveElement = document.createElement('div');
            moveElement.className = 'move-item';
            moveElement.textContent = `${Math.floor(index / 2) + 1}. ${move.notation}`;
            moveListElement.appendChild(moveElement);
        });
        
        moveListElement.scrollTop = moveListElement.scrollHeight;
    }

    // Update captured pieces display
    updateCapturedPieces() {
        const whiteContainer = document.getElementById('captured-white');
        const blackContainer = document.getElementById('captured-black');
        
        whiteContainer.innerHTML = '';
        blackContainer.innerHTML = '';
        
        this.capturedPieces.white.forEach(piece => {
            const pieceElement = document.createElement('span');
            pieceElement.className = 'captured-piece white';
            pieceElement.textContent = piece.symbol;
            whiteContainer.appendChild(pieceElement);
        });
        
        this.capturedPieces.black.forEach(piece => {
            const pieceElement = document.createElement('span');
            pieceElement.className = 'captured-piece black';
            pieceElement.textContent = piece.symbol;
            blackContainer.appendChild(pieceElement);
        });
    }

    // Start a new game
    newGame() {
        this.board = this.initializeBoard();
        this.currentPlayer = 'white';
        this.selectedSquare = null;
        this.possibleMoves = [];
        this.moveHistory = [];
        this.capturedPieces = { white: [], black: [] };
        this.gameStatus = 'playing';
        this.kings = {
            white: [7, 4],
            black: [0, 4]
        };
        
        this.renderBoard();
        this.updateUI();
    }

    // Undo the last move
    undoMove() {
        if (this.moveHistory.length === 0) return;
        
        const lastMove = this.moveHistory.pop();
        const { from, to, piece, capturedPiece } = lastMove;
        const [fromRow, fromCol] = from;
        const [toRow, toCol] = to;
        
        // Move piece back
        this.board[fromRow][fromCol] = piece;
        this.board[toRow][toCol] = capturedPiece;
        piece.hasMoved = this.moveHistory.some(move => move.piece === piece);
        
        // Restore captured piece
        if (capturedPiece) {
            const capturedArray = this.capturedPieces[capturedPiece.color];
            const index = capturedArray.indexOf(capturedPiece);
            if (index > -1) {
                capturedArray.splice(index, 1);
            }
        }
        
        // Update king position if king moved
        if (piece.constructor.name === 'King') {
            this.kings[piece.color] = from;
        }
        
        // Switch player back
        this.currentPlayer = this.currentPlayer === 'white' ? 'black' : 'white';
        
        // Update game state
        this.checkGameStatus();
        this.deselectSquare();
        this.updateUI();
    }
}

// Initialize the game when the page loads
document.addEventListener('DOMContentLoaded', () => {
    window.chessGame = new ChessGame();
});