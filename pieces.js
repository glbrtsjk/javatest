/**
 * Chess Pieces Implementation
 * This file contains all chess piece classes with their movement rules
 */

// Base chess piece class
class ChessPiece {
    constructor(color, symbol) {
        this.color = color; // 'white' or 'black'
        this.symbol = symbol; // Unicode chess symbol
        this.hasMoved = false; // Track if piece has moved (for castling, en passant)
    }

    // Abstract method - to be implemented by each piece
    getPossibleMoves(position, board) {
        throw new Error('getPossibleMoves must be implemented by subclass');
    }

    // Check if a position is within board bounds
    isValidPosition(row, col) {
        return row >= 0 && row < 8 && col >= 0 && col < 8;
    }

    // Check if a square is empty
    isEmpty(row, col, board) {
        return !board[row][col];
    }

    // Check if a square contains an enemy piece
    isEnemy(row, col, board) {
        const piece = board[row][col];
        return piece && piece.color !== this.color;
    }

    // Check if a square contains a friendly piece
    isFriendly(row, col, board) {
        const piece = board[row][col];
        return piece && piece.color === this.color;
    }

    // Get all moves in a direction until blocked
    getMovesInDirection(position, board, rowDelta, colDelta) {
        const moves = [];
        const [startRow, startCol] = position;
        let row = startRow + rowDelta;
        let col = startCol + colDelta;

        while (this.isValidPosition(row, col)) {
            if (this.isEmpty(row, col, board)) {
                moves.push([row, col]);
            } else if (this.isEnemy(row, col, board)) {
                moves.push([row, col]); // Can capture
                break; // Can't move beyond
            } else {
                break; // Blocked by friendly piece
            }
            row += rowDelta;
            col += colDelta;
        }

        return moves;
    }
}

// Pawn class
class Pawn extends ChessPiece {
    constructor(color) {
        const symbol = color === 'white' ? '♙' : '♟';
        super(color, symbol);
    }

    getPossibleMoves(position, board) {
        const moves = [];
        const [row, col] = position;
        const direction = this.color === 'white' ? -1 : 1; // White moves up, black moves down
        const startRow = this.color === 'white' ? 6 : 1;

        // Forward move
        const newRow = row + direction;
        if (this.isValidPosition(newRow, col) && this.isEmpty(newRow, col, board)) {
            moves.push([newRow, col]);

            // Double move from starting position
            if (row === startRow) {
                const doubleRow = row + 2 * direction;
                if (this.isValidPosition(doubleRow, col) && this.isEmpty(doubleRow, col, board)) {
                    moves.push([doubleRow, col]);
                }
            }
        }

        // Diagonal captures
        for (const colDelta of [-1, 1]) {
            const captureRow = row + direction;
            const captureCol = col + colDelta;
            if (this.isValidPosition(captureRow, captureCol) && 
                this.isEnemy(captureRow, captureCol, board)) {
                moves.push([captureRow, captureCol]);
            }
        }

        return moves;
    }
}

// Rook class
class Rook extends ChessPiece {
    constructor(color) {
        const symbol = color === 'white' ? '♖' : '♜';
        super(color, symbol);
    }

    getPossibleMoves(position, board) {
        const moves = [];
        const directions = [[0, 1], [0, -1], [1, 0], [-1, 0]]; // Right, Left, Down, Up

        for (const [rowDelta, colDelta] of directions) {
            moves.push(...this.getMovesInDirection(position, board, rowDelta, colDelta));
        }

        return moves;
    }
}

// Knight class
class Knight extends ChessPiece {
    constructor(color) {
        const symbol = color === 'white' ? '♘' : '♞';
        super(color, symbol);
    }

    getPossibleMoves(position, board) {
        const moves = [];
        const [row, col] = position;
        const knightMoves = [
            [-2, -1], [-2, 1], [-1, -2], [-1, 2],
            [1, -2], [1, 2], [2, -1], [2, 1]
        ];

        for (const [rowDelta, colDelta] of knightMoves) {
            const newRow = row + rowDelta;
            const newCol = col + colDelta;

            if (this.isValidPosition(newRow, newCol) && 
                !this.isFriendly(newRow, newCol, board)) {
                moves.push([newRow, newCol]);
            }
        }

        return moves;
    }
}

// Bishop class
class Bishop extends ChessPiece {
    constructor(color) {
        const symbol = color === 'white' ? '♗' : '♝';
        super(color, symbol);
    }

    getPossibleMoves(position, board) {
        const moves = [];
        const directions = [[1, 1], [1, -1], [-1, 1], [-1, -1]]; // All diagonals

        for (const [rowDelta, colDelta] of directions) {
            moves.push(...this.getMovesInDirection(position, board, rowDelta, colDelta));
        }

        return moves;
    }
}

// Queen class
class Queen extends ChessPiece {
    constructor(color) {
        const symbol = color === 'white' ? '♕' : '♛';
        super(color, symbol);
    }

    getPossibleMoves(position, board) {
        const moves = [];
        const directions = [
            [0, 1], [0, -1], [1, 0], [-1, 0], // Rook moves
            [1, 1], [1, -1], [-1, 1], [-1, -1] // Bishop moves
        ];

        for (const [rowDelta, colDelta] of directions) {
            moves.push(...this.getMovesInDirection(position, board, rowDelta, colDelta));
        }

        return moves;
    }
}

// King class
class King extends ChessPiece {
    constructor(color) {
        const symbol = color === 'white' ? '♔' : '♚';
        super(color, symbol);
    }

    getPossibleMoves(position, board) {
        const moves = [];
        const [row, col] = position;
        const directions = [
            [0, 1], [0, -1], [1, 0], [-1, 0],
            [1, 1], [1, -1], [-1, 1], [-1, -1]
        ];

        for (const [rowDelta, colDelta] of directions) {
            const newRow = row + rowDelta;
            const newCol = col + colDelta;

            if (this.isValidPosition(newRow, newCol) && 
                !this.isFriendly(newRow, newCol, board)) {
                moves.push([newRow, newCol]);
            }
        }

        return moves;
    }

    // Check if castling is possible
    canCastle(position, board, kingside = true) {
        if (this.hasMoved) return false;

        const [row, col] = position;
        const rookCol = kingside ? 7 : 0;
        const rook = board[row][rookCol];

        // Check if rook exists and hasn't moved
        if (!rook || rook.constructor.name !== 'Rook' || rook.hasMoved) {
            return false;
        }

        // Check if squares between king and rook are empty
        const start = Math.min(col, rookCol) + 1;
        const end = Math.max(col, rookCol);
        for (let c = start; c < end; c++) {
            if (board[row][c]) return false;
        }

        return true;
    }
}

// Factory function to create pieces
function createPiece(type, color) {
    switch (type) {
        case 'pawn': return new Pawn(color);
        case 'rook': return new Rook(color);
        case 'knight': return new Knight(color);
        case 'bishop': return new Bishop(color);
        case 'queen': return new Queen(color);
        case 'king': return new King(color);
        default: throw new Error(`Unknown piece type: ${type}`);
    }
}