package eu.visceral.registration.managedbeans;

import java.io.IOException;
import java.sql.Date;
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

/**
 * @author Georgios Kotsios-Kontokotsios
 * 
 */
@ManagedBean(name = "benchmarkManagerBean")
@SessionScoped()
public class BenchmarkManager {

    @EJB
    VisceralEAO service;
    private String name;
    private Date startDate;
    private Date endDate;

    private List<Competition> competitions;
    private String competition;

    private String competitionToChange;
    private String newName;
    private java.util.Date newStartDate;
    private java.util.Date newEndDate;

    public void init() {
        String adminsEmail = (String) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("admin");
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

    public void createCompetition() {
        boolean compExists = false;
        for (Competition c : competitions) {
            if (c.getName().equalsIgnoreCase(this.name)) {
                compExists = true;
                break;
            }
        }
        if (compExists) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Benchmark exists", null));
        } else {
            if (startDate != null && endDate != null) {
                if (endDate.compareTo(startDate) > 0) {
                    if (service.createCompetition(this)) {
                        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Benchmark created successfully", null));
                    } else {
                        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "There was an error while creating the Benchmark. Please check the form and try again.", null));
                    }
                } else {
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Ending date must not precede starting date!", null));
                }
            }
        }
    }

    public void changeCompetition() {
        if (newStartDate != null && newEndDate != null) {
            if (newEndDate.compareTo(newStartDate) > 0) {
                if (service.changeCompetition(this)) {
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Benchmark changed successfully", null));
                } else {
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "There was an error while changing this Benchmark. Please check the form and try again.", null));
                }
            } else {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Ending date must not precede starting date!", null));
            }
        }
    }

    public void deleteCompetition() {
        for (Competition comp : competitions) {
            if (this.competition.equalsIgnoreCase(comp.getName())) {
                if (this.service.deleteCompetition(comp)) {
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Benchmark deleted successfully", null));
                } else {
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "There was an error deleting the Benchmark. Please try again.", null));
                }
            }
        }
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name
     *            the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the startDate
     */
    public Date getStartDate() {
        return startDate;
    }

    /**
     * @param startDate
     *            the startDate to set
     */
    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    /**
     * @return the endDate
     */
    public Date getEndDate() {
        return endDate;
    }

    /**
     * @param endDate
     *            the endDate to set
     */
    public void setEndDate(Date endDate) {
        this.endDate = endDate;
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
     * @return the competition
     */
    public String getCompetition() {
        return competition;
    }

    /**
     * @param competition
     *            the competition to set
     */
    public void setCompetition(String competition) {
        this.competition = competition;
    }

    /**
     * @return the competitionToChange
     */
    public String getCompetitionToChange() {
        return competitionToChange;
    }

    /**
     * @param competitionToChange
     *            the competitionToChange to set
     */
    public void setCompetitionToChange(String competitionToChange) {
        this.competitionToChange = competitionToChange;
    }

    /**
     * @return the newName
     */
    public String getNewName() {
        return newName;
    }

    /**
     * @param newName
     *            the newName to set
     */
    public void setNewName(String newName) {
        this.newName = newName;
    }

    /**
     * @return the newStartDate
     */
    public java.util.Date getNewStartDate() {
        return newStartDate;
    }

    /**
     * @param date
     *            the newStartDate to set
     */
    public void setNewStartDate(java.util.Date date) {
        this.newStartDate = date;
    }

    /**
     * @return the newEndDate
     */
    public java.util.Date getNewEndDate() {
        return newEndDate;
    }

    /**
     * @param date
     *            the newEndDate to set
     */
    public void setNewEndDate(java.util.Date date) {
        this.newEndDate = date;
    }
}
