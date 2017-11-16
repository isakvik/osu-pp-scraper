package scraper;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

public class ResultsManager {

	private static final int ppTotalsSpacing = 15;
	private static final int bestScoreListLength = 5;
	private static final double performanceDropoffValue = 0.95;
	
	private static PrintWriter out;

	public static void writeStatistics(String user_id, ArrayList<Score> scores) {
		
		ArrayList<Score> bestAimScores = (ArrayList<Score>) scores.clone();
		bestAimScores.sort((s1, s2) -> Float.compare(s2.aimValue, s1.aimValue));
		ArrayList<Score> bestSpeedScores = (ArrayList<Score>) scores.clone();
		bestSpeedScores.sort((s1, s2) -> Float.compare(s2.speedValue, s1.speedValue));
		ArrayList<Score> bestAccScores = (ArrayList<Score>) scores.clone();
		bestAccScores.sort((s1, s2) -> Float.compare(s2.accValue, s1.accValue));
		
		double ppTotal = calculatePerformance(scores, "total");
		double ppAim = calculatePerformance(scores, "aim");
		double ppSpeed = calculatePerformance(scores, "speed");
		double ppAcc = calculatePerformance(scores, "acc");
		double ppMagic = ppTotal - (ppAim + ppSpeed + ppAcc);
		
		try {
			// create file with name of user
			File sttFile = new File(user_id + ".txt");
			out = new PrintWriter(sttFile);
			System.out.println("Created file: " + user_id + ".txt");
			
			out.println("Statistics for user \"" + user_id + "\":");
			writePpLine(out, "Total pp", ppTotal, 0);
			writePpLine(out, "Aim pp", ppAim, (ppAim / ppTotal) * 100);
			writePpLine(out, "Speed pp", ppSpeed, (ppSpeed / ppTotal) * 100);
			writePpLine(out, "Acc pp", ppAcc, (ppAcc / ppTotal) * 100);
			writePpLine(out, "Magic(?) pp", ppMagic, (ppMagic / ppTotal) * 100);
			
			out.print("\n\n");

			// write all score tables

			out.println("Top aim scores:");
			writeTypeTable(out, bestAimScores, "aim", getLongestName(bestAimScores));

			out.println("Top speed scores:");
			writeTypeTable(out, bestSpeedScores, "speed", getLongestName(bestSpeedScores));

			out.println("Top accuracy scores:");
			writeTypeTable(out, bestAccScores, "acc", getLongestName(bestAccScores));

			// already sorted by highest total pp worth from the osu API
			out.println("\nAll performances:");
			writeAllTable(out, scores, getLongestName(scores));
		}
		catch (IOException ioe) {
			ioe.printStackTrace();
		}
		
		System.out.println("Finishing writing to file.");
		out.close();
	}
	
	private static double calculatePerformance(ArrayList<Score> scores, String type) {
		
		double result = 0;
		
		// -_-
		
		if(type.equals("total")) {
			for(int i = 0; i < scores.size(); i++){
				result += scores.get(i).ppValue * Math.pow(performanceDropoffValue, i);
			}
		}
		else if(type.equals("aim")) {
			for(int i = 0; i < scores.size(); i++){
				result += scores.get(i).aimValue * Math.pow(performanceDropoffValue, i);
			}
		}
		else if(type.equals("speed")) {
			for(int i = 0; i < scores.size(); i++){
				result += scores.get(i).speedValue * Math.pow(performanceDropoffValue, i);
			}
		}
		else if(type.equals("acc")) {
			for(int i = 0; i < scores.size(); i++){
				result += scores.get(i).accValue * Math.pow(performanceDropoffValue, i	);
			}
		}
		
		return result;
	}

	private static void writePpLine(PrintWriter out, String desc, double pp, double percent) {
		out.printf("%-" + ppTotalsSpacing + "s", desc + ":");
		out.printf("%9.2f", pp);
		if(percent != 0){	// skip percent for total
			out.printf(" (%.2f%%)", percent);
		}
		out.print("\n");
	}
	
	private static void writeTypeTable(PrintWriter out, ArrayList<Score> scores, String type, int mapNameSpacing) {
		/*
		 * note: scores assumed a sorted list, it's not done here
		 */
		int index = 0;
				
		out.print("     ");
		out.printf("%-" + mapNameSpacing + "s", "MAP NAME");
		out.printf("%-12s", type.toUpperCase() + " PP");
		// out.printf("%-" + 6 + "s", type.toUpperCase() + " DIFF");
		out.print("\n");
		
		// -_-
		
		if(type.equals("aim")) {
			while(index++ < bestScoreListLength) {
				out.printf("%-5s", "#" + index);
				out.printf("%-" + mapNameSpacing + "s", scores.get(index - 1).beatmap_fullname);
				out.printf("%-12.2f", scores.get(index - 1).aimValue);
				out.print("\n");
			}
		}
		
		else if(type.equals("speed")) {
			while(index++ < bestScoreListLength) {
				out.printf("%-5s", "#" + index);
				out.printf("%-" + mapNameSpacing + "s", scores.get(index - 1).beatmap_fullname);
				out.printf("%-12.2f", scores.get(index - 1).speedValue);
				out.print("\n");
			}
		}
		
		else if(type.equals("acc")) {
			while(index++ < bestScoreListLength) {
				out.printf("%-5s", "#" + index);
				out.printf("%-" + mapNameSpacing + "s", scores.get(index - 1).beatmap_fullname);
				out.printf("%-12.2f", scores.get(index - 1).accValue);
				out.print("\n");
			}
		}

		out.print("\n");
	}
	
	private static void writeAllTable(PrintWriter out, ArrayList<Score> scores, int mapNameSpacing) {
		
		out.print("     ");
		out.printf("%-" + mapNameSpacing + "s", "MAP NAME");
		out.printf("%-12s", "TOTAL PP");
		out.printf("%-12s", "AIM PP");
		out.printf("%-12s", "SPEED PP");
		out.printf("%-12s", "ACC PP");
		out.printf("%-20s", "WEIGHTING");
		out.print("\n");
		
		int index = 0;		
		for(Score score : scores) {
			out.printf("%-5s", "#" + ++index);
			out.printf("%-" + mapNameSpacing + "s", score.beatmap_fullname);
			out.printf("%-12.2f", score.ppValue);
			out.printf("%-12.2f", score.aimValue);
			out.printf("%-12.2f", score.speedValue);
			out.printf("%-12.2f", score.accValue);
			
			double weightingPercent = Math.pow(performanceDropoffValue, index - 1);

			out.printf("%.2f%%", weightingPercent * 100);
			out.printf(" (%.2fpp)", score.ppValue * weightingPercent);
			out.print("\n");
		}
	}
	
	private static int getLongestName(ArrayList<Score> scores) {
		int result = 0;
		for (Score score : scores) {
			int length = score.beatmap_fullname.length();
			if(result < length)
				result = length;
		}
		
		return result + 4;
	}
}
