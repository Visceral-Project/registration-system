package eu.visceral.registration.managedbeans;

import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;

import eu.visceral.registration.ejb.eao.VisceralEAO;
import eu.visceral.registration.ejb.entity.PublishedResult;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.context.FacesContext;

@SessionScoped()
@ManagedBean
public class PublishedResultsBean {  
    private List<PublishedResult> publishedResults;
    private String metric;
    private String organ;
    private PublishedResult maxdice;

    private String modality;
    private String algorithm;
    private boolean showResultsTable = false;
    private List<String> modalities =Arrays.asList("CT", "CTce", "MRwb","MRT1cefs");
    private List<String> organs =Arrays.asList("Left Kidney", "Right Kidney", "Left Lung","Right Lung","Spleen","Liver","Pancreas","Unirary Bladder","Muscle Body Of Left Rectus Abdominis","Muscle Body Of Right Rectus Abdominis","Lumbar Vertebra 1","Thyroid","Left Psoas Major Muscle","Right Psoas Major Muscle","Gallblader","Aorta","Sternum","Trachea","Left Adrenal Gland","Right Adrenal Gland");
    private List<String> metrics = Arrays.asList("Dice Coefficient", "Hausdorff Distance", "Average Hausdorff Distance");
    

    @EJB
    VisceralEAO service;
    public void init() {
    }   
      public String publishStatus(float value) {
        if (value == -1234) {
            return " ";
        } else {
            return String.valueOf(value);
        }
    }
    public void getResultsList() {
        setShowResultsTable(true);
    }

    
    //finds the maximum dice per organ and adds them in a list as an overview for the Leaderboard
    public List<PublishedResult> maxDices(){
        List<PublishedResult> response = new ArrayList<PublishedResult>();

        for(String organ:organs){
        publishedResults = service.getPublishedResults();
        maxdice=publishedResults.get(3);
        maxdice.setDice("0.0");
        maxdice.setOrganname("");
        try{
            for (PublishedResult result : publishedResults) {
                if(getorganID(organ).equalsIgnoreCase(result.getOrganname())){
                    if(Float.parseFloat(result.getDice()) >= Float.parseFloat(maxdice.getDice())){
                        maxdice=result;
                    }
                }
            } 
        }catch(NullPointerException e){}
        if(maxdice.getOrganname()!="")
        response.add(maxdice);
}
        return response;
    }


    public List<PublishedResult> publishedResults() {
        publishedResults = service.getPublishedResults();
        List<PublishedResult> response = new ArrayList<PublishedResult>();
            for (PublishedResult result : publishedResults) {
                try{
                if (result.getOrganname().equals(getorganID(organ))&& result.getModality().equals(modality)) {
                    response.add(result);
                }
                }catch(NullPointerException e){}
            } try{
            Collections.sort(response, new Comparator<PublishedResult>() {
                @Override public int compare(PublishedResult p1, PublishedResult p2) {
                    if (Float.parseFloat(p1.getDice()) > Float.parseFloat(p2.getDice())) {
                        return -1;
                      }
                      if (Float.parseFloat(p1.getDice()) < Float.parseFloat(p2.getDice())) {
                        return 1;
                      }
                      return 0;
                    
                }
            });
            }catch(NumberFormatException e){};
        return response;
    }
    
    
    public void deletePublishedResult(PublishedResult result) {
        if (service.deletePublishedResult(result)) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Result removed from the leaderboard", null));
        } else {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Failed to remove result. Please try again", null));
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
    public String getName(String organID) {
        String organName;
        switch (organID) {
        case "29663":
            organName ="Left Kidney" ;
            break;
        case "29662":
            organName = "Right Kidney";
            break;
        case "86":
            organName = "Spleen";
            break;
        case "58":
            organName = "Liver";
            break;
        case "1326":
            organName = "Left Lung";
            break;
        case "1302":
            organName = "Right Lung";
            break;
        case "237":
            organName = "Unirary Bladder";
            break;
        case "40358":
            organName = "Muscle Body Of Left Rectus Abdominis";
            break;
        case "40357":
            organName = "Muscle Body Of Right Rectus Abdominis";
            break;
        case "29193":
            organName = "Lumbar Vertebra 1";
            break;
        case "7578":
            organName = "Thyroid";
            break;
        case "170":
            organName = "Pancreas";
            break;
        case "32249":
            organName = "Left Psoas Major Muscle";
            break;
        case "32248":
            organName = "Right Psoas Major Muscle";
            break;
        case "187":
            organName ="Gallblader" ;
            break;
        case "2473":
            organName = "Sternum";
            break;
        case "480":
            organName = "Aorta";
            break;
        case "1247":
            organName = "Trachea";
            break;
        case "30325":
            organName = "Left Adrenal Gland";
            break;
        case "30324":
            organName = "Right Adrenal Gland";
            break;
        default:
            organName = null;
            break;
        }
        return organName;
    }
    /**
     * @return the metric
     */
    public String getMetric() {
        return metric;
    }

    /**
     * @param metric the metric to set
     */
    public void setMetric(String metric) {
        this.metric = metric;
    }

    /**
     * @return the organ
     */
    public String getOrgan() {
        return organ;
    }

    /**
     * @param organ the organ to set
     */
    public void setOrgan(String organ) {
        this.organ = organ;
    }

    /**
     * @return the modality
     */
    public String getModality() {
        return modality;
    }

    /**
     * @param modality the modality to set
     */
    public void setModality(String modality) {
        this.modality = modality;
    }

    /**
     * @return the algorithm
     */
    public String getAlgorithm() {
        return algorithm;
    }

    /**
     * @param algorithm the algorithm to set
     */
    public void setAlgorithm(String algorithm) {
        this.algorithm = algorithm;
    }

    /**
     * @return the showResultsTable
     */
    public boolean isShowResultsTable() {
        return showResultsTable;
    }

    /**
     * @param showResultsTable the showResultsTable to set
     */
    public void setShowResultsTable(boolean showResultsTable) {
        this.showResultsTable = showResultsTable;
    }

    /**
     * @return the modalities
     */
    public List<String> getModalities() {
        return modalities;
    }

    /**
     * @param modalities the modalities to set
     */
    public void setModalities(List<String> modalities) {
        this.modalities = modalities;
    }

    /**
     * @return the organs
     */
    public List<String> getOrgans() {
        return organs;
    }

    /**
     * @param organs the organs to set
     */
    public void setOrgans(List<String> organs) {
        this.organs = organs;
    }
}  
