package telran.bullscows;

public interface BullsCowsCSVGenerator {
    public void generateGamerCsv(String gamersCsv);
    public void generateGamesCsv(String gamesCsv);
    public void generateGamesGamersCsv(String gamesGamersCsv);
    public void generateMovesCsv(String movesCsv);
}