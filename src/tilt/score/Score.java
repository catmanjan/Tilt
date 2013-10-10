package tilt.score;

public class Score {
	
	public String level;
	public String name;
	public long score;
	
	public Score(String level, String name, long score) {
		this.level = level;
		this.name = name;
		this.score = score;
	}
	
	public Score(String data) {
		String[] split = data.split(";");
		level = split[0];
		name = split[1];
		score = Long.parseLong(split[2]);
	}
	
	@Override
	public String toString() {
		// Should put a stringbuilder here...
		// Should sanitize input here...
		return "level=" + level + "&name=" + name + "&score=" + score;
	}

}
