package checkers;

/**
 * Created by liren
 */
public class Main {
    public static void main(String[] args) {
        StdDrawPlus.setXscale(0, 8);
        StdDrawPlus.setYscale(0, 8);
        Board b = new Board(false);  //set true to create empty board
        b.defaultSetup();
        while (true) {
            b.drawBoard();
            if (StdDrawPlus.mousePressed()) {  //test place
                double x = StdDrawPlus.mouseX();
                double y = StdDrawPlus.mouseY();
//                checkers.Piece p = new checkers.Piece(true, b, 1, 1, "pawn");
//                b.place(p, (int) x, (int) y);
                if (b.canSelect((int) x, (int) y)) b.select((int) x, (int) y);
            }
            if (StdDrawPlus.isSpacePressed() && b.canEndTurn()) {  //test remove
                b.endTurn();
            }
            if (StdDrawPlus.isNPressed()) {
                b = new Board(false);
                b.defaultSetup();
            }
            if (b.winner() != null) {
                System.out.println(b.winner());
                return;
            }
            StdDrawPlus.show(25);  // 10 - 25 ms
        }
    }
}
