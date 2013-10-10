package tilt.score;

public class ScoreHandler {

	public static boolean enabled = true;
	public static boolean universityMode = false;

	private static ScoreGetter getter;
	private static ScoreSetter setter;

	public static void get(String levelName) {
		if (!enabled) {
			return;
		}

		// Does this stop the existing thread?
		getter = new ScoreGetter(levelName);
		getter.start();
	}

	public static void upload(String name, String level, long score) {
		upload(new Score(level, name, score));
	}

	/**
	 * Make this deal with nasty user input
	 * 
	 * @param score
	 */
	public static void upload(Score score) {
		if (!enabled) {
			return;
		}

		setter = new ScoreSetter(score);
		setter.start();
	}

}
