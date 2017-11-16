package scraper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.util.ArrayList;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import static scraper.ScraperConfiguration.API_KEY;
import static scraper.ScraperConfiguration.MAP_FOLDER_NAME;

public class OsuFileScraper {

	private static final int list_length = 100;

	private static ArrayList<Score> scores;
	
	public static ArrayList<Score> scrapeScores(String user_id) {

		scores = new ArrayList<Score>();
		
		try {
			// a-at least it's readable right
		    createFolder();
			populateScoreArray(user_id);
			downloadMaps();
		}
		catch (IOException ioe) {
			ioe.printStackTrace();
			return null;
		}
		
		return scores;
	}
	
	private static void createFolder() {
        File folder = new File(MAP_FOLDER_NAME);
        
        if (!folder.exists()) {
            if (folder.mkdir()) {
                System.out.println("Created directory: " + MAP_FOLDER_NAME);
            }
            else {
                System.out.println("Failed to create directory: " + MAP_FOLDER_NAME);
            }
        }
	}
	
	private static void populateScoreArray(String user_id) throws IOException {

		URL apiUrl = new URL("https://osu.ppy.sh/api/get_user_best?"
			+ "k=" + API_KEY
			+ "&u=" + user_id
			+ "&limit=" + list_length
			+ "&type=string");
	
		HttpURLConnection request = (HttpURLConnection) apiUrl.openConnection();
		
		System.out.println("Accessing URL: " + apiUrl);
    	
	    // Convert to a JSON object to print data
	    JsonParser jp = new JsonParser(); //from gson
	    JsonElement root = jp.parse(new InputStreamReader((InputStream) request.getContent()));
	    JsonArray scoreArray = root.getAsJsonArray();
	    
	    // populate object arrays
	    for(JsonElement obj : scoreArray){
	    	JsonObject score = obj.getAsJsonObject();
	    	
	    	scores.add(new Score(
	    			score.get("beatmap_id").getAsString(),
	    			score.get("maxcombo").getAsInt(),
	    			score.get("count100").getAsInt(),
	    			score.get("count50").getAsInt(),
	    			score.get("countmiss").getAsInt(),
	    			score.get("enabled_mods").getAsInt(),
	    			score.get("pp").getAsFloat()
	    		));
	    }
	    
	    
		System.out.println("Populated score array.");
	}
	
	private static void downloadMaps() throws IOException {

		// create new file for every map in performance list
		for(Score score : scores){
			File osuFile = new File(MAP_FOLDER_NAME + "/" + score.beatmap_id + ".osu");
			
	        if (!osuFile.exists()) {
				
				URL osuFileUrl = new URL("https://osu.ppy.sh/osu/" + score.beatmap_id);
				HttpURLConnection request = (HttpURLConnection) osuFileUrl.openConnection();
				
				// direct inputstream to fileoutputstream
				InputStream in = (InputStream) request.getContent();
				FileOutputStream out = new FileOutputStream(osuFile);
				
				byte[] buffer = new byte[1024 * 100]; // 100KB buffer
				int len;
				while ((len = in.read(buffer)) != -1) {
				    out.write(buffer, 0, len);
				}
				
				out.close();
				System.out.println("Created file: " + osuFile.getName());
	        }
	        else {
	        	System.out.println("Skipped file: " + osuFile.getName());
	        }
		}
	}
}
