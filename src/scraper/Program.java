package scraper;

import java.io.File;
import java.util.ArrayList;

public class Program {

	public static ArrayList<Score> scores;
	private static String user_id = "";	// ask user for this maybe
	
	public static void main(String[] args) {

		// check if diffcalc exists
		File oppai = new File("oppai.exe");
		
		if(!oppai.exists()) {
			System.out.println("could not find oppai.exe in program folder. add one to continue");
			System.exit(0);
		}
		
		scores = new ArrayList<Score>();
		
		scores = OsuFileScraper.scrapeScores(user_id);
		if(scores == null){
			System.out.println("An unexpected error occurred while trying to fetch performance data.");
			return;
		}

		CalculatorManager.calculateScorePp(scores);
		ResultsManager.writeStatistics(user_id, scores);
		
		// what now
	}
	
}
