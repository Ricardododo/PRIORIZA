import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

public class TestSMTP {
    public static void main(String[] args) {
        String host = "smtp.gmail.com";
        String port = "587";
        String user = "ricardodevelop85@gmail.com";
        String password = "vwye uota itzm isjp";

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", host);
        props.put("mail.smtp.port", port);

        Session session = Session.getInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(user, password);
            }
        });
        session.setDebug(true);

        try {
            Message msg = new MimeMessage(session);
            msg.setFrom(new InternetAddress(user));
            msg.setRecipients(Message.RecipientType.TO,
                    InternetAddress.parse("andres.diazbareno@gmail.com"));
            msg.setSubject("Prueba SMTP");
            msg.setText("Si recibes esto, la conexión funciona");

            Transport.send(msg);
            System.out.println("ÉXITO: Email enviado");
        } catch (MessagingException e) {
            System.out.println("ERROR: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
