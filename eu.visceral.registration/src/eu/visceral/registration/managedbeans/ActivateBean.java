package eu.visceral.registration.managedbeans;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;

import eu.visceral.registration.ejb.eao.VisceralEAO;
import eu.visceral.registration.ejb.entity.Competition;
import eu.visceral.registration.ejb.entity.User;
import eu.visceral.registration.util.MailManHandler;

/**
 * @author Georgios Kotsios-Kontokotsios
 * 
 */
@ManagedBean(name = "activateBean")
@RequestScoped
public class ActivateBean {

    @EJB
    VisceralEAO service;

    @ManagedProperty(value = "#{param.email}")
    private String email;

    @ManagedProperty(value = "#{param.status}")
    private String status;

    /**
     * @return the status
     */
    public String getStatus() {
        return status;
    }

    /**
     * @param status
     *            the status to set
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * @return the email
     */
    public String getEmail() {
        return email;
    }

    /**
     * @param email
     *            the email to set
     */
    public void setEmail(String email) {
        this.email = email;
    }

    private User user;

    public void init() {
        String adminsEmail = (String) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("admin");
        // With the following line the logged in user is shown in the Dashboard
        // view.FacesContext.getCurrentInstance().addMessage(null, new
        // FacesMessage(FacesMessage.SEVERITY_ERROR, adminsEmail, null));
        if (adminsEmail == "") {
            try {
                FacesContext.getCurrentInstance().getExternalContext().redirect("/register/Login.xhtml");
            } catch (IOException e) {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, e);
            }
        } else {
            user = service.retrieveUser(this.email);
            if (user != null) {
                if (this.status.equalsIgnoreCase("activate")) {
                    user.setCopyrightSigned(true);
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "The account with registered email " + this.email + ", has been successfully activated.", null));
                    SendEmail mail = new SendEmail();
                    mail.sendActivationConf(user);

                    // Add activated participant on the Benchmarks' mailing
                    // list
                    for (Competition c : user.getCompetitions()) {
                        MailManHandler mailManHandler = new MailManHandler(c.getName());
                        mailManHandler.addLineToFile(this.email);
                        mailManHandler.syncMailManList();
                    }
                } else {
                    user.setCopyrightSigned(false);

                    // Remove deactivated participant from the Benchmarks'
                    // mailing list
                    for (Competition c : user.getCompetitions()) {
                        MailManHandler mailManHandler = new MailManHandler(c.getName());
                        mailManHandler.removeLineFromFile(this.email);
                        mailManHandler.syncMailManList();
                    }

                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "The account with registered email " + this.email + ", has been deactivated.", null));
                }
                service.updateUser(user);
            } else {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "There was a problem activating the account. Please try again later", null));
            }
        }
    }
}
