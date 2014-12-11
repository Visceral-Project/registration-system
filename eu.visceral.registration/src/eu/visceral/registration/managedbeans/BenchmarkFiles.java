package eu.visceral.registration.managedbeans;

import java.io.IOException;
import java.nio.file.DirectoryIteratorException;
import java.nio.file.DirectoryStream;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;

import eu.visceral.registration.ejb.eao.VisceralEAO;
import eu.visceral.registration.ejb.entity.Competition;

/**
 * @author Georgios Kotsios-Kontokotsios
 * 
 */
@ManagedBean(name = "benchmarkFilesBean")
@SessionScoped()
public class BenchmarkFiles {

    @EJB
    VisceralEAO service;

    private String competition;
    private List<String> files;
    private boolean listExists;

    public void init(Competition c) {
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
            if (c != null) {
                this.competition = c.getName();
            } else {
                this.competition = service.retrieveUser(usersEmail).getCompetitions().get(0).getName();
            }
        }

        listFiles();
    }

    public void listFiles() {
        listExists = false;
        if (this.competition != null && !this.competition.isEmpty()) {
            Path dir = FileSystems.getDefault().getPath(System.getProperty("user.home") + "/" + this.competition + "/");
            try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir)) {
                files = new ArrayList<String>();
                for (Path file : stream) {
                    files.add(file.getFileName().toString());
                }
            } catch (IOException | DirectoryIteratorException x) {
                // IOException can never be thrown by the iteration.
                // In this snippet, it can only be thrown by newDirectoryStream.
                System.err.println(x);
            }

            if (files != null && !files.isEmpty()) {
                listExists = true;
            }
        }
    }

    public void downloadFile(String filename) throws IOException {
        FileServe fileServe = new FileServe();
        fileServe.downloadBenchmarkFile(competition, filename);
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
     * @return the files
     */
    public List<String> getFiles() {
        return files;
    }

    /**
     * @param files
     *            the files to set
     */
    public void setFiles(List<String> files) {
        this.files = files;
    }

    /**
     * @return the listExists
     */
    public boolean isListExists() {
        return listExists;
    }

    /**
     * @param listExists
     *            the listExists to set
     */
    public void setListExists(boolean listExists) {
        this.listExists = listExists;
    }
}
