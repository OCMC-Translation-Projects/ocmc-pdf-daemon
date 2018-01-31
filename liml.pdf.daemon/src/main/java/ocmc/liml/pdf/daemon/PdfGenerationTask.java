package ocmc.liml.pdf.daemon;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Runs a task (separate thread) to generate the Xelatex content for
 * a tex file.  The tex file can then be used to create a PDF.
 * @author mac002
 *
 */
public class PdfGenerationTask implements Runnable {
	String pdfId = "";
	String directory = "";
	
	/**
	 * 
	 * @param template
	 * @param pathOut - path and filename to write the tex file
	 */
	public PdfGenerationTask (
			String directory
			, String pdfId
			) {
		this.directory = directory;
		this.pdfId = pdfId;
	}
	
	@Override
	public void run() {
		List<String> commands = new ArrayList<String>();
		String command = "cd " + directory;
		commands.add(command);
		String result = this.executeCommandProcessor(directory + "/makepdf", this.pdfId, directory);
		if (result != null && result.length() > 0) {
			System.out.println(result);
		}
	}
	
	private synchronized String executeCommandProcessor(String command, String file, String dir) {
		StringBuffer result = new StringBuffer();
		try {
				ProcessBuilder  ps = new ProcessBuilder(command, file);
				ps.directory(new File(dir));
				ps.redirectErrorStream(true);

				Process pr = ps.start();  

				BufferedReader in = new BufferedReader(new InputStreamReader(pr.getInputStream()));
				String line;
				while ((line = in.readLine()) != null) {
					result.append(line);
				}
				pr.waitFor();
				
				in.close();
				result.append("OK");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result.toString();
	}
	
}

