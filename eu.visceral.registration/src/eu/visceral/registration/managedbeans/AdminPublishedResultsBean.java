package eu.visceral.registration.managedbeans;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;

import eu.visceral.registration.ejb.eao.VisceralEAO;
import eu.visceral.registration.ejb.entity.PublishedResult;

/**
 * @author Georgios Kotsios-Kontokotsios
 * 
 */
@ManagedBean(name = "adminLeaderboardBean")
@SessionScoped()
public class AdminPublishedResultsBean {
    @EJB
    VisceralEAO service;

    private List<PublishedResult> publishedResults;

    private List<String> metrics = Arrays.asList("Dice Coefficient", "Jaccard Coefficient", "Area under ROC Curve", "Cohen Kappa", "Rand Index", "Adjusted Rand Index", "Interclass Correlation",
            "Volumetric Similarity Coefficient", "Mutual Information", "Hausdorff Distance", "Average Hausdorff Distance", "Mahanabolis Distance", "Variation of Information", "Global Consistency Error",
            "Probabilistic Distance", "Sensitivity", "Specificity", "Precision", "F-Measure", "Accuracy", "Fallout");

    public void init() {
        String adminsEmail = (String) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("admin");

        if (adminsEmail == null || adminsEmail.isEmpty()) {
            try {
                FacesContext.getCurrentInstance().getExternalContext().redirect("/register/Login.xhtml");
            } catch (IOException e) {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, e);
            }
        }

        publishedResults = service.getPublishedResults();
    }

    public String publishStatus(float value) {
        if (value == -1234) {
            return " ";
        } else {
            return String.valueOf(value);
        }
    }

    public boolean isMetricResults(String metric) {
        for (PublishedResult result : publishedResults) {
            if (result.getMetric().equalsIgnoreCase(metric)) {
                return true;
            }
        }
        return false;
    }

    public List<PublishedResult> publishedResults(String metric) {
        List<PublishedResult> response = new ArrayList<PublishedResult>();

        for (PublishedResult result : publishedResults) {
            if (result.getMetric().equalsIgnoreCase(metric)) {
                response.add(result);
            }
        }

        return response;
    }

    public void deletePublishedResult(PublishedResult result) {
        if (service.deletePublishedResult(result)) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Result removed from the leaderboard", null));
        } else {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Failed to remove result. Please try again", null));
        }
    }
    
    public void deleteAllPublishedResults() {
        if (service.removeAllPublishedResults()) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "All results were removed from the leaderboard", null));
        } else {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Failed to remove results. Please try again", null));
        }
    }

    /**
     * @return the metrics
     */
    public List<String> getMetrics() {
        return metrics;
    }

    /**
     * @param metrics
     *            the metrics to set
     */
    public void setMetrics(List<String> metrics) {
        this.metrics = metrics;
    }

    /**
     * @return the publishedResults
     */
    public List<PublishedResult> getPublishedResults() {
        return publishedResults;
    }

    /**
     * @param publishedResults
     *            the publishedResults to set
     */
    public void setPublishedResults(List<PublishedResult> publishedResults) {
        this.publishedResults = publishedResults;
    }

}
