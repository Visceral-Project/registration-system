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

/**
 * @author Georgios Kotsios-Kontokotsios
 * 
 */
@ManagedBean(name = "resetPwdBean")
@RequestScoped
public class ResetPasswordBean {
    @EJB
    VisceralEAO service;

    @ManagedProperty(value = "#{param.fid}")
    private String fid;

    private String password;
    private String confirmpassword;

    public void init() {
        if (!service.isFidValid(fid)) {
            try {
                FacesContext.getCurrentInstance().getExternalContext().redirect("/register/Login.xhtml");
            } catch (IOException e) {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, e);
            }
        }
    }

    public String changeUserPassword() {
        if (service.resetPwd(this)) {
            return "passwordReseted";
        } else {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Password reset failed, please try again", null));
        }
        return "passwordResetFailed";
    }

    /**
     * @return the password
     */
    public String getPassword() {
        return password;
    }

    /**
     * @param password
     *            the password to set
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * @return the fid
     */
    public String getfid() {
        return fid;
    }

    /**
     * @param fid
     *            the forgotID to set
     */
    public void setfid(String fid) {
        this.fid = fid;
    }

    /**
     * @return the confirmpassword
     */
    public String getConfirmpassword() {
        return confirmpassword;
    }

    /**
     * @param confirmpassword
     *            the confirmpassword to set
     */
    public void setConfirmpassword(String confirmpassword) {
        this.confirmpassword = confirmpassword;
    }
}
