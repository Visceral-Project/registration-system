package eu.visceral.registration.managedbeans;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
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
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;

import org.apache.commons.io.IOUtils;
import org.apache.myfaces.custom.fileupload.UploadedFile;

import eu.visceral.registration.ejb.eao.VisceralEAO;
import eu.visceral.registration.ejb.entity.Competition;

/**
 * @author Georgios Kotsios-Kontokotsios
 * 
 */
@ManagedBean(name = "fileManagerBean")
@SessionScoped()
public class FileManager {

    @EJB
    VisceralEAO service;

    private List<Competition> competitions;
    private String selection;
    private UploadedFile uploadedFile;
    private List<String> files;
    private boolean listExists;

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

    public void listFiles() {
        listExists = false;
        if (selection != null && !selection.isEmpty()) {
            Path dir = FileSystems.getDefault().getPath(System.getProperty("user.home") + "/" + selection + "/");
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

    public void upload() throws IOException {
        if (this.uploadedFile != null) {
            new File(System.getProperty("user.home") + "/" + selection + "/").mkdirs();
            File file = new File(System.getProperty("user.home") + "/" + selection + "/" + this.uploadedFile.getName());
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
        } else {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Please select a file first", null));
        }
    }

    public void deleteFile(String filename) {
        File file = new File(System.getProperty("user.home") + "/" + selection + "/" + filename);
        if (file.delete()) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "File deleted succefully", null));
        } else {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Failed to delete file", null));
        }
    }

    public void downloadFile(String filename) throws IOException {
        FileServe fileServe = new FileServe();
        fileServe.downloadBenchmarkFile(this.selection, filename);
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
     * @return the selection
     */
    public String getSelection() {
        return selection;
    }

    /**
     * @param selection
     *            the selection to set
     */
    public void setSelection(String selection) {
        this.selection = selection;
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
    public void setUploadedFile(UploadedFile uploadedFile) {
        this.uploadedFile = uploadedFile;
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
