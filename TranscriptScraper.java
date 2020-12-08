package chrismlong;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TranscriptScraper {

	// links to all transcripts: http://transcripts.foreverdreaming.org/

	private static HashMap<String, String[]> characterMap = getCharacterMap();

	public static void main(String[] args) throws IOException {
		long startTime = System.currentTimeMillis();
		String transcriptsFilePath = "C:\\Users\\cml3t\\Desktop\\Shows\\transcripts_theoffice.csv";
		List<String[]> result = processTranscripts(transcriptsFilePath);

		// write it to a new file
		FileWriter csvWriter = new FileWriter("C:\\Users\\cml3t\\Desktop\\Shows\\word_counts_theoffice.csv");
		csvWriter.append("Episode_ID,Show,Character,Season,Episode,Words" + "\n");
		for (String[] res : result) {
			String epId = res[0] + "_" + res[2] + "_" + res[3];
			csvWriter.append(epId + "," + res[0] + "," + res[1] + "," + res[2] + "," + res[3] + "," + res[4] + "\n");
		}

		csvWriter.flush();
		csvWriter.close();

		long duration = (System.currentTimeMillis() - startTime) / 1000;
		System.out.println("Total Duration: " + duration + " s");
	}

	private static HashMap<String, String[]> getCharacterMap() {
		HashMap<String, String[]> characterMap = new HashMap<String, String[]>();

		// How I Met Your Mother
		String[] himym = { "Barney", "Ted", "Marshall", "Lily", "Robin" };
		characterMap.put("How I Met Your Mother", himym);

		// The Office
		String[] office = { "Michael", "Jim", "Dwight", "Pam", "Oscar", "Stanley", "Kevin", "Meredith", "Creed", "Ryan",
				"Kelly", "Andy", "Phyllis" , "Angela" , "Toby" };
		characterMap.put("The Office", office);

		// Friends
		String[] friends = { "Ross", "Joey", "Monica", "Chandler", "Phoebe" };
		characterMap.put("Friends", friends);

		// return
		return characterMap;
	}

	public static List<String[]> processTranscripts(String filePath) {
		List<String[]> result = new ArrayList<String[]>();
		BufferedReader reader = null;
		int counter = 0;

		try {
			String line = "";
			reader = new BufferedReader(new FileReader(filePath));
			line = reader.readLine();

			while (line != null && !line.isEmpty()) {
				long startTime = System.currentTimeMillis();
				// process the episode
				String[] data = line.split(",");
				if (counter == 0) {
					line = reader.readLine();
					counter++;
					continue;
				}

				List<String[]> episodeArrays = processEpisode(data);
				result.addAll(episodeArrays);

				// go to next line
				line = reader.readLine();
				long duration = System.currentTimeMillis() - startTime;
				System.out.println("Processing episode " + counter + ": " + duration / 1000 + "s");
				counter++;

			}

			// catch or close
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			try {
				reader.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return result;
	}

	private static List<String[]> processEpisode(String[] data) throws Exception {
		List<String[]> ret = new ArrayList<String[]>();

		// get the input
		String show = data[0];
		String season = data[1];
		String episodeNum = data[2];
		String transcriptUrl = data[3];

		// get the characters to loop
		String[] charactersToLoop = characterMap.get(show.toString());

		HashMap<String, Integer> characterWordsMap = new HashMap<String, Integer>();
		characterWordsMap = getCharacterWordsMap(charactersToLoop, transcriptUrl);

		// loop and get the results
		for (String character : charactersToLoop) {
			String[] lineToAdd = new String[5];
			lineToAdd[0] = show;
			lineToAdd[1] = character;
			lineToAdd[2] = season;
			lineToAdd[3] = episodeNum;
			lineToAdd[4] = characterWordsMap.get(character).toString();
			
			ret.add(lineToAdd);
		}

		return ret;
	}

	private static HashMap<String, Integer> getCharacterWordsMap(String[] charactersToLoop, String transcriptUrl)
			throws Exception {
		HashMap<String, Integer> retMap = new HashMap<String, Integer>();
		boolean isLine = false;
		String charactersLine = "";

		URL url = new URL(transcriptUrl);
		BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
		String line;

		// add initial values
		// see if its a characters line
		for (String character : charactersToLoop) {
			retMap.put(character, 0);
		}

		// loop through the site
		while ((line = in.readLine()) != null) {
			isLine = false;
			if (line.startsWith("<p>") || line.startsWith("</p><p>")) {
				// clean up the line
				line = line.replaceAll("\\<.*?>", "");
				line = line.replaceAll("\\[.*\\]", "");
				line = line.replaceAll("\\(.*\\)", "");

				// see if its a characters line
				for (String character : charactersToLoop) {
					if (line.toUpperCase().startsWith(character.toUpperCase() + ":")
							&& line.length() > character.length() + 2) {
						isLine = true;
						charactersLine = character;
					}
				}

				// get the number of words and then add to the map
				if (isLine) {
					// get words to add
					String trimmedLine = line.substring(charactersLine.length() + 2);
					String[] words = trimmedLine.split("\\s+");
					int wordsToAdd = words.length;

					// add it to map
					int formerValue = retMap.get(charactersLine);
					retMap.replace(charactersLine, formerValue + wordsToAdd);
				}
			}
		}

		in.close();

		return retMap;
	}
}