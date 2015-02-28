package eu.visceral.registration.managedbeans;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;

import org.apache.commons.io.IOUtils;
import org.apache.myfaces.custom.fileupload.UploadedFile;

import eu.visceral.registration.ejb.eao.VisceralEAO;
import eu.visceral.registration.ejb.entity.Address;
import eu.visceral.registration.ejb.entity.Admin;
import eu.visceral.registration.ejb.entity.Competition;
import eu.visceral.registration.ejb.entity.User;
import eu.visceral.registration.ejb.entity.Vm;
import eu.visceral.registration.util.MailManHandler;

/**
 * @author Georgios Kotsios-Kontokotsios
 * 
 */
@ManagedBean(name = "dashboardBean")
@SessionScoped()
public class DashboardBean {

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
    private String uniqueID;
    private UploadedFile uploadedFile;

    private String street;
    private int zip;
    private String city;
    private String country;

    private User user;
    private boolean hasVM;
    private String VM_DNSname;
    private String VM_username;
    private String VM_password;
    private String VM_OS;
    private String VM_protocol;
    private String VM_port;
    private boolean VM_showkey;
    private boolean VM_status;
    private boolean VM_exclusion_status;
    private boolean VM_submitted;

    private Competition competition;
    private List<Competition> registeredCompetitions;
    private List<Competition> competitions;
    private String[] selectedCompetitions;
    private int compId;
    private long daysToDisplay;

