package eu.visceral.registration;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.faces.context.FacesContext;
import javax.faces.event.PhaseEvent;
import javax.faces.event.PhaseId;
import javax.faces.event.PhaseListener;
import javax.servlet.http.HttpSession;

public class AuthorizationListener implements PhaseListener {

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = -2328967781294205530L;

    public void afterPhase(PhaseEvent event) {

        FacesContext facesContext = event.getFacesContext();
        String currentPage = facesContext.getViewRoot().getViewId();

        // True if user is on admin or user dashboard because they both end with
        // the same characters
        boolean isDashboardPage = currentPage.lastIndexOf("Dashboard.xhtml") > 0;
        HttpSession session = (HttpSession) facesContext.getExternalContext().getSession(true);
        Object currentUser = session.getAttribute("username");
        Object currentAdmin = session.getAttribute("admin");

        if (isDashboardPage && (currentUser == null || currentUser.toString().isEmpty()) && (currentAdmin == null || currentAdmin.toString().isEmpty())) {
            try {
                FacesContext.getCurrentInstance().getExternalContext().redirect("/register/Login.xhtml");
            } catch (IOException e) {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, e);
            }
        }
    }

    public void beforePhase(PhaseEvent event) {
    }

    public PhaseId getPhaseId() {
        return PhaseId.RESTORE_VIEW;
    }
}