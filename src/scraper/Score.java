package scraper;

public class Score {
	/*
	 * value object for holding performance-relevant data
	 */

	public String beatmap_id;
	public int maxCombo;
	public int count100;
	public int count50;
	public int countMiss;
	public int enabled_mods;
	public float ppValue;

	// populated in CalculatorManager
	public String beatmap_fullname;		// artist - song [diffname] (mapper) +mods
	public float aimValue;
	public float speedValue;
	public float accValue;
	
	public Score(String id, int c, int c100, int c50, int cm, int mods, float pp) {
		this.beatmap_id = id;
		this.maxCombo = c;
		this.count100 = c100;
		this.count50 = c50;
		this.countMiss = cm;
		this.enabled_mods = mods;
		this.ppValue = pp;

		this.beatmap_fullname = "";
		this.aimValue = 0;
		this.speedValue = 0;
		this.accValue = 0;
	}
}
