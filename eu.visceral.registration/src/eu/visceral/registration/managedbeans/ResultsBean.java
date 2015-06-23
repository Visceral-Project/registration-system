package eu.visceral.registration.managedbeans;

import java.io.IOException;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;

import eu.visceral.registration.ejb.eao.VisceralEAO;
import eu.visceral.registration.ejb.entity.User;

/**
 * @author Georgios Kotsios-Kontokotsios
 * 
 */
@ManagedBean(name = "resultsBean")
@SessionScoped()
public class ResultsBean {
    @EJB
    VisceralEAO service;
    private User loggedUser;
    private boolean results;
    private List<String> measures = Arrays.asList("empty", "count", "minimum", "maximum", "median", "average");
    private List<String> metrics = Arrays.asList("Dice Coefficient", "Jaccard Coefficient", "Area under ROC Curve", "Cohen Kappa", "Rand Index", "Adjusted Rand Index", "Interclass Correlation",
            "Volumetric Similarity Coefficient", "Mutual Information", "Hausdorff Distance", "Average Hausdorff Distance", "Mahanabolis Distance", "Variation of Information", "Global Consistency Error",
            "Probabilistic Distance", "Sensitivity", "Specificity", "Precision", "F-Measure", "Accuracy", "Fallout");
    private List<String> metricsnew = Arrays.asList("Dice Coefficient", "Average Hausdorff Distance","Adjusted Rand Index","Interclass Correlation");
    private List<String> modalities;
    private List<Integer> configurations;
    private List<String> regions;
    private List<Timestamp> timestamps;
    private String modality;
    private String region;
    private int configuration;
    private String timestamp;
    private boolean showResultsTable = false;
    private String organToPublish;

