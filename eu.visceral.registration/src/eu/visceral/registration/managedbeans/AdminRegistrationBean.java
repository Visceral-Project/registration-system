package eu.visceral.registration.managedbeans;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;
import javax.faces.validator.ValidatorException;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

import eu.visceral.registration.ejb.eao.VisceralEAO;

/**
 * @author Georgios Kotsios-Kontokotsios
 * 
 */
@ManagedBean(name = "adminRegBean")
@SessionScoped()
public class AdminRegistrationBean {

    @EJB
    VisceralEAO service;
    private String firstname;
    private String lastname;
    private String password;
    private String confirmpassword;
    private String email;
    private String confirmemail;
    private String affiliation;
    private boolean get_conf_emails;
    private String phone;

    /**
     * @return the firstname
     */
    public String getFirstname() {
        return firstname;
    }

    /**
     * @param firstname
     *            the firstname to set
     */
    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    /**
     * @return the lastname
     */
    public String getLastname() {
        return lastname;
    }

    /**
     * @param lastname
     *            the lastname to set
     */
    public void setLastname(String lastname) {
        this.lastname = lastname;
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
     * @return the confirmemail
     */
    public String getConfirmemail() {
        return confirmemail;
    }

    /**
     * @param confirmemail
     *            the confirmemail to set
     */
    public void setConfirmemail(String confirmemail) {
        this.confirmemail = confirmemail;
    }

    /**
     * @return the affiliation
     */
    public String getAffiliation() {
        return affiliation;
    }

    /**
     * @param affiliation
     *            the affiliation to set
     */
    public void setAffiliation(String affiliation) {
        this.affiliation = affiliation;
    }

    /**
     * @return the phone
     */
    public String getPhone() {
        return phone;
    }

    /**
     * @param phone
     *            the phone to set
     */
    public void setPhone(String phone) {
        this.phone = phone;
    }

    /**
     * @return the get_conf_emails
     */
    public boolean isGet_conf_emails() {
        return get_conf_emails;
    }

    /**
     * @param get_conf_emails
     *            the get_conf_emails to set
     */
    public void setGet_conf_emails(boolean get_conf_emails) {
        this.get_conf_emails = get_conf_emails;
    }

    private void addMessage(FacesMessage message) {
        FacesContext.getCurrentInstance().addMessage(null, message);
    }

    public void emailValidator(FacesContext context, UIComponent component, Object value) {
        // Get the first email address from the attribute
        UIInput emailComponent = (UIInput) component.getAttributes().get("email");

        // Retrieve the String value from the component
        String email = (String) emailComponent.getValue();

        // Convert the value parameter to a String.
        String confirmEmail = (String) value;
        if (email == null || confirmEmail == null) {
            throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Please fill both email fields!", null));
        } else if (!email.equals(confirmEmail)) {
            throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Email addresses doesn't match", null));
        } else {
            try {
                InternetAddress emailAddr = new InternetAddress(email);
                emailAddr.validate();
            } catch (AddressException ex) {
                throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Invalid email address. Must be like user@domain", null));
            }
        }
    }

    public String addAdmin() {
        // If Successful, return success message.
        if (service.retrieveUser(this.email) == null && service.retrieveAdmin(this.email) == null) {
            if (service.persistAdmin(this)) {
                addMessage(new FacesMessage(FacesMessage.SEVERITY_WARN, "Registration Successful. " + this.firstname + " is an administrator.", null));
                return "success";
            }
            // If Unsuccessful, return failure message
            addMessage(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Registration Failed", null));
            return "failure";
        }
        // If Unsuccessful, return failure message
        addMessage(new FacesMessage(FacesMessage.SEVERITY_ERROR, "You are already registered either as a user or as an Administrator.", null));
        return "failure";
    }
}
