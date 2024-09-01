package telran.bullscows;

import java.io.*;
import java.time.*;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.*;

public class BullsCowsCSVGeneratorImpl implements BullsCowsCSVGenerator {

	private static final int NUM_GAMES = 50;
	private static final int FINISHED_GAMES = 40;
	private static final int NUM_GAMERS = 10;
	private static final int MAX_MOVES = 10;
	private static final Random random = new Random();

	private final int numGames;
	private final int finishedGames;
	private final int numGamers;
	private final int maxMoves;

	private final TreeMap<Long, Game> games = new TreeMap<>();
	private final TreeMap<String, LocalDate> gamers = new TreeMap<>();
	private final TreeMap<Long, GameGamer> gamesGamers = new TreeMap<>();
	private final TreeMap<Long, Move> moves = new TreeMap<>();

	public BullsCowsCSVGeneratorImpl() {
		this(NUM_GAMES, FINISHED_GAMES, NUM_GAMERS, MAX_MOVES);
	}

	public BullsCowsCSVGeneratorImpl(int numGames, int finishedGames, int numGamers, int maxMoves) {
		this.numGames = numGames;
		this.finishedGames = finishedGames;
		this.numGamers = numGamers;
		this.maxMoves = maxMoves;
	}

	@Override
	public void generateGamerCsv(String gamersCsv) {
		System.out.println("Generating Gamers CSV...");

		IntStream.rangeClosed(1, numGamers).forEach(i -> {
			LocalDate birthdate = LocalDate.now().minusYears(18 + random.nextInt(30));
			gamers.put(generateUsername(), birthdate);
		});
		printToFile(gamersCsv, gamers);
		System.out.println("Gamers CSV generated successfully.");
	}

	@Override
	public void generateGamesCsv(String gamesCsv) {
		System.out.println("Generating Games CSV...");
		Set<Integer> finishedGameIndices = getRandomIndicies();
		IntStream.rangeClosed(1, numGames).forEach(gameId -> {
			LocalDateTime gameDate = LocalDateTime.now().minusDays(random.nextInt(100));
			String gameSequence = generateRandomSequence();
			boolean isGameFinished = finishedGameIndices.contains(gameId);
			games.put(Long.valueOf(gameId), new Game(Long.valueOf(gameId), gameDate, gameSequence, isGameFinished));
		});
		printToFile(gamesCsv, games);
		System.out.println("Games CSV generated successfully.");
	}

	private Set<Integer> getRandomIndicies() {
		Set<Integer> finishedGameIndices = new HashSet<>();
		while (finishedGameIndices.size() < finishedGames) {
			finishedGameIndices.add(random.nextInt(numGames) + 1);
		}
		return finishedGameIndices;
	}

	@Override
	public void generateGamesGamersCsv(String gamesGamersCsv) {
		System.out.println("Generating GamesGamers CSV...");
		Long gameGamerId = 100L;
		games.entrySet().forEach(game -> {
			String[] inGameGamers = getRandomGamers();
			int numOfGamers = inGameGamers.length;
			Long[] gameGamersIds = new Long[numOfGamers];
			addGameGamers(gameGamerId, game, inGameGamers, numOfGamers, gameGamersIds);
			addGameMoves(gameGamersIds, game.getKey());
		});
		printToFile(gamesGamersCsv, gamesGamers);
		System.out.println("GamesGamers CSV generated successfully.");
	}

	private void addGameGamers(Long gameGamerId, Entry<Long, Game> game, String[] inGameGamers, 
			int numOfGamers, Long[] gamesGamersIds) {
		long[] currentGameGamerId = { gameGamerId };

		IntStream.range(0, numOfGamers).forEach(i -> {
			gamesGamers.put(currentGameGamerId[0],
					new GameGamer(currentGameGamerId[0], game.getKey(), inGameGamers[i], false));
			gamesGamersIds[i] = currentGameGamerId[0];
			currentGameGamerId[0]++; // Increment the ID
		});
	}

