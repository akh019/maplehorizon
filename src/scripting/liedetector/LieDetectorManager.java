package scripting.liedetector;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import tools.Pair;
import tools.Randomizer;
import constants.ServerConstants;

public class LieDetectorManager {
	
	private static final String CAPTCHA_DIRECTORY = ServerConstants.Resources.RESOURCE_PATH + "liedetector";
	
	private static List<Pair<String, byte[]>> captchas = new ArrayList<>();
	
	public static Pair<String, byte[]> getRandomCaptcha() {
		if (!captchas.isEmpty())
			return captchas.get(Randomizer.nextInt(captchas.size()));
		else return null;
	}
	
	public static void loadAllImages() {
		if (captchas.isEmpty()) {
			File directory = new File(CAPTCHA_DIRECTORY);
			for (String filename : directory.list()) {
				filename = filename.substring(0, filename.length() - 4); // Removes .jpg
				try {
					captchas.add(new Pair<>(filename, getBytesFromFile(new File(CAPTCHA_DIRECTORY + "/" + filename + ".jpg"))));
				} catch (IOException e) {
					System.err.println("Failed loading Lie Detector Image " + filename + " " + e);
				}
			}
		}
	}
	
	public static void reloadImages() {
		captchas.clear();
		loadAllImages();
	}
	
	public static byte[] getBytesFromFile(final File file) throws IOException { 
        byte[] bytes = null; 
        try (InputStream is = new FileInputStream(file)) { 
            long length = file.length(); 
            if (length > Integer.MAX_VALUE) { 
                return null; 
            } 
            bytes = new byte[(int) length]; 
            int offset = 0; 
            int numRead = 0; 
            while (offset < bytes.length && (numRead = is.read(bytes, offset, bytes.length - offset)) >= 0) { 
                offset += numRead; 
            } 
            if (offset < bytes.length) { 
                System.err.println("Could not read Lie Detector Image " + file.getName()); 
                return null; 
            } 
        } 
        return bytes; 
    }

}
