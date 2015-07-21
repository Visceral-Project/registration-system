package eu.visceral.registration.managedbeans;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import eu.visceral.registration.ejb.entity.Admin;
import eu.visceral.registration.ejb.entity.User;

@ManagedBean(name = "sendEmailBean")
@SessionScoped()
public class SendEmail {

    private String mailSubject;
    private String mailContent;
    private String userEmail = "";
    private String forgotId = "";
    private ArrayList<String> toEmails;

    public boolean sendForgot(String forgotID, String email) {
        this.forgotId = forgotID;
        this.toEmails = new ArrayList<String>();
        this.toEmails.add(email);
        this.mailSubject = "Visceral - Forgot Password Service";
        this.mailContent = "<h3>Visceral Registration System</h3><div><p>You have requested to reset your password. In order to do this please click <a href=\"http://visceral.eu:8080/register/ResetPassword.xhtml?fid="
                + this.forgotId + "\">here</a>.</p></div><div><p>Please disregard this message if you haven't requested for it.</p></div>";
        return send();
    }

    public boolean sendPDFAgreement(String email, List<Admin> admins) throws UnsupportedEncodingException {
        // if (admins != null && !admins.isEmpty()) {
        this.toEmails = new ArrayList<String>();
        // for (Admin admin : admins) {
        // if (admin.getGetConfEmails()) {
        // this.toEmails.add(admin.getEmail());
        // }
        // }

        this.toEmails.add("hanbury@ifs.tuwien.ac.at");
        this.toEmails.add("Oscar.Jimenez@hevs.ch");
        this.toEmails.add("Henning.Mueller@hevs.ch");
        
        this.userEmail = email;
        this.mailSubject = "Visceral - Agreement Upload Confirmation";
        this.mailContent = "<h3>Visceral Registration System</h3><div><p>A participant uploaded the signed agreement. In order to download the PDF click <a href=\"http://visceral.eu:8080/register/Download.xhtml?pdf="
                + URLEncoder.encode(this.userEmail, "UTF-8") + ".pdf\">here</a>.</p></div>"
                + "<div><p>If the signed PDF is in order you can activate the participant's account from the administrator's <a href=\"http://visceral.eu:8080/register/Login.xhtml\">dashboard</a></p></div>";
        return send();
        // } else {
        // return false;
        // }
    }

    public boolean sendRegistrationConf(String userMail) {
        this.toEmails = new ArrayList<String>();
        this.toEmails.add(userMail);
        this.mailSubject = "Visceral - Registration Confirmation";
        this.mailContent = "<h3>Visceral Registration System</h3><div><p>Your registation was successfull but your account is still inactive. In order to activate your account you have to login and follow the instructions. To do this please click <a href=\"http://visceral.eu:8080/register/Login.xhtml\">here</a>.</p></div>";
        return send();
    }

    public boolean sendActivationConf(User user) {
        this.toEmails = new ArrayList<String>();
        this.toEmails.add(user.getEmail());
        this.mailSubject = "Visceral - Activation Confirmation";
        this.mailContent = "<h3>Visceral Registration System</h3><div><p>Your account has been activated. If you want to login please click <a href=\"http://visceral.eu:8080/register/Login.xhtml\">here</a>.</p></div>";

        // When activation mail is sent inform the HESSO admins to assing a VM
        if (send()) {
            this.toEmails = new ArrayList<String>();
            this.toEmails.add("ivan.eggel@hevs.ch");
            this.toEmails.add("Roger.Schaer@hevs.ch");
            this.toEmails.add("Oscar.Jimenez@hevs.ch");
            this.mailSubject = "Visceral - VM assignment";
            this.mailContent = "<h3>Visceral Registration System</h3><div><p>The account with email <b>" + user.getEmail() + "</b> has been activated. Now you will need to assign a VM (<b>" + user.getPreferance()
                    + "</b>). You can do this from the administrator's <a href=\"http://visceral.eu:8080/register/Login.xhtml\">dashboard</a>.</p></div>";
        }
        return send();
    }

    public boolean sendVMAssignedConf(String userMail) {
        this.toEmails = new ArrayList<String>();
        this.toEmails.add(userMail);
        this.mailSubject = "Visceral - A VM has been assigned to you";
        this.mailContent = "<h3>Visceral Registration System</h3><div><p>A virtual machine has been assigned to your account. In order to connect to it you will need details found on the Visceral <a href=\"http://visceral.eu:8080/register/Login.xhtml\">dashboard</a>.</p></div>";
        return send();
    }

