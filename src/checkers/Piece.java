package checkers;

public class Piece {
    private boolean isFire;
    private Board b;
    private int x;
    private int y;
    private String type;
    private boolean isKing;
    private boolean hasCaptured;
    
    public Piece(boolean isFire, Board b, int x, int y, String type) {
        this.isFire = isFire;
        this.b = b;
        // the initial position of the piece
        this.x = x;
        this.y = y;
        // "pawn", "bomb", "shield"
        this.type = type;
        this.isKing = false;  // default value
        this.hasCaptured = false;
    }
    
    public boolean isFire() {
        return isFire;
    }
    
    //  0 for fire pieces, 1 for water pieces.
    public int side() {
        if (isFire) return 0;
        return 1;
    }
    
    public boolean isKing() {
        return isKing;
    }
    
    public boolean isBomb() {
        return (type.equals("bomb"));
    }
    
    public boolean isShield() {
        return (type.equals("shield"));
    }
    
    /*
     * assume the movement is valid.
     * crowned
     * 1. single step (piece hasn't moved)
     * 2. captures
     * first capture (piece hasn't moved) (bomb explodes)
     * second capture (piece has captured)
     */
    public void move(int x, int y) {
        if (!isBomb()) {  // not bomb
            if (Math.abs(this.x - x) == 2) {
                b.remove((this.x + x)/2, (this.y + y)/2);
                hasCaptured = true;
            }
            b.remove(this.x, this.y);
            if (isFire && y == 7 || !isFire && y == 0) isKing = true;
            b.place(this, x, y);
            this.x = x;
            this.y = y;
        }  else { //bomb
            if (Math.abs(this.x - x) == 1) {
                b.remove(this.x, this.y);
                if (isFire && y == 7 || !isFire && y == 0) isKing = true;
                b.place(this, x, y);
                this.x = x;
                this.y = y;
            } else {
                b.remove((this.x + x)/2, (this.y + y)/2);
                explode(x, y);
                b.remove(this.x, this.y);
                hasCaptured = true;
            }
        }
    }
    
    private void explode(int x, int y) {
        for (int i = x-1; i <= x+1; i++) {
            for (int j = y-1; j <= y+1; j++) {
                if (b.pieceAt(i, j) != null && !b.pieceAt(i, j).isShield()) {
                    b.remove(i, j);
                }
            }
        }
    }
    
    public boolean hasCaptured() {
        return hasCaptured;
    }
    
    public void doneCapturing() {
        hasCaptured = false;
    }
}
