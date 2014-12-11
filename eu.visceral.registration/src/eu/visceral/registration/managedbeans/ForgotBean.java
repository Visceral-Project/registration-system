package eu.visceral.registration.managedbeans;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;

import org.apache.commons.lang.RandomStringUtils;

import eu.visceral.registration.ejb.eao.VisceralEAO;

/**
 * @author Georgios Kotsios-Kontokotsios
 * 
 */
@ManagedBean(name = "forgotBean")
@SessionScoped()
public class ForgotBean {
    @EJB
    VisceralEAO service;
    private String email;
    private String forgotID;

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

    /**
     * @return the forgotID
     */
    public String getForgotID() {
        return forgotID;
    }

    public String sendForgotEmail() {
        this.forgotID = RandomStringUtils.randomAlphanumeric(32);
        service.updateForgotPasswordField(this);

        SendEmail mail = new SendEmail();
        if (mail.sendForgot(this.forgotID, this.email)) {
            return "forgot_mail_sent";
        } else {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "This email is not associated with a user.", null));
        }
        return "forgot_mail_failed";
    }
}
