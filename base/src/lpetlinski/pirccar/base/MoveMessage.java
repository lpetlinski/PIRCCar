package lpetlinski.pirccar.base;

import lpetlinski.simpleconnection.events.Message;

public class MoveMessage implements Message {
    private Move move;
    private Turn turn;

    public Move getMove() {
        return move;
    }

    public void setMove(Move move) {
        this.move = move;
    }

    public Turn getTurn() {
        return turn;
    }

    public void setTurn(Turn turn) {
        this.turn = turn;
    }

    @Override
    public String toString() {
        return this.move.toString() + " " + this.turn.toString();
    }
}
