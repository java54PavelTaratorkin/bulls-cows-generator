package telran.bullscows;

public class BullsCowsCSVGeneratorAppl {
	
    private static final String GAMES_CSV = "game.csv";
    private static final String GAMERS_CSV = "gamer.csv";
    private static final String GAMES_GAMERS_CSV = "game_gamer.csv";
    private static final String MOVES_CSV = "move.csv";

    public static void main(String[] args) throws Exception {
        BullsCowsCSVGenerator generator = new BullsCowsCSVGeneratorImpl();
        generator.generateGamerCsv(GAMERS_CSV);
        generator.generateGamesCsv(GAMES_CSV);
        generator.generateGamesGamersCsv(GAMES_GAMERS_CSV);
        generator.generateMovesCsv(MOVES_CSV);
    }
}