	@Override
	public void generateMovesCsv(String movesCsv) {
		System.out.println("Generating Moves CSV...");
		printToFile(movesCsv, moves);
		System.out.println("Moves CSV generated successfully.");
	}

	private void addGameMoves(Long[] gamesGamersIds, Long gameId) {
	    int numGameMoves = 1 + random.nextInt(maxMoves);
	    AtomicLong moveId = new AtomicLong(moves.isEmpty() ? 1000 : moves.lastKey() + 1);
	    Game game = games.get(gameId);
	    String secretSequence = game.sequence();

	    IntStream.range(0, numGameMoves).forEach(moveNum -> {
	        Long gameGamerId = gamesGamersIds[moveNum % gamesGamersIds.length];
	        boolean isLastMoveAndFinished = (moveNum == numGameMoves - 1) && game.isFinished();

	        String sequence = isLastMoveAndFinished ? secretSequence : generateGamerSequence(secretSequence, gameGamerId);
	        int[] bullsCows = isLastMoveAndFinished ? new int[]{4, 0} : calculateBullsAndCows(sequence, secretSequence);

	        moves.put(moveId.get(), new Move(moveId.get(), sequence, bullsCows[0], bullsCows[1], gameGamerId));

	        if (isLastMoveAndFinished) {
	            updateGameGamerAsWinner(gameGamerId, gameId);
	        }

	        moveId.incrementAndGet();
	    });
	}

	private void updateGameGamerAsWinner(Long gameGamerId, Long gameId) {
		GameGamer gameGamer = gamesGamers.get(gameGamerId);
		gamesGamers.put(gameGamerId, new GameGamer(gameGamerId, gameId, gameGamer.gamerId(), true));
	}

	private String generateGamerSequence(String secretSequence, Long gameGamerId) {
		String sequence;
		do {
			sequence = generateRandomSequence();
		} while (sequenceExists(sequence, gameGamerId) && sequence.equalsIgnoreCase(secretSequence));

		return sequence;
	}

	private boolean sequenceExists(String sequence, Long gameGamerId) {
		return moves.values().stream()
				.anyMatch(move -> move.gameGamerId().equals(gameGamerId) && move.sequence().equals(sequence));
	}

	private int[] calculateBullsAndCows(String guess, String secret) {
	    int[] bullsCows = new int[2]; // bullsCows[0] for bulls, bullsCows[1] for cows

	    IntStream.range(0, guess.length()).forEach(i -> {
	        char currentChar = guess.charAt(i);
	        if (currentChar == secret.charAt(i)) {
	            bullsCows[0]++; // increment bulls
	        } else if (secret.indexOf(currentChar) >= 0) {
	            bullsCows[1]++; // increment cows
	        }
	    });

	    return bullsCows;
	}

	private String generateRandomSequence() {
		return random.ints(0, 10).distinct().limit(4).mapToObj(String::valueOf).collect(Collectors.joining());
	}

	private String[] getRandomGamers() {
		List<String> usernames = new ArrayList<>(gamers.keySet());
		Collections.shuffle(usernames);
		int numSelected = 1 + random.nextInt(usernames.size());
		return usernames.subList(0, numSelected).toArray(String[]::new);
	}

	private String generateUsername() {
		int gamerId = random.nextInt(1000);
		String username = "gamer" + gamerId;
		while (gamers.containsKey(username)) {
			gamerId = random.nextInt(1000);
			username = "gamer" + gamerId;
		}
		return username;
	}

	private void printToFile(String fileName, Map<?, ?> map) {
		try (PrintWriter writer = new PrintWriter(new FileOutputStream(fileName))) {
			map.values().forEach(writer::println);
		} catch (Exception e) {
			System.err.println("Error while writing to " + fileName + ": " + e.getMessage());
		}
	}
}