    public boolean sendVmSubmissionConf(User user, String vmDnsName) {
        this.toEmails = new ArrayList<String>();
        this.toEmails.add(user.getEmail());
        this.mailSubject = "Visceral - Virtual Machine Submission Confirmation";
        this.mailContent = "<h3>Visceral Registration System</h3><div><p>Your Virtual Machine has been submitted successfully. Make sure you have saved your work on the machine because your credentials are going to be withdrawn</p><br/><a href=\"http://visceral.eu:8080/register/Login.xhtml\">Visceral Registration System</a></div>";

        // After VM submission mail is sent inform the HESSO admins to withdraw
        // the credentials
        if (send()) {
            this.toEmails = new ArrayList<String>();
            this.toEmails.add("ivan.eggel@hevs.ch");
            this.toEmails.add("Roger.Schaer@hevs.ch");
            this.toEmails.add("Oscar.Jimenez@hevs.ch");
            this.toEmails.add("hanbury@ifs.tuwien.ac.at");
            this.mailSubject = "Visceral - Virtual Machine Submission Confirmation";
            this.mailContent = "<h3>Visceral Registration System</h3><div><p>A participant submitted a Virtual Machine</p><p><strong>Participant information</strong></p><p>Unique ID: "
                    + user.getUniqueid().replaceAll("_submitted", "") + "</p><p>Name: " + user.getFirstname() + "</p><p>Surname: " + user.getLastname() + "</p><p>E-Mail: " + user.getEmail()
                    + "</p><p>Virtual Machine Domain Name: " + vmDnsName + "</p><br/><a href=\"http://visceral.eu:8080/register/Login.xhtml\">Visceral Registration System</a></div>";
        }
        return send();
    }

    public boolean sendVmReset(User user) {
        this.toEmails = new ArrayList<String>();
        this.toEmails.add(user.getEmail());
        this.mailSubject = "Visceral - Your virtual machine has been returned";
        this.mailContent = "<h3>Visceral Registration System</h3><div><p>Your Virtual Machine has been returned. This means that our automated tests failed to use your machine</p><br/><a href=\"http://visceral.eu:8080/register/Login.xhtml\">Visceral Registration System</a></div>";
        return send();
    }

    public boolean sendVmCloneRequest(User user, String vmDnsName) {
        this.toEmails = new ArrayList<String>();
        this.toEmails.add("ivan.eggel@hevs.ch");
        this.toEmails.add("Roger.Schaer@hevs.ch");
        this.toEmails.add("Oscar.Jimenez@hevs.ch");
        this.toEmails.add("hanbury@ifs.tuwien.ac.at");
        this.mailSubject = "Visceral - Virtual Machine Clone Request";
        this.mailContent = "<h3>Visceral Registration System</h3><div><p>A participant requested to clone a Virtual Machine</p><p><strong>Participant information</strong></p><p>Unique ID: "
                + user.getUniqueid().replaceAll("_submitted", "") + "</p><p>Name: " + user.getFirstname() + "</p><p>Surname: " + user.getLastname() + "</p><p>E-Mail: " + user.getEmail()
                + "</p><p>Virtual Machine Domain Name: " + vmDnsName + "</p><br/><a href=\"http://visceral.eu:8080/register/Login.xhtml\">Visceral Registration System</a></div>";
        return send();
    }

    public boolean sendNewVmRequest(User user) {
        System.out.println(user.getPreferance());
        this.toEmails = new ArrayList<String>();
        this.toEmails.add("ivan.eggel@hevs.ch");
        this.toEmails.add("Roger.Schaer@hevs.ch");
        this.toEmails.add("Oscar.Jimenez@hevs.ch");

        this.mailSubject = "Visceral - VM assignment";
        this.mailContent = "<h3>Visceral Registration System</h3><div><p>The account with email <b>" + user.getEmail() + "</b> has registered in a new benchmark. Now you will need to assign a VM (<b>"
                + user.getPreferance() + "</b>). You can do this from the administrator's <a href=\"http://visceral.eu:8080/register/Login.xhtml\">dashboard</a>.</p></div>";
        return send();
    }

    private boolean send() {
        String from = "registration@visceral.eu";
        String host = "localhost";

        // Get the session object
        Properties properties = System.getProperties();
        properties.setProperty("mail.smtp.host", host);
        Session session = Session.getDefaultInstance(properties);

        // compose the message
        try {
            MimeMessage message = new MimeMessage(session);

            message.setFrom(new InternetAddress(from));

            for (String toEmail : this.toEmails) {
                message.addRecipient(Message.RecipientType.TO, new InternetAddress(toEmail));
            }

            message.setSubject(this.mailSubject);

            // Unnecessary for now because PDFs can be served if needed by
            // specific URLs
            // BodyPart messageBodyPart = new MimeBodyPart();
            //
            // messageBodyPart.setText("Hello, this is example of sending email  ");
            //
            // Multipart multipart = new MimeMultipart();
            // multipart.addBodyPart(messageBodyPart);
            //
            // messageBodyPart = new MimeBodyPart();
            // File f = new File(System.getProperty("user.home") +
            // "/signedAgreements/" + email + ".pdf");
            // DataSource source = new FileDataSource(f);
            // messageBodyPart.setDataHandler(new DataHandler(source));
            // messageBodyPart.setFileName(email + ".pdf");
            // messageBodyPart.setHeader("Content-Type",
            // "application/octet-stream");
            // multipart.addBodyPart(messageBodyPart);
            //
            // message.setContent(multipart);

            message.setContent(this.mailContent, "text/html");

            // Send message
            Transport.send(message);
            return true;

        } catch (MessagingException mex) {
            mex.printStackTrace();
            return false;
        }
    }
}
