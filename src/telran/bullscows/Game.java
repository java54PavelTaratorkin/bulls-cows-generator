package telran.bullscows;

import java.time.LocalDateTime;

public record Game(Long id, LocalDateTime date, String sequence, boolean isFinished) {
    @Override
    public String toString() {
        return id + "," + date + "," + sequence + "," + isFinished;
    }
}