package eu.visceral.registration.managedbeans;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import eu.visceral.registration.ejb.eao.VisceralEAO;
import eu.visceral.registration.ejb.entity.PublishedResult;

/**
 * @author Georgios Kotsios-Kontokotsios
 * 
 */
@ManagedBean(name = "leaderboardBean")
@SessionScoped()
public class PublishedResultsBean {
    @EJB
    VisceralEAO service;

    private List<PublishedResult> publishedResults;

    private List<String> metrics = Arrays.asList("Dice Coefficient", "Jaccard Coefficient", "Area under ROC Curve", "Cohen Kappa", "Rand Index", "Adjusted Rand Index", "Interclass Correlation",
            "Volumetric Similarity Coefficient", "Mutual Information", "Hausdorff Distance", "Average Hausdorff Distance", "Mahanabolis Distance", "Variation of Information", "Global Consistency Error",
            "Probabilistic Distance", "Sensitivity", "Specificity", "Precision", "F-Measure", "Accuracy", "Fallout");

    public void init() {
        publishedResults = service.getPublishedResults();
    }

    public boolean isMetricResults(String metric) {
        for (PublishedResult result : publishedResults) {
            if (result.getMetric().equalsIgnoreCase(metric)) {
                return true;
            }
        }
        return false;
    }

    public String publishStatus(float value) {
        if (value == -1234) {
            return " ";
        } else {
            return String.valueOf(value);
        }
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
