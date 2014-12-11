package eu.visceral.registration.managedbeans;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

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

import org.apache.commons.lang.RandomStringUtils;

import eu.visceral.registration.ejb.eao.VisceralEAO;
import eu.visceral.registration.ejb.entity.Competition;

/**
 * @author Georgios Kotsios-Kontokotsios
 * 
 */
@ManagedBean(name = "regBean")
@SessionScoped()
public class RegistrationBean {

    @EJB
    VisceralEAO service;
    private String firstname;
    private String lastname;
    private String password;
    private String confirmpassword;
    private String email;
    private String confirmemail;
    private String affiliation;
    private boolean copyright_signed;
    private String phone;
    private String preferance;
    private String street;
    private int zip;
    private String city;
    private String country;
    private String uniqueId;

    private List<Competition> competitions;
    private String[] selectedCompetitions;

    public void init() {
        List<Competition> c = service.queryDBForCompetitions();
        if (c != null && !c.isEmpty()) {
            setCompetitions(c);
            this.selectedCompetitions = new String[1];
            this.selectedCompetitions[0] = c.get(c.size() - 1).getName();
        } else {
            addMessage(new FacesMessage(FacesMessage.SEVERITY_ERROR, "The available benchmarks could not be loaded. Please try registering later.", null));
        }
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

    public String addUser() {
        // Set the participant's unique ID
        this.uniqueId = RandomStringUtils.randomAlphanumeric(6);
        // If Successful, return success message.
        if (service.retrieveUser(this.email) == null && service.retrieveAdmin(this.email) == null) {
            if (service.persistUser(this)) {
                addMessage(new FacesMessage(FacesMessage.SEVERITY_WARN, "Registration Successful. A confirmation was sent to your mail.", null));
                SendEmail mail = new SendEmail();
                mail.sendRegistrationConf(this.email);
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

    public boolean isCompetitionClosed(Competition c) {
        Calendar cal = Calendar.getInstance();
        Date currentDate = cal.getTime();

        if (c.getEnddate().before(currentDate)) {
            return true;
        }
        return false;
    }

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
     * @return the copyright_signed
     */
    public boolean isCopyright_signed() {
        return copyright_signed;
    }

    /**
     * @param copyright_signed
     *            the copyright_signed to set
     */
    public void setCopyright_signed(boolean copyright_signed) {
        this.copyright_signed = copyright_signed;
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
     * @return the street
     */
    public String getStreet() {
        return street;
    }

    /**
     * @param street
     *            the street to set
     */
    public void setStreet(String street) {
        this.street = street;
    }

    /**
     * @return the zip
     */
    public int getZip() {
        return zip;
    }

    /**
     * @param zip
     *            the zip to set
     */
    public void setZip(int zip) {
        this.zip = zip;
    }

    /**
     * @return the city
     */
    public String getCity() {
        return city;
    }

    /**
     * @param city
     *            the city to set
     */
    public void setCity(String city) {
        this.city = city;
    }

    /**
     * @return the country
     */
    public String getCountry() {
        return country;
    }

    /**
     * @param country
     *            the country to set
     */
    public void setCountry(String country) {
        this.country = country;
    }

    /**
     * @return the uniqueId
     */
    public String getUniqueId() {
        return uniqueId;
    }

    /**
     * @param uniqueId
     *            the uniqueId to set
     */
    public void setUniqueId(String uniqueId) {
        this.uniqueId = uniqueId;
    }

    /**
     * @return the competitions
     */
    public List<Competition> getCompetitions() {
        return competitions;
    }

    /**
     * @param competitions
     *            the competitions to set
     */
    public void setCompetitions(List<Competition> competitions) {
        this.competitions = competitions;
    }

    /**
     * @return the selectedCompetitions
     */
    public String[] getSelectedCompetitions() {
        return selectedCompetitions;
    }

    /**
     * @param selectedCompetitions
     *            the selectedCompetitions to set
     */
    public void setSelectedCompetitions(String[] selectedCompetitions) {
        this.selectedCompetitions = selectedCompetitions;
    }

    /**
     * @return the preferance
     */
    public String getPreferance() {
        return preferance;
    }

    /**
     * @param preferance
     *            the preferance to set
     */
    public void setPreferance(String preferance) {
        this.preferance = preferance;
    }
}
