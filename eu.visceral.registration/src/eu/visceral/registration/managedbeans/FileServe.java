package eu.visceral.registration.managedbeans;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

/**
 * @author Georgios Kotsios-Kontokotsios
 * 
 */
@ManagedBean(name = "fileServe")
@RequestScoped
public class FileServe {

    @ManagedProperty(value = "#{param.pdf}")
    private String pdf;

    /**
     * @return the pdf
     */
    public String getPdf() {
        return pdf;
    }

    /**
     * @param pdf
     *            the pdf to set
     */
    public void setPdf(String pdf) {
        this.pdf = pdf;
    }

    public void download() throws Exception {
        if (this.pdf != null) {
            FacesContext ctx = FacesContext.getCurrentInstance();
            pushFile(ctx.getExternalContext(), System.getProperty("user.home") + "/signedAgreements/" + this.pdf, this.pdf);
            ctx.responseComplete();
        }
    }

    public void downloadBenchmarkFile(String competition, String filename) throws IOException {
        if (filename != null && !filename.isEmpty()) {
            FacesContext ctx = FacesContext.getCurrentInstance();
            pushFile(ctx.getExternalContext(), System.getProperty("user.home") + "/" + competition + "/" + filename, filename);
            ctx.responseComplete();
        }
    }

    private void pushFile(ExternalContext extCtx, String fileName, String displayName) throws IOException {
        File f = new File(fileName);
        if (!f.exists()) {
            try {
                FacesContext.getCurrentInstance().getExternalContext().redirect("/register/Error.html");
            } catch (IOException e) {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, e);
            }
        } else {
            int length = 0;
            OutputStream os = extCtx.getResponseOutputStream();
            String mimetype = extCtx.getMimeType(fileName);

            extCtx.setResponseContentType((mimetype != null) ? mimetype : "application/octet-stream");
            extCtx.setResponseContentLength((int) f.length());
            extCtx.setResponseHeader("Content-Disposition", "attachment; filename=\"" + displayName + "\"");

            // Stream to the requester.
            byte[] bbuf = new byte[1024];
            DataInputStream in = new DataInputStream(new FileInputStream(f));

            while ((in != null) && ((length = in.read(bbuf)) != -1)) {
                os.write(bbuf, 0, length);
            }
            in.close();
        }
    }
}