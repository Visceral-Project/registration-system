package eu.visceral.registration.managedbeans;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;

import eu.visceral.registration.ejb.eao.VisceralEAO;

/**
 * @author Georgios Kotsios-Kontokotsios
 * 
 */
@ManagedBean(name = "loginBean")
@SessionScoped()
public class LoginBean {

    @EJB
    VisceralEAO service;
    private String email;
    private String password;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    private void addMessage(FacesMessage message) {
        FacesContext.getCurrentInstance().addMessage(null, message);
    }

    public String logUserIn() {
        if (service.logUserIn(this).equalsIgnoreCase("user")) {
            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("admin", "");
            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("username", email);
            try {
                FacesContext.getCurrentInstance().getExternalContext().redirect("/register/Dashboard.xhtml");
            } catch (IOException e) {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, e);
            }
            return "logged_successfully";
        } else if (service.logUserIn(this).equalsIgnoreCase("admin")) {
            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("username", "");
            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("admin", email);
            try {
                FacesContext.getCurrentInstance().getExternalContext().redirect("/register/AdminDashboard.xhtml");
            } catch (IOException e) {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, e);
            }
            return "logged_successfully";
        } else {
            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("username", "");
            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("admin", "");
            addMessage(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Either your email or your password is wrong", null));
            return "loggin_failed";
        }
    }
}
