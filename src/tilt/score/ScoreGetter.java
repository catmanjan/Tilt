package tilt.score;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;

import tilt.game.Game;

public class ScoreGetter extends Thread {

	String levelName;

	public ScoreGetter(String levelName) {
		this.levelName = levelName;
	}

	@Override
	public void run() {
		ArrayList<String> data = new ArrayList<String>();
		try {
			URL url = new URL(
					"http://www.twosquared.com.au/tilt/scores/?level="
							+ URLEncoder.encode(levelName, "utf-8"));

			if (ScoreHandler.universityMode) {
				// For ANU
				System.setProperty("http.proxyHost", "iccache.anu.edu.au");
				// For ANU
				System.setProperty("http.proxyPort", "80");
			}

			URLConnection con = url.openConnection();
			if (ScoreHandler.universityMode) {
				// For ANU
				con.setRequestProperty("Proxy-Authorization",
						"Basic dTUwMTM5MjU6dGlueWNoYW4xMA==");
			}
			con.setDoOutput(true);

			BufferedReader in = new BufferedReader(new InputStreamReader(
					con.getInputStream()));
			String line;
			while ((line = in.readLine()) != null) {
				data.add(line);
			}
			in.close();
		} catch (IOException e) {
			e.printStackTrace();
			Game.highscores = null;
		}

		Score[] scores = new Score[data.size()];
		for (int i = 0; i < scores.length; i++) {
			scores[i] = new Score(data.get(i));
		}

		Game.highscores = scores;
	}

}
