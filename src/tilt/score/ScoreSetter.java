package tilt.score;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;

public class ScoreSetter extends Thread {
	
	Score score;
	
	public ScoreSetter(Score score) {
		this.score = score;
	}
	
	@Override
	public void run() {
		try {
			URL url = new URL("http://www.twosquared.com.au/tilt/scores/");
			
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

			OutputStreamWriter osw = new OutputStreamWriter(con
					.getOutputStream());
			osw.write(score.toString());
			osw.flush();
			osw.close();

			BufferedReader in = new BufferedReader(new InputStreamReader(con
					.getInputStream()));
			String line;
			while ((line = in.readLine()) != null) {
				System.out.println(line);
			}
			in.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
