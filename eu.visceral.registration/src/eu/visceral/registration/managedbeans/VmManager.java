package eu.visceral.registration.managedbeans;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;

import eu.visceral.registration.ejb.eao.VisceralEAO;
import eu.visceral.registration.ejb.entity.Vm;

/**
 * @author Georgios Kotsios-Kontokotsios
 * 
 */
@ManagedBean(name = "vmManagerBean")
@SessionScoped()
public class VmManager {

    @EJB
    VisceralEAO service;

    private List<Vm> vmList;
    private HashMap<String, String> vmStatuses;
    private String dnsName;
    private String username;
    private String password;
    private String os;
    private String protocol;
    private int port;
    private boolean show_init_key;

    public void init() {
        String adminsEmail = (String) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("admin");
        if (adminsEmail == "") {
            try {
                FacesContext.getCurrentInstance().getExternalContext().redirect("/register/Login.xhtml");
            } catch (IOException e) {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, e);
            }
        }

        List<Vm> VMs = service.getVMs();
        VmApiAccessor vmApiAccessor = new VmApiAccessor();
        vmStatuses = new HashMap<String, String>();
        if (VMs != null && !VMs.isEmpty()) {
            setVmList(VMs);
            for (Iterator<Vm> iterator = VMs.iterator(); iterator.hasNext();) {
                Vm vm = (Vm) iterator.next();
                try {
                    String responce = vmApiAccessor.callApi("status", vm.getDnsname());
                    if (responce.contains("started")) {
                        vmStatuses.put(vm.getDnsname(), "active");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else {
            setVmList(VMs);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "No virtual machines found", null));
        }
    }

    public void shutdownVm(String vmDndName) {
        VmApiAccessor vmApiAccessor = new VmApiAccessor();
        try {
            String responce = vmApiAccessor.callApi("shutdown", vmDndName);
            if (responce.contains("shutdown")) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "VM is shutting down", null));
            } else {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "VM failed to shutdown", null));
            }
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Failed to communicate with Azure Cloud. Please try again.", null));
            e.printStackTrace();
        }
    }

    public void startVm(String vmDndName) {
        VmApiAccessor vmApiAccessor = new VmApiAccessor();
        try {
            String responce = vmApiAccessor.callApi("start", vmDndName);
            if (responce.contains("start")) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "VM is booting up", null));
            } else {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "VM failed to start", null));
            }
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Failed to communicate with Azure Cloud. Please try again.", null));
            e.printStackTrace();
        }
    }

    public void deleteVm(Vm vm) {
        this.service.deleteVm(vm);
    }

    public void updateVm(Vm vm) {
        if (this.service.updateVm(vm)) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "VM info updated!", null));
        } else {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "VM info update failed! Please try again.", null));
        }
    }

    /**
     * @return the dnsName
     */
    public String getDnsName() {
        return dnsName;
    }

    /**
     * @param dnsName
     *            the dnsName to set
     */
    public void setDnsName(String dnsName) {
        this.dnsName = dnsName;
    }

    /**
     * @return the username
     */
    public String getUsername() {
        return username;
    }

    /**
     * @param username
     *            the username to set
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * @return the password
     */
    public String getPassword() {
        return password;
    }

    /**
     * @param password
     *            the password to set
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * @return the os
     */
    public String getOs() {
        return os;
    }

    /**
     * @param os
     *            the os to set
     */
    public void setOs(String os) {
        this.os = os;
    }

    /**
     * @return the protocol
     */
    public String getProtocol() {
        return protocol;
    }

    /**
     * @param protocol
     *            the protocol to set
     */
    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    /**
     * @return the port
     */
    public int getPort() {
        return port;
    }

    /**
     * @param port
     *            the port to set
     */
    public void setPort(int port) {
        this.port = port;
    }

    /**
     * @return the show_init_key
     */
    public boolean isShow_init_key() {
        return show_init_key;
    }

    /**
     * @param show_init_key
     *            the show_init_key to set
     */
    public void setShow_init_key(boolean show_init_key) {
        this.show_init_key = show_init_key;
    }

    /**
     * @return the vmList
     */
    public List<Vm> getVmList() {
        return vmList;
    }

    /**
     * @param vmList
     *            the vmList to set
     */
    public void setVmList(List<Vm> vmList) {
        this.vmList = vmList;
    }

    /**
     * @return the vmStatuses
     */
    public HashMap<String, String> getVmStatuses() {
        return vmStatuses;
    }

    /**
     * @param vmStatuses
     *            the vmStatuses to set
     */
    public void setVmStatuses(HashMap<String, String> vmStatuses) {
        this.vmStatuses = vmStatuses;
    }
}
