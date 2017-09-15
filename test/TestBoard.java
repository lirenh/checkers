import static org.junit.Assert.*;

import checkers.Board;
import checkers.Piece;
import org.junit.Test;

public class TestBoard {

    @Test
    public void testWinner() {
        Board b = new Board(true);
        assertEquals("No one", b.winner());
        b.place(new Piece(true, b, 1, 1, "pawn"), 1, 1);
        assertEquals("Fire", b.winner());
        b.place(new Piece(false, b, 1, 1, "pawn"), 2, 2);
        assertNull(b.winner());
        b.remove(1, 1);
    }
    
    
    
    public static void main(String[] args) {
        jh61b.junit.textui.runClasses(TestBoard.class);
    }
}
