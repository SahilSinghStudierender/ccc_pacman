package at.coding.contest;

public class Ghost {
    int row;
    int col;
    String movement;

    public Ghost(int row, int col, String movement) {
        this.row = row;
        this.col = col;
        this.movement = movement;
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

    public String getMovement() {
        return movement;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public void setCol(int col) {
        this.col = col;
    }

    public void setMovement(String movement) {
        this.movement = movement;
    }
}
