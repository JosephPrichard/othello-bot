package dto;

public class Profile
{
    private Player player;
    private float elo;
    private int won;
    private int lost;
    private int drawn;

    public Profile(Player player) {
        this.player = player;
        this.elo = 1000;
        this.won = 0;
        this.lost = 0;
        this.drawn = 0;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public float getElo() {
        return elo;
    }

    public void setElo(float elo) {
        this.elo = elo;
    }

    public int getWon() {
        return won;
    }

    public void setWon(int won) {
        this.won = won;
    }

    public int getLost() {
        return lost;
    }

    public void setLost(int lost) {
        this.lost = lost;
    }

    public int getDrawn() {
        return drawn;
    }

    public void setDrawn(int drawn) {
        this.drawn = drawn;
    }

    public float getWinRate() {
        return won / (float)(won + lost + drawn);
    }
}
