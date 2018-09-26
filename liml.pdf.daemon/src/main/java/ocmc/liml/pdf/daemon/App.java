package ocmc.liml.pdf.daemon;

import java.io.File;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Runs a daemon to 
 *
 */
public class App {
    public static void main( String[] args ) {
    	try {
    		String dirToWatch = System.getenv("SRV_FILE");
    		if (dirToWatch == null || dirToWatch.length() == 0 && args.length > 0) {
        		dirToWatch = args[0];
    		}
    		Path path = Paths.get(dirToWatch);
    		System.out.println( "Hello World! I have my eye on " + dirToWatch );
            WatchService watcher = FileSystems.getDefault().newWatchService();
            WatchKey key = path.register(watcher, StandardWatchEventKinds.ENTRY_CREATE);
            while ((key = watcher.take()) != null) {
                for (WatchEvent<?> event : key.pollEvents()) {
                    System.out.println(
                            "Event kind:" + event.kind() 
                              + ". File affected: " + event.context() + ".");
                    String filename = event.context().toString();
                    if (filename.endsWith(".tex")) {
        				// create a thread that will generate a PDF
        				ExecutorService executorService = Executors.newSingleThreadExecutor();
        				executorService.execute(
        						new PdfGenerationTask(
        								dirToWatch
        								, filename 
        								)
        						);
        				executorService.shutdown();
                    }
                }
                key.reset();
            }
       } catch (Exception e) {
    		e.printStackTrace();
    	}
    }
}
