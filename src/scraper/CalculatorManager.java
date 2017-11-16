package scraper;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class CalculatorManager {

	public static void calculateScorePp(ArrayList<Score> scores) {
		/*
		 * This method runs the calculator for every score in the list,
		 * and adds the pp values for each category to each Score-object
		 */
		
	    for(Score score : scores){
			try {
		        Runtime rt = Runtime.getRuntime();
		        Process pr = rt.exec("\"oppai tweaks.exe\" " + getArguments(score));
		        
	            BufferedReader in = new BufferedReader(new InputStreamReader(pr.getInputStream()));
	            String line;
	            
	            while ((line = in.readLine()) != null) {
	            	
	            	///////////////////////////////////////////////////////////////	
	            	// i want this to be more flexible later, but i guess it works
	            	// should also change Score-object
	            	///////////////////////////////////////////////////////////////

	            	if(line.startsWith("aim: ")){
	            		score.aimValue = Float.parseFloat(line.substring(5));
		            	score.speedValue = Float.parseFloat(in.readLine().substring(7));
		            	score.accValue = Float.parseFloat(in.readLine().substring(10));
	            	}
	            	// else don't spend as much time looking
	            }

	            pr.waitFor(); // discards program exit value
	            score.beatmap_fullname = findBeatmapName(score.beatmap_id, getEnabledMods(score.enabled_mods));
	            
	            System.out.println("Calculated score for map: " + score.beatmap_fullname);
			}
			catch (IOException e) {
				System.err.println(e.getMessage());
			}
			catch (InterruptedException ie) {
				// e.printStackTrace();
			}
			
			/*
			System.out.println("aim: " + score.aimValue);
			System.out.println("speed: " + score.speedValue);
			System.out.println("acc: " + score.accValue);
			*/
		}
	}
	
	private static String getArguments(Score score) {
		return ScraperConfiguration.MAP_FOLDER_NAME + "/" + score.beatmap_id + ".osu " +
				score.count100 + "x100 " +
				score.count50 + "x50 " +
				score.countMiss + "m " +
				score.maxCombo + "x " +
				getEnabledMods(score.enabled_mods);
	}

	public static String getEnabledMods(int mods) {
		if (mods == 0 || mods == 4)	// if nomod (or novideo)
			return "";
		
		StringBuilder sb = new StringBuilder("+");
		
		if ((mods & 1) > 0)
			sb.append("NF");
		if ((mods & 2) > 0)
			sb.append("EZ");
		if ((mods & 8) > 0)
			sb.append("HD");
		if ((mods & 16) > 0)
			sb.append("HR");
		if ((mods & 64) > 0 || (mods & 512) > 0)
			sb.append("DT");
		if ((mods & 256) > 0)
			sb.append("HT");
		if ((mods & 1024) > 0)
			sb.append("FL");
		if ((mods & 4096) > 0)
			sb.append("SO");
		
		return sb.toString();
	}
	
	public static String findBeatmapName(String beatmap_id, String mods) throws FileNotFoundException, IOException {
		/*
		 * Gets beatmap artist, song, diffname, mapper and mods
		 */
		
		File osuFile = new File(ScraperConfiguration.MAP_FOLDER_NAME + "/" + beatmap_id + ".osu");
		BufferedReader in = new BufferedReader(new FileReader(osuFile));
        String line = null;
        String artist = "", songTitle = "", mapper = "", diff = "";
        
        while ((line = in.readLine()) != null) {
        	if(line.startsWith("Title:") && songTitle.isEmpty()){
        		songTitle = line.substring(6);
        	}
        	if(line.startsWith("Artist:") && artist.isEmpty()){
        		artist = line.substring(7);
        	}
        	if(line.startsWith("Creator:") && mapper.isEmpty()){
        		mapper = line.substring(8);
        	}
        	if(line.startsWith("Version:") && diff.isEmpty()){
        		diff = line.substring(8);
        	}
        	if(line.startsWith("[Difficulty]"))
        		break;
        }
        
        in.close();
		return artist + " - " + songTitle + " [" + diff + "] (" + mapper + ") " + mods;
	}
}
