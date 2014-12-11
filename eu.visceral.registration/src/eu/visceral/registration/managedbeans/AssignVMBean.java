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
@ManagedBean(name = "assignVmBean")
@SessionScoped()
public class AssignVMBean {

    @EJB
    VisceralEAO service;

    private String email;

    private String dnsName;
    private String username;
    private String password;
    private String os;
    private String protocol;
    private int port;
    private boolean show_init_key;

    public void init() {
        String adminsEmail = (String) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("admin");
        if (adminsEmail == "") {
            try {
                FacesContext.getCurrentInstance().getExternalContext().redirect("/register/Login.xhtml");
            } catch (IOException e) {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, e);
            }
        }
    }

    public void assign() {
        if (this.email != null && !this.email.isEmpty()) {
            if (service.assignVM(this)) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "VM got assigned successfully", null));
                SendEmail mail = new SendEmail();
                mail.sendVMAssignedConf(this.email);
            } else {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "There was an error assigning the VM. Maybe a VM has already been assigned.", null));
            }
        } else {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "No user to assign VM. Please go back to dashboard and retry.", null));
        }
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

    /**
     * @return the dnsName
     */
    public String getDnsName() {
        return dnsName;
    }

    /**
     * @param dnsName
     *            the dnsName to set
     */
    public void setDnsName(String dnsName) {
        this.dnsName = dnsName;
    }

    /**
     * @return the username
     */
    public String getUsername() {
        return username;
    }

    /**
     * @param username
     *            the username to set
     */
    public void setUsername(String username) {
        this.username = username;
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
     * @return the os
     */
    public String getOs() {
        return os;
    }

    /**
     * @param os
     *            the os to set
     */
    public void setOs(String os) {
        this.os = os;
    }

    /**
     * @return the protocol
     */
    public String getProtocol() {
        return protocol;
    }

    /**
     * @param protocol
     *            the protocol to set
     */
    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    /**
     * @return the port
     */
    public int getPort() {
        return port;
    }

    /**
     * @param port
     *            the port to set
     */
    public void setPort(int port) {
        this.port = port;
    }

    /**
     * @return the show_init_key
     */
    public boolean isShow_init_key() {
        return show_init_key;
    }

    /**
     * @param show_init_key
     *            the show_init_key to set
     */
    public void setShow_init_key(boolean show_init_key) {
        this.show_init_key = show_init_key;
    }
}
