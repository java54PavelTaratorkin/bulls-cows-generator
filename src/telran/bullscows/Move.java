package telran.bullscows;

public record Move(Long id, String sequence, int bulls, int cows, Long gameGamerId) {
    @Override
    public String toString() {
        return id + "," + sequence + "," + bulls + "," + cows + "," + gameGamerId;
    }
}