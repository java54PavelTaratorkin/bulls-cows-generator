package telran.bullscows;

public record GameGamer(Long id, Long gameId, String gamerId, boolean isWinner) {
    @Override
    public String toString() {
        return id + "," + gameId + "," + gamerId + "," + isWinner;
    }
}