    private boolean getUserResults(String email) {
        loggedUser = service.retrieveUser(email);
        if (loggedUser != null && !loggedUser.getUniqueid().isEmpty() && service.existUserResults(loggedUser.getUniqueid().replaceAll("_submitted", ""))) {
            results = true;
        } else {
            results = false;
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "No results available for your account", null));
        }
        return results;
    }

    public void getResultsList() {
        setShowResultsTable(true);
    }

    private void fillOptions() {
        this.modalities = service.getAvailableModalities(loggedUser.getUniqueid());
        this.configurations = service.getAvailableConfigurations(loggedUser.getUniqueid());
        this.regions = service.getAvailableRegions(loggedUser.getUniqueid());
        this.timestamps = service.getAvailableSubmisions(loggedUser.getUniqueid());
    }

    public void init() {
        String usersEmail = (String) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("username");
        if (usersEmail == null || usersEmail.isEmpty()) {
            try {
                FacesContext.getCurrentInstance().getExternalContext().redirect("/register/Login.xhtml");
            } catch (IOException e) {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, e);
            }
        } else {
            getUserResults(usersEmail);
            fillOptions();
        }
    }

    public void publishOrganResult() {
        Timestamp ts = null;
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSS");
            Date parsedDate = dateFormat.parse(this.timestamp);
            ts = new java.sql.Timestamp(parsedDate.getTime());
        } catch (ParseException e) {
            e.printStackTrace();
        }

        boolean publishStatus = true;

        if (ts != null) {
            for (String metric : this.metricsnew) {
                if (service.publishResult(loggedUser, this.modality, this.region, this.configuration, getMetricShortName(metric), metric, this.organToPublish, ts) == "failed") {
                    publishStatus = false;
                }
            }
        }

        if (publishStatus) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Your result is now published!", null));
        } else {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Could not publish results", null));
        }
    }

    public boolean isMetricResults(String metricLongName) {
        String metricShortName = getMetricShortName(metricLongName);
        long numOfResults = service.getMetricResultsCount(loggedUser.getUniqueid(), metricShortName.toLowerCase(), configuration, modality, region);
        boolean isMetricResults = false;
        if (loggedUser != null && metricShortName != null) {
            if (numOfResults > 0) {
                isMetricResults = true;
            }
        }
        return isMetricResults;
    }

    public String getSingleCell(String organ, String measure, String metric) {
        String organID;
        if (organ.matches("^\\d*$")) {
            organID = organ;
        } else {
            organID = getorganID(organ);
        }
        String shortMetricName = getMetricShortName(metric);
        String cellValue;
        DecimalFormat df = new DecimalFormat("#.###");

        switch (measure) {
        case "empty":
            cellValue = df.format(service.getSingleOrganResultEmpty(loggedUser.getUniqueid(), organID, this.modality, this.region, this.configuration, shortMetricName, this.timestamp));
            break;
        case "count":
            cellValue = df.format(service.getSingleOrganResultCount(loggedUser.getUniqueid(), organID, this.modality, this.region, this.configuration, shortMetricName, this.timestamp));
            break;
        case "average":
            cellValue = df.format(service.getSingleOrganResultAvg(loggedUser.getUniqueid(), organID, this.modality, this.region, this.configuration, shortMetricName, this.timestamp));
            break;
        case "minimum":
            cellValue = df.format(service.getSingleOrganResultMin(loggedUser.getUniqueid(), organID, this.modality, this.region, this.configuration, shortMetricName, this.timestamp));
            break;
        case "maximum":
            cellValue = df.format(service.getSingleOrganResultMax(loggedUser.getUniqueid(), organID, this.modality, this.region, this.configuration, shortMetricName, this.timestamp));
            break;
        case "median":
            cellValue = df.format(service.getSingleOrganResultMedian(loggedUser.getUniqueid(), organID, this.modality, this.region, this.configuration, shortMetricName, this.timestamp));
            break;
        default:
            cellValue = "n/a";
            break;
        }
        return cellValue;
    }

    private String getMetricShortName(String metricLongName) {
        String metricShortName;

        switch (metricLongName) {
        case "Dice Coefficient":
            metricShortName = "DICE";
            break;
        case "Jaccard Coefficient":
            metricShortName = "JACRD";
            break;
        case "Area under ROC Curve":
            metricShortName = "AUC";
            break;
        case "Cohen Kappa":
            metricShortName = "KAPPA";
            break;
        case "Rand Index":
            metricShortName = "RNDIND";
            break;
        case "Adjusted Rand Index":
            metricShortName = "ADJRIND";
            break;
        case "Interclass Correlation":
            metricShortName = "ICCORR";
            break;
        case "Volumetric Similarity Coefficient":
            metricShortName = "VOLSMTY";
            break;
        case "Mutual Information":
            metricShortName = "MUTINF";
            break;
        case "Hausdorff Distance":
            metricShortName = "HDRFDST";
            break;
        case "Average Hausdorff Distance":
            metricShortName = "AVGDIST";
            break;
        case "Mahanabolis Distance":
            metricShortName = "MAHLNBS";
            break;
        case "Variation of Information":
            metricShortName = "VARINFO";
            break;
        case "Global Consistency Error":
            metricShortName = "GCOERR";
            break;
        case "Probabilistic Distance":
            metricShortName = "PROBDST";
            break;
        case "Sensitivity":
            metricShortName = "SNSVTY";
            break;
        case "Specificity":
            metricShortName = "SPCFTY";
            break;
        case "Precision":
            metricShortName = "PRCISON";
            break;
        case "F-Measure":
            metricShortName = "FMEASR";
            break;
        case "Accuracy":
            metricShortName = "ACURCY";
            break;
        case "Fallout":
            metricShortName = "FALLOUT";
            break;
        default:
            metricShortName = null;
            break;
        }
        return metricShortName;
    }

    private String getorganID(String organName) {
        String organID;

        switch (organName) {
        case "Left Kidney":
            organID = "29663";
            break;
        case "Right Kidney":
            organID = "29662";
            break;
        case "Spleen":
            organID = "86";
            break;
        case "Liver":
            organID = "58";
            break;
        case "Left Lung":
            organID = "1326";
            break;
        case "Right Lung":
            organID = "1302";
            break;
        case "Unirary Bladder":
            organID = "237";
            break;
        case "Muscle Body Of Left Rectus Abdominis":
            organID = "40358";
            break;
        case "Muscle Body Of Right Rectus Abdominis":
            organID = "40357";
            break;
        case "Lumbar Vertebra 1":
            organID = "29193";
            break;
        case "Thyroid":
            organID = "7578";
            break;
        case "Pancreas":
            organID = "170";
            break;
        case "Left Psoas Major Muscle":
            organID = "32249";
            break;
        case "Right Psoas Major Muscle":
            organID = "32248";
            break;
        case "Gallblader":
            organID = "187";
            break;
        case "Sternum":
            organID = "2473";
            break;
        case "Aorta":
            organID = "480";
            break;
        case "Trachea":
            organID = "1247";
            break;
        case "Left Adrenal Gland":
            organID = "30325";
            break;
        case "Right Adrenal Gland":
            organID = "30324";
            break;
        default:
            organID = null;
            break;
        }
        return organID;
    }

    /**
     * @return the measures
     */
    public List<String> getMeasures() {
        return measures;
    }

    /**
     * @param measures
     *            the measures to set
     */
    public void setMeasures(List<String> measures) {
        this.measures = measures;
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
     * @return the modalities
     */
    public List<String> getModalities() {
        return modalities;
    }

    /**
     * @param modalities
     *            the modalities to set
     */
    public void setModalities(List<String> modalities) {
        this.modalities = modalities;
    }

    /**
     * @return the configurations
     */
    public List<Integer> getConfigurations() {
        return configurations;
    }

    /**
     * @param configurations
     *            the configurations to set
     */
    public void setConfigurations(List<Integer> configurations) {
        this.configurations = configurations;
    }

    /**
     * @return the regions
     */
    public List<String> getRegions() {
        return regions;
    }

    /**
     * @param regions
     *            the regions to set
     */
    public void setRegions(List<String> regions) {
        this.regions = regions;
    }

    /**
     * @return the modality
     */
    public String getModality() {
        return modality;
    }

    /**
     * @param modality
     *            the modality to set
     */
    public void setModality(String modality) {
        this.modality = modality;
    }

    /**
     * @return the region
     */
    public String getRegion() {
        return region;
    }

    /**
     * @param region
     *            the region to set
     */
    public void setRegion(String region) {
        this.region = region;
    }

    /**
     * @return the configuration
     */
    public int getConfiguration() {
        return configuration;
    }

    /**
     * @param configuration
     *            the configuration to set
     */
    public void setConfiguration(int configuration) {
        this.configuration = configuration;
    }

    /**
     * @return the showResultsTable
     */
    public boolean isShowResultsTable() {
        return showResultsTable;
    }

    /**
     * @param showResultsTable
     *            the showResultsTable to set
     */
    public void setShowResultsTable(boolean showResultsTable) {
        this.showResultsTable = showResultsTable;
    }

    /**
     * @return the results
     */
    public boolean isResults() {
        return results;
    }

    /**
     * @param results
     *            the results to set
     */
    public void setResults(boolean results) {
        this.results = results;
    }

    /**
     * @return the timestamps
     */
    public List<Timestamp> getTimestamps() {
        return timestamps;
    }

    /**
     * @param timestamps
     *            the timestamps to set
     */
    public void setTimestamps(List<Timestamp> timestamps) {
        this.timestamps = timestamps;
    }

    /**
     * @return the timestamp
     */
    public String getTimestamp() {
        return timestamp;
    }

    /**
     * @param timestamp
     *            the timestamp to set
     */
    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    /**
     * @return the organToPublish
     */
    public String getOrganToPublish() {
        return organToPublish;
    }

    /**
     * @param organToPublish
     *            the organToPublish to set
     */
    public void setOrganToPublish(String organToPublish) {
        this.organToPublish = organToPublish;
    }

    /**
     * @return the metricsnew
     */
    public List<String> getMetricsnew() {
        return metricsnew;
    }

    /**
     * @param metricsnew the metricsnew to set
     */
    public void setMetricsnew(List<String> metricsnew) {
        this.metricsnew = metricsnew;
    }
}
