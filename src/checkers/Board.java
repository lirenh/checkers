package checkers;

public class Board {
    private boolean shouldBeEmpty;
    private Piece[][] pieces;
    private boolean isFiresTurn;
    private boolean hasMoved;  // true if player has moved a piece
    private int xSelected; // the coordinates of the current selected piece
    private int ySelected;
    
    public Board(boolean shouldBeEmpty) {
        // initialize an empty board if true.
        this.shouldBeEmpty = shouldBeEmpty;
        this.pieces = new Piece[8][8];
        this.isFiresTurn = true;
        this.hasMoved = false;
        xSelected = -1;
        ySelected = -1;
    }
    
    /** 
     * Draw an board.
     */
    protected void drawBoard() {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (i == xSelected && j == ySelected) StdDrawPlus.setPenColor(StdDrawPlus.WHITE);
                else if ((i + j) % 2 == 0) StdDrawPlus.setPenColor(StdDrawPlus.GRAY);
                else StdDrawPlus.setPenColor(StdDrawPlus.RED);
                StdDrawPlus.filledSquare(i + .5, j + .5, .5);
                Piece p = pieceAt(i, j); 
                if (p != null) {
                    String type, side, king, pic;
                    if (p.isBomb()) {
                        type = "bomb";
                    } else if (p.isShield()) {
                        type = "shield";                       
                    } else {
                        type = "pawn";
                    }
                    if (p.isFire()) side = "-fire";
                    else side = "-water";
                    if (p.isKing()) king = "-crowned";
                    else king = "";
                    
                    pic = "img/" + type + side + king + ".png";
                    StdDrawPlus.picture(i + .5, j + .5, pic, 1, 1);                    
                }
            }
        }
    }
    
    /**
     * Place pieces on the board with the default layout.
     */
    protected void defaultSetup() {
        if (shouldBeEmpty) return;
        for (int i = 1; i < 8; i += 2) pieces[i][7] = new Piece(false, this, i, 7, "pawn");
        for (int i = 0; i < 8; i += 2) pieces[i][6] = new Piece(false, this, i, 6, "shield");
        for (int i = 1; i < 8; i += 2) pieces[i][5] = new Piece(false, this, i, 5, "bomb");     
        for (int i = 0; i < 8; i += 2) pieces[i][2] = new Piece(true, this, i, 2, "bomb");
        for (int i = 1; i < 8; i += 2) pieces[i][1] = new Piece(true, this, i, 1, "shield");
        for (int i = 0; i < 8; i += 2) pieces[i][0] = new Piece(true, this, i, 0, "pawn");
    }
    
    /**
     * Gets the piece at position(x, y) on the board, or null if there is no piece. if(x, y) are out of bounds, return null;
     * @param x the x-coordinate
     * @param y the y-coordinate
     * @return the piece if it exists, null otherwise
     */
    public Piece pieceAt(int x, int y) {
        if ( x < 0 || x > 7 || y < 0 || y > 7) return null;
        return pieces[x][y];
    }
    
    // return true if the square at (x, y) can be selected.
    public boolean canSelect(int x, int y) {
        if (x < 0 || x > 7 || y < 0 || y > 7) return false;
        Piece p = pieceAt(x, y);
        //select a piece
        if (!hasMoved && p != null && (p.isFire() == isFiresTurn)) return true;
        //select a empty square
        if (!hasMoved && (p == null) && (xSelected != -1) 
                && (validMove(xSelected, ySelected, x, y) > 0)) return true;
        if ((p == null) && (xSelected != -1) && pieceAt(xSelected, ySelected).hasCaptured()
                && (validMove(xSelected, ySelected, x, y) == 2)) return true;
        return false;
    }
    
    // return 0 if move is not valid, 1 if it is a valid one step move, 2 if it is a valid capture.
    private int validMove(int xi, int yi, int xf, int yf) {
        int flag = 0;
        if (xf < 0 || xf > 7 || yf < 0 || yf > 7 || pieces[xi][yi] == null || pieces[xf][yf] != null) flag = 0;
        Piece p = pieceAt(xi, yi);
        Piece p0 = pieceAt((xi+xf)/2, (yi+yf)/2);
        if (p.isKing()) {
            if (Math.abs(xi-xf) == 1 && Math.abs(yi-yf) == 1) flag = 1;  //move one step without capturing
            if (Math.abs(xi-xf) == 2 && Math.abs(yi-yf) == 2) {
                if (p0 != null && (p.isFire() != p0.isFire())) flag = 2;  //capturing
            }
        } else if (!p.isKing()) {
            if (Math.abs(xi-xf) == 1 && Math.abs(yi-yf) == 1 && onward(xi, yi, xf, yf)) flag = 1;  //move one step without capturing
            if (Math.abs(xi-xf) == 2 && Math.abs(yi-yf) == 2 && onward(xi, yi, xf, yf)) {
                if (p0 != null && (p.isFire() != p0.isFire())) flag = 2;  //capturing
            }
        } else {
            flag = 0;
        }
        return flag;
    }
    
    // Check if a normal piece is moving forward.
    private boolean onward(int xi, int yi, int xf, int yf) {
        Piece p = pieceAt(xi, yi);
        if (p.isFire() && yf > yi) {
            return true;
        } else if (!p.isFire() && yf < yi) {
            return true;
        }
        return false;
    }
    
    // Sets xSelect and ySelect. Assumes canSelect(x, y) returns true
    public void select(int x, int y) {
        if (pieceAt(x, y) == null) {
            hasMoved = true;
            pieceAt(xSelected, ySelected).move(x, y);
            if (pieceAt(x, y) == null) {  //capturing piece exploded
                xSelected = -1;
                ySelected = -1;
                return;
            }
        }
        xSelected = x; //selecting piece OR safe capture
        ySelected = y;
    }
    
    // for test use
    public void place(Piece p, int x, int y) {
        if ( x < 0 || x > 7 || y < 0 || y > 7 || p == null || pieceAt(x, y) != null) return;
        pieces[x][y] = p;
    }
    
    //
    public Piece remove(int x, int y) {
        if ( x < 0 || x > 7 || y < 0 || y > 7) {
            System.out.println("The coordinates of the piece to be removed is out of bounds");
            return null;
        } else if (pieceAt(x, y) == null) {
            System.out.println("No piece can be removed.");
            return null;
        } else {
            Piece p = pieceAt(x, y);
            pieces[x][y] = null;
            return p;
        }
    }
    
    public boolean canEndTurn() {
        return hasMoved;
    }
    
    public void endTurn() {
        isFiresTurn = !isFiresTurn;
        xSelected = -1;
        ySelected = -1;
        hasMoved = false;
    }
    
    // return "Fire", "Water", "No one", or null
    public String winner() {
        int nFire = 0, nWater = 0;
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                Piece p = pieceAt(i, j);
                if (p != null) {
                    if (p.isFire()) nFire++;
                    else nWater++;
                }
            }
        }
        if (nFire == 0 && nWater == 0) {
            return "No one";
        } else if (nFire != 0 && nWater == 0) {
            return "Fire";
        } else if (nFire == 0 && nWater != 0) {
            return "Water";
        } else {
            return null;
        }
    }
}
