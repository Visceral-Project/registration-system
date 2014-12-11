package eu.visceral.registration.managedbeans;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;

import eu.visceral.registration.ejb.eao.VisceralEAO;
import eu.visceral.registration.ejb.entity.Competition;
import eu.visceral.registration.ejb.entity.User;
import eu.visceral.registration.ejb.entity.Vm;

/**
 * @author Georgios Kotsios-Kontokotsios
 * 
 */
@ManagedBean(name = "adminDashboardBean")
@SessionScoped()
public class AdminDashboardBean {

    @EJB
    VisceralEAO service;
    private List<User> userList;
    private int startIndex = 0;
    private int pageSize = 10;
    private List<Competition> competitions;
    private int compId = 0;

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
        }

        List<Competition> c = service.queryDBForCompetitions();
        if (c != null && !c.isEmpty()) {
            setCompetitions(c);
        } else {
            setCompetitions(c);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "No available benchmarks", null));
        }
    }

    public void loadUserList() {
        // Fetch paged data from the database
        if (this.compId > 0) {
            this.userList = service.getAllUsersPaged(startIndex, pageSize, this.compId);
        } else {
            this.userList = null;
        }
    }

    public void reloadList() {
        this.startIndex = 0;
        loadUserList();
    }

    public void logUserOut() {
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("admin", "");
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("username", "");
        try {
            this.startIndex = 0;
            this.compId = 0;
            FacesContext.getCurrentInstance().getExternalContext().redirect("/register/Login.xhtml");
        } catch (IOException e) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, e);
        }
    }

    public void deleteUser(User user) {
        this.service.deleteUser(user);
    }

    public boolean hasVm(User user) {
        Vm vm = service.getVMinfo(user);
        return vm != null ? true : false;
    }

    public List<Vm> getVm(User user) {
        List<Vm> VMs = new ArrayList<Vm>();
        VMs.add(this.service.getVMinfo(user));
        return VMs;
    }

    public String getStatus(User user) {
        if (user.getUniqueid().contains("_submitted")) {
            return "vm-submitted";
        } else if (service.getVMinfo(user) != null) {
            return "vm-assigned";
        } else if (user.getCopyrightSigned()) {
            return "activated";
        } else if (user.getPdfUploaded()) {
            return "pdf-uploaded";
        } else {
            return "";
        }
    }

    public String URLencode(String url) throws UnsupportedEncodingException {
        return URLEncoder.encode(url, "UTF-8");
    }

    /**
     * @return the userList
     */
    public List<User> getUserList() {
        if (userList == null) {
            loadUserList();
        }
        return userList;
    }

    /**
     * @param userList
     *            the userList to set
     */
    public void setUserList(List<User> userList) {
        this.userList = userList;
    }

    /**
     * @return the startIndex
     */
    public int getStartIndex() {
        return startIndex;
    }

    /**
     * @param startIndex
     *            the startIndex to set
     */
    public void setStartIndex(int startIndex) {
        this.startIndex = startIndex;
    }

    /**
     * @return the pageSize
     */
    public int getPageSize() {
        return pageSize;
    }

    /**
     * @param pageSize
     *            the pageSize to set
     */
    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    /**
     * @returns true if startIndex is greater than 0
     */
    public boolean hasPrevious() {
        return (startIndex - pageSize) >= 0;
    }

    /**
     * @returns true if startIndex is greater than 0
     */
    public boolean hasNext() {
        int numOfUsers = this.service.getNumOfUsersOnCompetition(this.compId);
        if (numOfUsers < (startIndex + pageSize)) {
            return false;
        }
        return true;
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
}