    public void init() {
        String usersEmail = (String) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("username");
        // With the following line the logged in user is shown in the Dashboard
        // view.
        // FacesContext.getCurrentInstance().addMessage(null, new
        // FacesMessage(FacesMessage.SEVERITY_ERROR, usersEmail, null));
        if (usersEmail == "") {
            try {
                FacesContext.getCurrentInstance().getExternalContext().redirect("/register/Login.xhtml");
            } catch (IOException e) {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, e);
            }
        } else {
            List<Competition> c = service.queryDBForCompetitions();
            if (c != null && !c.isEmpty()) {
                setCompetitions(c);
            } else {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "The available benchmarks could not be loaded. Please try again later.", null));
            }

            this.user = service.retrieveUser(usersEmail);
            if (user != null) {
                this.email = this.user.getEmail();
                this.firstname = this.user.getFirstname();
                this.lastname = this.user.getLastname();
                this.affiliation = this.user.getAffiliation();
                this.street = this.user.getAddress().getStreet();
                this.zip = this.user.getAddress().getZip();
                this.city = this.user.getAddress().getCity();
                this.country = this.user.getAddress().getCountry();
                this.phone = this.user.getPhone();
                this.uniqueID = this.user.getUniqueid();
                this.copyright_signed = this.user.getCopyrightSigned();

                this.registeredCompetitions = this.user.getCompetitions();
                reloadInfo();

                diffDates();
                this.VM_status = false;
                Vm vm = service.getVMinfo(this.user);
                this.hasVM = vm != null ? true : false;
                if (vm != null) {
                    this.VM_DNSname = vm.getDnsname();
                    this.VM_OS = vm.getOs();
                    this.VM_password = vm.getPassword();
                    this.VM_username = vm.getUsername();
                    this.VM_protocol = vm.getProtocol();
                    this.VM_port = vm.getPort();
                    this.VM_showkey = vm.getShowInitKey();
                    this.VM_status = getVmStatus(this.VM_DNSname);
                    this.VM_exclusion_status = getVmExclusionStatus();
                }
                if (this.uniqueID.contains("_submitted")) {
                    VM_submitted = true;
                } else {
                    VM_submitted = false;
                }
            }
        }
    }

    public void reloadInfo() {
        if (this.compId > 0) {
            for (Competition c : this.competitions) {
                if (this.compId == c.getCompetitionid()) {
                    this.competition = c;
                }
            }
        } else {
            this.competition = registeredCompetitions.get(registeredCompetitions.size() - 1);
            this.compId = this.competition.getCompetitionid();
        }
    }

    public boolean getVmExclusionStatus() {
        VmApiAccessor vmApiAccessor = new VmApiAccessor();
        try {
            String response = vmApiAccessor.callApi("exclusionstatus", this.VM_DNSname);
            if (this.VM_exclusion_status && response.contains("excluded")) {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public void toggleVmExclusion() {
        VmApiAccessor vmApiAccessor = new VmApiAccessor();
        try {
            if (this.VM_exclusion_status) {
                String response = vmApiAccessor.callApi("exclude", this.VM_DNSname);
                if (response.contains("excluded")) {
                    this.VM_exclusion_status = false;
                }
            } else {
                String response = vmApiAccessor.callApi("include", this.VM_DNSname);
                if (response.contains("included")) {
                    this.VM_exclusion_status = true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean getVmStatus(String vmId) {
        VmApiAccessor vmApiAccessor = new VmApiAccessor();
        try {
            String response = vmApiAccessor.callApi("status", vmId);
            if (response.contains("started")) {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public void shutdownVm() {
        VmApiAccessor vmApiAccessor = new VmApiAccessor();
        try {
            String response = vmApiAccessor.callApi("shutdown", this.VM_DNSname);
            if (response.contains("shutdown")) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "VM is shutting down", null));
            } else {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "VM failed to shutdown", null));
            }
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Failed to communicate with Azure Cloud. Please try again.", null));
            e.printStackTrace();
        }
    }

    public void startVm() {
        VmApiAccessor vmApiAccessor = new VmApiAccessor();
        try {
            String response = vmApiAccessor.callApi("start", this.VM_DNSname);
            if (response.contains("start")) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "VM is booting up", null));
            } else {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "VM failed to start", null));
            }
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Failed to communicate with Azure Cloud. Please try again.", null));
            e.printStackTrace();
        }
    }

    public boolean hasCompetitionsStarted() {
        Calendar cal = Calendar.getInstance();
        Date currentDate = cal.getTime();
        return this.competition.getStartdate().before(currentDate);
    }

    public boolean isCompetitionClosed(Competition c) {
        Calendar cal = Calendar.getInstance();
        Date currentDate = cal.getTime();
        return c.getEnddate().before(currentDate);
    }

    public boolean isAlreadyRegistered(Competition c) {
        for (Competition comp : this.registeredCompetitions) {
            if (comp.getName().equalsIgnoreCase(c.getName())) {
                return true;
            }
        }
        return false;
    }

    public void downloadFile(String filename) throws IOException {
        FileServe fileServe = new FileServe();
        fileServe.downloadBenchmarkFile(this.competition.getName(), filename);
    }

    private void diffDates() {
        Calendar cal = Calendar.getInstance();
        Date currentDate = cal.getTime();

        long dif = 0;

        if (this.competition.getStartdate().before(currentDate)) {
            dif = this.competition.getEnddate().getTime() - currentDate.getTime();
        } else {
            dif = this.competition.getStartdate().getTime() - currentDate.getTime();
        }

        this.setDaysToDisplay(dif / (24 * 60 * 60 * 1000));
    }

    public String changeUserInfo() {
        this.user.setFirstname(this.firstname);
        this.user.setLastname(this.lastname);
        this.user.setAffiliation(this.affiliation);
        Address address = this.user.getAddress();
        address.setStreet(this.street);
        address.setZip(this.zip);
        address.setCity(this.city);
        address.setCountry(this.country);
        this.user.setAddress(address);
        this.user.setPhone(this.phone);

        if (this.selectedCompetitions.length == 0) {
            if (service.updateUser(this.user)) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "User information changed successfully", null));
            } else {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Persisting the user data failed", null));
            }
        } else {
            if (service.addCompetitions(this.user, this.selectedCompetitions)) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "User information changed successfully", null));

                // Add participant to the correct mailing lists
                for (String comp : this.selectedCompetitions) {
                    MailManHandler mailManHandler = new MailManHandler(comp);
                    mailManHandler.addLineToFile(this.email);
                    mailManHandler.syncMailManList();
                }

                // Reset the selected Competitions value
                this.selectedCompetitions = null;

                // Ask about VM choice
                if (this.hasVM) {
                    return "askAboutCloneVm";
                }
            } else {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Persisting the user data failed", null));
            }
        }
        return "goToDashboard";
    }

    public String disassociateVM() {
        SendEmail sendEmail = new SendEmail();
        sendEmail.sendNewVmRequest(this.user);

        this.service.deleteVm(service.getVMinfo(this.user));
        return "goToDashboard";
    }

    public String cloneVM() {
        SendEmail sendEmail = new SendEmail();
        sendEmail.sendVmCloneRequest(this.user, this.VM_DNSname);

        this.service.deleteVm(service.getVMinfo(this.user));
        return "goToDashboard";
    }

    public void logUserOut() {
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("admin", "");
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("username", "");
        try {
            FacesContext.getCurrentInstance().getExternalContext().redirect("/register/Login.xhtml");
        } catch (IOException e) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, e);
        }
    }

    public void deleteUser() {
        this.service.deleteUser(this.user);
        logUserOut();
    }

    public void upload() throws IOException {
        // Save the PDF agreement signed by the participant
        if (this.uploadedFile != null) {
            new File(System.getProperty("user.home") + "/signedAgreements/").mkdirs();
            File file = new File(System.getProperty("user.home") + "/signedAgreements/" + this.email + ".pdf");
            if (file.exists()) {
                file.delete();
                file.createNewFile();
            } else {
                file.createNewFile();
            }
            InputStream input = this.uploadedFile.getInputStream();
            OutputStream output = new FileOutputStream(file);
            try {
                IOUtils.copy(input, output);
            } finally {
                IOUtils.closeQuietly(output);
                IOUtils.closeQuietly(input);
            }

            this.user.setPdfUploaded(true);
            this.service.updateUser(this.user);

            // List<Admin> admins = this.service.getAdmins();
            List<Admin> admins = new ArrayList<Admin>();
            admins.add(new Admin());
            // if (admins != null) {
            SendEmail mail = new SendEmail();
            if (mail.sendPDFAgreement(getEmail(), admins)) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "The signed PDF was sent. After account activation you will receive a confirmation Email.", null));
            } else {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "There was a problem processing your PDF file. Please try uploading it again.", null));
            }
            // }
        } else {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Please select the signed PDF", null));
        }
    }

    public void submitVm() throws Exception {
        if (!this.uniqueID.contains("_submitted")) {
            VmApiAccessor api = new VmApiAccessor();
            String response = api.callApi("blockaccess", this.VM_DNSname);
            if (response.contains("202")) {
                SendEmail mail = new SendEmail();
                mail.sendVmSubmissionConf(this.user, VM_DNSname);

                this.user.setUniqueid(this.uniqueID + "_submitted");
                service.updateUser(this.user);
                VM_submitted = true;
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Virtual Machine submitted successfully", null));
            } else {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Virtual Machine submit failed", null));
            }
        } else {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Your Virtual Machine was already submitted", null));
        }
    }

    public String URLencode(String url) throws UnsupportedEncodingException {
        return URLEncoder.encode(url, "UTF-8");
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
     * @return the daysToDisplay
     */
    public long getDaysToDisplay() {
        return daysToDisplay;
    }

    /**
     * @param daysToDisplay
     *            the daysToDisplay to set
     */
    public void setDaysToDisplay(long daysToDisplay) {
        this.daysToDisplay = daysToDisplay;
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
     * @return the uniqueID
     */
    public String getUniqueID() {
        return uniqueID;
    }

    /**
     * @param uniqueID
     *            the uniqueID to set
     */
    public void setUniqueID(String uniqueID) {
        this.uniqueID = uniqueID;
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
     * @return the uploadedFile
     */
    public UploadedFile getUploadedFile() {
        return uploadedFile;
    }

    /**
     * @param uploadedFile
     *            the uploadedFile to set
     */
    public void setUploadedFile(UploadedFile uploadedFile) throws IOException {
        this.uploadedFile = uploadedFile;
    }

    /**
     * @return the competition
     */
    public Competition getCompetition() {
        return competition;
    }

    /**
     * @param competition
     *            the competition to set
     */
    public void setCompetition(Competition competition) {
        this.competition = competition;
    }

    /**
     * @return the hasVM
     */
    public boolean isHasVM() {
        return hasVM;
    }

    /**
     * @param hasVM
     *            the hasVM to set
     */
    public void setHasVM(boolean hasVM) {
        this.hasVM = hasVM;
    }

    /**
     * @return the vM_DNSname
     */
    public String getVM_DNSname() {
        return VM_DNSname;
    }

    /**
     * @param vM_DNSname
     *            the vM_DNSname to set
     */
    public void setVM_DNSname(String vM_DNSname) {
        VM_DNSname = vM_DNSname;
    }

    /**
     * @return the vM_username
     */
    public String getVM_username() {
        return VM_username;
    }

    /**
     * @param vM_username
     *            the vM_username to set
     */
    public void setVM_username(String vM_username) {
        VM_username = vM_username;
    }

    /**
     * @return the vM_password
     */
    public String getVM_password() {
        return VM_password;
    }

    /**
     * @param vM_password
     *            the vM_password to set
     */
    public void setVM_password(String vM_password) {
        VM_password = vM_password;
    }

    /**
     * @return the vM_OS
     */
    public String getVM_OS() {
        return VM_OS;
    }

    /**
     * @param vM_OS
     *            the vM_OS to set
     */
    public void setVM_OS(String vM_OS) {
        VM_OS = vM_OS;
    }

    /**
     * @return the vM_protocol
     */
    public String getVM_protocol() {
        return VM_protocol;
    }

    /**
     * @param vM_protocol
     *            the vM_protocol to set
     */
    public void setVM_protocol(String vM_protocol) {
        VM_protocol = vM_protocol;
    }

    /**
     * @return the vM_port
     */
    public String getVM_port() {
        return VM_port;
    }

    /**
     * @param vM_port
     *            the vM_port to set
     */
    public void setVM_port(String vM_port) {
        VM_port = vM_port;
    }

    /**
     * @return the vM_showkey
     */
    public boolean isVM_showkey() {
        return VM_showkey;
    }

    /**
     * @param vM_showkey
     *            the vM_showkey to set
     */
    public void setVM_showkey(boolean vM_showkey) {
        VM_showkey = vM_showkey;
    }

    /**
     * @return the vM_status
     */
    public boolean isVM_status() {
        return VM_status;
    }

    /**
     * @param vM_status
     *            the vM_status to set
     */
    public void setVM_status(boolean vM_status) {
        VM_status = vM_status;
    }

    /**
     * @return the vM_submitted
     */
    public boolean isVM_submitted() {
        return VM_submitted;
    }

    /**
     * @param vM_submitted
     *            the vM_submitted to set
     */
    public void setVM_submitted(boolean vM_submitted) {
        VM_submitted = vM_submitted;
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
     * @return the registeredCompetitions
     */
    public List<Competition> getRegisteredCompetitions() {
        return registeredCompetitions;
    }

    /**
     * @param registeredCompetitions
     *            the registeredCompetitions to set
     */
    public void setRegisteredCompetitions(List<Competition> registeredCompetitions) {
        this.registeredCompetitions = registeredCompetitions;
    }

    /**
     * @return the compId
     */
    public int getCompId() {
        return compId;
    }

    /**
     * @param compId
     *            the compId to set
     */
    public void setCompId(int compId) {
        this.compId = compId;
    }

    /**
     * @return the vM_exclusion_status
     */
    public boolean isVM_exclusion_status() {
        return VM_exclusion_status;
    }

    /**
     * @param vM_exclusion_status
     *            the vM_exclusion_status to set
     */
    public void setVM_exclusion_status(boolean vM_exclusion_status) {
        VM_exclusion_status = vM_exclusion_status;
    }
}
