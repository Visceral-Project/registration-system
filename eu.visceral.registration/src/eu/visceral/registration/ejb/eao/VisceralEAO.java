package eu.visceral.registration.ejb.eao;

import java.io.File;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityExistsException;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TransactionRequiredException;

import eu.visceral.registration.ejb.entity.Address;
import eu.visceral.registration.ejb.entity.Admin;
import eu.visceral.registration.ejb.entity.Competition;
import eu.visceral.registration.ejb.entity.Group;
import eu.visceral.registration.ejb.entity.PublishedResult;
import eu.visceral.registration.ejb.entity.SegmentationResult;
import eu.visceral.registration.ejb.entity.User;
import eu.visceral.registration.ejb.entity.Vm;
import eu.visceral.registration.managedbeans.AdminRegistrationBean;
import eu.visceral.registration.managedbeans.AssignVMBean;
import eu.visceral.registration.managedbeans.BenchmarkManager;
import eu.visceral.registration.managedbeans.ForgotBean;
import eu.visceral.registration.managedbeans.LoginBean;
import eu.visceral.registration.managedbeans.RegistrationBean;
import eu.visceral.registration.managedbeans.ResetPasswordBean;
import eu.visceral.registration.util.Util;

/**
 * Session Bean implementation class VisceralEAO
 * 
 * @author Georgios Kotsios-Kontokotsios
 * 
 */
@Stateless(mappedName = "visceralEAO")
@LocalBean
public class VisceralEAO {

    @PersistenceContext()
    EntityManager entityManager;

    private User currentUser;
    private Admin currentAdmin;

    private final String ADMIN_GROUP = "admin";

    /**
     * Default constructor.
     */
    public VisceralEAO() {

    }

    public boolean persistUser(RegistrationBean registrationBean) {
        try {
            // Populate an instance of the User Entity
            User user = new User();
            user.setFirstname(registrationBean.getFirstname());
            user.setLastname(registrationBean.getLastname());
            user.setPassword(Util.hash(registrationBean.getPassword()));
            user.setEmail(registrationBean.getEmail());
            user.setAffiliation(registrationBean.getAffiliation());
            user.setCopyrightSigned(registrationBean.isCopyright_signed());
            user.setPhone(registrationBean.getPhone());
            user.setPreferance(registrationBean.getPreferance());
            user.setUniqueid(registrationBean.getUniqueId());
            // Populate an instance of the Address Entity
            Address address = new Address();
            address.setStreet(registrationBean.getStreet());
            address.setZip(registrationBean.getZip());
            address.setCity(registrationBean.getCity());
            address.setCountry(registrationBean.getCountry());

            // Set the User Address
            user.setAddress(address);

            // Create a List of Users
            List<User> users = new ArrayList<User>();
            users.add(user);

            // Add the List of Users to the Address
            address.setUsers(users);

            // Generate JPQL String by appending the Competitions that the user
            // was added on.
            // Currently only one competition can be choosen during the
            // registration, but the db scheme supports users registering on
            // multiple competitions.

            // Retrieve the Competition names from the database
            String[] targets = registrationBean.getSelectedCompetitions();
            StringBuffer buffer = new StringBuffer();
            buffer.append("SELECT c FROM Competition c WHERE c.name=?1");
            int count = 2;
            for (int x = 1; x < targets.length; x++) {
                buffer.append(" OR c.name=?" + count);
                count++;
            }

            // Add the parameter values to the query
            count = 1;
            Query query = this.entityManager.createQuery(buffer.toString());
            for (String target : targets) {
                query.setParameter(count, target);
                count++;
            }

            // Fix for expression of type list needs express conversion to
            // conform to conversion List
            List<Competition> competitions = Util.castList(Competition.class, query.getResultList());

            // Add user to each competition
            for (Competition competition : competitions) {
                competition.getUsers().add(user);
            }

            // Add Competitions to user
            user.setCompetitions(competitions);

            // Commit data to the database
            entityManager.persist(user);

            return true;
        } catch (ClassCastException e) {
            System.out.println("Class Cast Exception");
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            System.out.println("Illegal Argument Exception");
            e.printStackTrace();
        } catch (Exception e) {
            System.out.println("Generic Exception");
            e.printStackTrace();
        }
        return false;
    }

    public boolean addCompetitions(User user, String[] targets) {
        StringBuffer buffer = new StringBuffer();
        buffer.append("SELECT c FROM Competition c WHERE c.name=?1");
        int count = 2;
        for (int x = 1; x < targets.length; x++) {
            buffer.append(" OR c.name=?" + count);
            count++;
        }

        // Add the parameter values to the query
        count = 1;
        Query query = this.entityManager.createQuery(buffer.toString());
        for (String target : targets) {
            query.setParameter(count, target);
            count++;
        }

        // Fix for expression of type list needs express conversion to
        // conform to conversion List
        List<Competition> competitions = Util.castList(Competition.class, query.getResultList());

        // Add user to each competition
        for (Competition competition : competitions) {
            competition.getUsers().add(user);
        }

        // First add all previously registered competitions
        competitions.addAll(user.getCompetitions());

        // Add Competitions to user
        user.setCompetitions(competitions);

        user.setUniqueid(user.getUniqueid().replaceAll("_submitted", ""));

        // Commit data to the database
        try {
            entityManager.merge(user);
            return true;
        } catch (IllegalArgumentException e) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, e);
            return false;
        } catch (TransactionRequiredException e) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, e);
            return false;
        }
    }

    public User retrieveUser(String email) {
        if (queryDBForUser("email", email)) {
            return this.currentUser;
        } else {
            return null;
        }
    }

    public boolean updateUser(User user) {
        try {
            entityManager.merge(user);
            return true;
        } catch (IllegalArgumentException e) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, e);
            return false;
        } catch (TransactionRequiredException e) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, e);
            return false;
        }
    }

    public String logUserIn(LoginBean loginBean) {
        if (queryDBForUser("email", loginBean.getEmail())) {
            String pass = this.currentUser.getPassword();
            if (Util.hash(loginBean.getPassword()).equals(pass)) {
                return "user";
            }
        } else if (queryDBForAdmin("email", loginBean.getEmail())) {
            String pass = this.currentAdmin.getPassword();
            if (Util.hash(loginBean.getPassword()).equals(pass)) {
                return "admin";
            }
        }

        return "";
    }

    public boolean isAdminValid(String email, String password) {
        if (queryDBForAdmin("email", email)) {
            String pass = this.currentAdmin.getPassword();
            if (Util.hash(password).equals(pass)) {
                return true;
            }
        }
        return false;
    }

    public boolean isFidValid(String fid) {
        if (queryDBForUser("forgotPasswordId", fid)) {
            return true;
        } else {
            return false;
        }
    };

    public boolean resetPwd(ResetPasswordBean bean) {
        if (queryDBForUser("forgotPasswordId", bean.getfid())) {
            this.currentUser.setPassword(Util.hash(bean.getPassword()));
            this.currentUser.setForgotPasswordId(null);
            this.entityManager.merge(this.currentUser);
            return true;
        } else {
            return false;
        }
    }

    public boolean updateForgotPasswordField(ForgotBean forgotBean) {
        if (queryDBForUser("email", forgotBean.getEmail())) {
            this.currentUser.setForgotPasswordId(forgotBean.getForgotID());
            entityManager.merge(this.currentUser);
            return true;
        } else {
            return false;
        }
    }

    public List<Competition> queryDBForCompetitions() {
        // Generate JPQL String which returns all available Competitions
        Query q = this.entityManager.createQuery("SELECT c FROM Competition c");
        q.setHint("javax.persistence.cache.storeMode", "REFRESH");

        List<Competition> competitions = Util.castList(Competition.class, q.getResultList());

        if (competitions != null && !competitions.isEmpty()) {
            return competitions;
        } else {
            return null;
        }
    }

    public boolean persistAdmin(AdminRegistrationBean adminRegistrationBean) {
        try {
            // Populate an instance of the User Entity
            Admin admin = new Admin();
            admin.setFirstname(adminRegistrationBean.getFirstname());
            admin.setLastname(adminRegistrationBean.getLastname());
            admin.setPassword(Util.hash(adminRegistrationBean.getPassword()));
            admin.setEmail(adminRegistrationBean.getEmail());
            admin.setAffiliation(adminRegistrationBean.getAffiliation());
            admin.setGetConfEmails(adminRegistrationBean.isGet_conf_emails());
            admin.setPhone(adminRegistrationBean.getPhone());

            Group group = new Group();
            group.setName(ADMIN_GROUP);
            group.setEmail(adminRegistrationBean.getEmail());

            admin.setGroup(group);

            List<Admin> admins = new ArrayList<Admin>();
            admins.add(admin);

            group.setAdmins(admins);

            // Commit data to the database
            entityManager.persist(admin);

            return true;
        } catch (ClassCastException e) {
            System.out.println("Class Cast Exception");
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            System.out.println("Illegal Argument Exception");
            e.printStackTrace();
        } catch (Exception e) {
            System.out.println("Generic Exception");
            e.printStackTrace();
        }
        return false;
    }

    public Vm getVMinfo(User user) {
        Query q = this.entityManager.createQuery("SELECT c FROM Vm c WHERE c.user=?1");
        q.setHint("javax.persistence.cache.storeMode", "REFRESH");
        q.setParameter(1, user);

        List<Vm> VMs = Util.castList(Vm.class, q.getResultList());
        if (VMs != null && !VMs.isEmpty()) {
            return VMs.get(0);
        } else {
            return null;
        }
    }

    public List<Vm> getVMs() {
        Query q = this.entityManager.createQuery("SELECT c FROM Vm c");
        q.setHint("javax.persistence.cache.storeMode", "REFRESH");

        List<Vm> VMs = Util.castList(Vm.class, q.getResultList());
        return VMs;
    }

    public List<Admin> getAdmins() {
        Query q = this.entityManager.createQuery("SELECT c FROM Admin c");
        List<Admin> admins = Util.castList(Admin.class, q.getResultList());

        if (admins != null && !admins.isEmpty()) {
            return admins;
        } else {
            return null;
        }
    }

    public List<User> getUsers() {
        Query q = this.entityManager.createQuery("SELECT c FROM User c");

        List<User> users = Util.castList(User.class, q.getResultList());
        if (users != null && !users.isEmpty()) {
            return users;
        } else {
            return null;
        }
    }

    private boolean queryDBForUser(String queryClaus, String queryParameter) {
        // Generate JPQL String which returns a user
        Query q = this.entityManager.createQuery("SELECT c FROM User c WHERE c." + queryClaus + "=?1");
        q.setHint("javax.persistence.cache.storeMode", "REFRESH");
        q.setParameter(1, queryParameter);

        List<User> users = Util.castList(User.class, q.getResultList());

        this.currentUser = new User(); // initiate currentUser
        if (users != null && !users.isEmpty()) {
            this.currentUser = users.get(0);
            return true;
        } else {
            return false;
        }
    }

    private boolean queryDBForAdmin(String queryClaus, String queryParameter) {
        // Generate JPQL String which returns a user
        Query q = this.entityManager.createQuery("SELECT c FROM Admin c WHERE c." + queryClaus + "=?1");
        q.setHint("javax.persistence.cache.storeMode", "REFRESH");
        q.setParameter(1, queryParameter);

        List<Admin> admins = Util.castList(Admin.class, q.getResultList());

        this.currentAdmin = new Admin(); // initiate currentUser
        if (admins != null && !admins.isEmpty()) {
            this.currentAdmin = admins.get(0);
            return true;
        } else {
            return false;
        }
    }

    public boolean assignVM(AssignVMBean assignVMBean) {
        User participant = retrieveUser(assignVMBean.getEmail());
        Vm vminfo = getVMinfo(participant);
        if (vminfo == null) {
            Vm vm = new Vm();

            vm.setDnsname(assignVMBean.getDnsName());
            vm.setUsername(assignVMBean.getUsername());
            vm.setPassword(assignVMBean.getPassword());
            vm.setOs(assignVMBean.getOs());
            vm.setProtocol(assignVMBean.getProtocol());
            vm.setPort(Integer.toString(assignVMBean.getPort()));
            vm.setShowInitKey(assignVMBean.isShow_init_key());

            vm.setUser(this.currentUser);

            try {
                entityManager.persist(vm);
                return true;
            } catch (EntityExistsException e) {
                return false;
            } catch (IllegalArgumentException e) {
                return false;
            } catch (TransactionRequiredException e) {
                return false;
            }
        } else {
            return false;
        }
    }

    public boolean createCompetition(BenchmarkManager createBenchmark) {
        Competition competition = new Competition();
        competition.setName(createBenchmark.getName());
        competition.setStartdate(createBenchmark.getStartDate());
        competition.setEnddate(createBenchmark.getEndDate());

        try {
            entityManager.persist(competition);
            File compDir = new File(System.getProperty("user.home") + "/" + competition.getName());
            // if the directory does not exist, create it
            if (!compDir.exists()) {
                compDir.mkdir();
            }
            return true;
        } catch (EntityExistsException e) {
            return false;
        } catch (IllegalArgumentException e) {
            return false;
        } catch (TransactionRequiredException e) {
            return false;
        }
    }

    public List<User> getAllUsersPaged(int startIndex, int pageSize, int compId) {
        // Generate JPQL String which returns a list of users based on the given
        // offset and limit
        Query q = this.entityManager.createQuery("SELECT u FROM User u " + "INNER JOIN u.competitions c " + "WHERE c.competitionid = :comp");
        q.setHint("javax.persistence.cache.storeMode", "REFRESH");
        q.setFirstResult(startIndex);
        q.setMaxResults(pageSize);

        q.setParameter("comp", compId);

        List<User> users = Util.castList(User.class, q.getResultList());

        if (users != null && !users.isEmpty()) {
            return users;
        }
        return null;
    }

    public int getNumOfUsersOnCompetition(int compId) {
        Query q = this.entityManager.createQuery("SELECT u FROM User u " + "INNER JOIN u.competitions c " + "WHERE c.competitionid = :comp");
        q.setHint("javax.persistence.cache.storeMode", "REFRESH");
        q.setParameter("comp", compId);

        List<User> users = Util.castList(User.class, q.getResultList());

        if (users != null && !users.isEmpty()) {
            return users.size();
        }
        return 0;
    }

    public Admin retrieveAdmin(String email) {
        if (queryDBForAdmin("email", email)) {
            return this.currentAdmin;
        } else {
            return null;
        }
    }

    public void deleteUser(User user) {
        if (user.getUserid() > 0) {
            Query query = entityManager.createQuery("DELETE FROM Vm c WHERE c.user =?1");
            query.setParameter(1, user).executeUpdate();

            query = entityManager.createQuery("DELETE FROM User c WHERE c.userid =?1");
            query.setParameter(1, user.getUserid()).executeUpdate();

            query = entityManager.createQuery("DELETE FROM Address c WHERE c.addressid =?1");
            query.setParameter(1, user.getAddress().getAddressid()).executeUpdate();
        }
    }

    public boolean deleteCompetition(Competition competition) {
        try {
            Competition comp = entityManager.find(Competition.class, competition.getCompetitionid());
            entityManager.remove(comp);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean changeCompetition(BenchmarkManager bean) {
        Query q = this.entityManager.createQuery("UPDATE Competition bench SET bench.name=?1, bench.startdate =?2, bench.enddate=?3  WHERE bench.name=?4");
        q.setParameter(1, bean.getNewName());
        q.setParameter(2, bean.getNewStartDate());
        q.setParameter(3, bean.getNewEndDate());
        q.setParameter(4, bean.getCompetitionToChange());

        try {
            q.executeUpdate();
            return true;
        } catch (IllegalArgumentException e) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, e);
            return false;
        } catch (TransactionRequiredException e) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, e);
            return false;
        }
    }

    public void deleteVm(Vm vm) {
        Vm vmToDelete = entityManager.find(Vm.class, vm.getId());
        entityManager.remove(vmToDelete);
    }

    public boolean updateVm(Vm vm) {
        Query q = this.entityManager.createQuery("UPDATE Vm v SET v.dnsname=?1, v.username =?2, v.password=?3, v.os=?4, v.port=?5, v.protocol=?6, v.showInitKey=?7  WHERE v.id=?8");
        q.setParameter(1, vm.getDnsname());
        q.setParameter(2, vm.getUsername());
        q.setParameter(3, vm.getPassword());
        q.setParameter(4, vm.getOs());
        q.setParameter(5, vm.getPort());
        q.setParameter(6, vm.getProtocol());
        q.setParameter(7, vm.getShowInitKey());
        q.setParameter(8, vm.getId());

        try {
            q.executeUpdate();
            return true;
        } catch (IllegalArgumentException e) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, e);
            return false;
        } catch (TransactionRequiredException e) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, e);
            return false;
        }
    }

    public boolean resetSubmitStatus(User user) {
        if (user.getUniqueid().contains("_submitted")) {
            user.setUniqueid(user.getUniqueid().replaceAll("_submitted", ""));
            // Commit data to the database
            try {
                entityManager.merge(user);
                return true;
            } catch (IllegalArgumentException e) {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, e);
                return false;
            } catch (TransactionRequiredException e) {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, e);
                return false;
            }
        } else {
            return false;
        }
    }

    public Vm getVM(String vmDNSName) {
        Query q = this.entityManager.createQuery("SELECT c FROM Vm c Where c.dnsname=?1");
        q.setParameter(1, vmDNSName);

        List<Vm> vms = Util.castList(Vm.class, q.getResultList());
        if (vms != null && !vms.isEmpty()) {
            return vms.get(0);
        } else {
            return null;
        }
    }

    public boolean existUserResults(String userUniqueID) {
        Query q = this.entityManager.createNativeQuery("SELECT count(*) FROM regsystem_visceral.segmentation_results c Where c.PRTCPNT=?1");
        q.setParameter(1, userUniqueID);
        Long results = (Long) q.getSingleResult();
        if (results > 0) {
            return true;
        }
        return false;
    }

    public Long getMetricResultsCount(String userUniqueID, String metricShortName, int configuration, String modality, String region) {
        Query q = this.entityManager.createQuery("SELECT count(c) FROM SegmentationResult c Where c.prtcpnt=?1 and c.cnfig=?2 and c.region=?3 and c.mdltyNme=?4 and c." + metricShortName + ">0");
        q.setHint("javax.persistence.cache.storeMode", "REFRESH");
        q.setParameter(1, userUniqueID);
        q.setParameter(2, configuration);
        q.setParameter(3, region);
        q.setParameter(4, modality);

        Long count = (Long) q.getSingleResult();
        return count;
    }

    public Object getSingleOrganResultMedian(String participant, String organ, String modality, String region, int configuration, String metric, String timestamp) {
        Query q = this.entityManager.createNativeQuery("SELECT Avg(tmp." + metric + ") as MEDIAN FROM (SELECT inTab." + metric
                + ", @rows := @rows + 1 as rowNum FROM regsystem_visceral.segmentation_results as inTab,  (SELECT @rows := -1) as init where inTab.PRTCPNT='" + participant + "' and inTab.ORGAN=" + organ
                + " and inTab.region='" + region + "' and inTab.CNFIG=" + configuration + " and inTab.MDLTY_NME='" + modality + "' and inTab.TIMESTAMP='" + timestamp + "' ORDER BY inTab." + metric
                + ") as tmp WHERE tmp.rowNum in (Floor(@rows / 2), Ceil(@rows / 2));");

        Object cellValue = (Object) (q.getSingleResult() != null ? q.getSingleResult() : 0);
        return cellValue;
    }

    public Object getSingleOrganResultAvg(String participant, String organ, String modality, String region, int configuration, String metric, String timestamp) {
        Query q = this.entityManager.createNativeQuery("SELECT Avg(inTab." + metric + ") FROM regsystem_visceral.segmentation_results as inTab WHERE inTab.PRTCPNT='" + participant + "' and inTab.ORGAN=" + organ
                + " and inTab.region='" + region + "' and inTab.CNFIG=" + configuration + " and inTab.MDLTY_NME='" + modality + "' and inTab.TIMESTAMP='" + timestamp + "' and inTab." + metric + ">0;");
        Object cellValue = (Object) (q.getSingleResult() != null ? q.getSingleResult() : 0);
        return cellValue;
    }

    public Long getSingleOrganResultCount(String participant, String organ, String modality, String region, int configuration, String metric, String timestamp) {
        Query q = this.entityManager.createNativeQuery("SELECT count(inTab." + metric + ") FROM regsystem_visceral.segmentation_results as inTab WHERE inTab.PRTCPNT='" + participant + "' and inTab.ORGAN=" + organ
                + " and inTab.region='" + region + "' and inTab.CNFIG=" + configuration + " and inTab.MDLTY_NME='" + modality + "' and inTab.TIMESTAMP='" + timestamp + "';");
        Long cellValue = (Long) q.getSingleResult();
        return cellValue;
    }

    public Object getSingleOrganResultMax(String participant, String organ, String modality, String region, int configuration, String metric, String timestamp) {
        Query q = this.entityManager.createNativeQuery("SELECT max(inTab." + metric + ") FROM regsystem_visceral.segmentation_results as inTab WHERE inTab.PRTCPNT='" + participant + "' and inTab.ORGAN=" + organ
                + " and inTab.region='" + region + "' and inTab.CNFIG=" + configuration + " and inTab.MDLTY_NME='" + modality + "' and inTab.TIMESTAMP='" + timestamp + "';");
        Object cellValue = (Object) (q.getSingleResult() != null ? q.getSingleResult() : 0);
        return cellValue;
    }

    public Object getSingleOrganResultMin(String participant, String organ, String modality, String region, int configuration, String metric, String timestamp) {
        Query q = this.entityManager.createNativeQuery("SELECT min(inTab." + metric + ") FROM regsystem_visceral.segmentation_results as inTab WHERE inTab.PRTCPNT='" + participant + "' and inTab.ORGAN=" + organ
                + " and inTab.region='" + region + "' and inTab.CNFIG=" + configuration + " and inTab.MDLTY_NME='" + modality + "' and inTab.TIMESTAMP='" + timestamp + "';");
        Object cellValue = (Object) (q.getSingleResult() != null ? q.getSingleResult() : 0);
        return cellValue;
    }

    public Long getSingleOrganResultEmpty(String participant, String organ, String modality, String region, int configuration, String metric, String timestamp) {
        Query q = this.entityManager.createNativeQuery("SELECT count(inTab." + metric + ") FROM regsystem_visceral.segmentation_results as inTab WHERE inTab.PRTCPNT='" + participant + "' and inTab.ORGAN=" + organ
                + " and inTab.region='" + region + "' and inTab.CNFIG=" + configuration + " and inTab.MDLTY_NME='" + modality + "' and inTab.TIMESTAMP='" + timestamp + "' and inTab." + metric + "=0;");
        Long cellValue = (Long) q.getSingleResult();
        return cellValue;
    }

    public List<String> getAvailableModalities(String userUniqueID) {
        Query q = this.entityManager.createQuery("SELECT DISTINCT c.mdltyNme FROM SegmentationResult c Where c.prtcpnt=?1 order by c.mdltyNme asc");
        q.setHint("javax.persistence.cache.storeMode", "REFRESH");
        q.setParameter(1, userUniqueID);

        List<String> results = Util.castList(String.class, q.getResultList());
        return results;
    }

    public List<Integer> getAvailableConfigurations(String userUniqueID) {
        Query q = this.entityManager.createQuery("SELECT DISTINCT c.cnfig FROM SegmentationResult c Where c.prtcpnt=?1 order by c.cnfig asc");
        q.setHint("javax.persistence.cache.storeMode", "REFRESH");
        q.setParameter(1, userUniqueID);

        List<Integer> results = Util.castList(Integer.class, q.getResultList());
        return results;
    }

    public List<String> getAvailableRegions(String userUniqueID) {
        Query q = this.entityManager.createQuery("SELECT DISTINCT c.region FROM SegmentationResult c Where c.prtcpnt=?1 order by c.region asc");
        q.setHint("javax.persistence.cache.storeMode", "REFRESH");
        q.setParameter(1, userUniqueID);

        List<String> results = Util.castList(String.class, q.getResultList());
        return results;
    }

    public List<Timestamp> getAvailableSubmisions(String userUniqueID) {
        Query q = this.entityManager.createQuery("SELECT DISTINCT c.timestamp FROM SegmentationResult c Where c.prtcpnt=?1 order by c.timestamp asc");
        q.setHint("javax.persistence.cache.storeMode", "REFRESH");
        q.setParameter(1, userUniqueID);

        List<Timestamp> results = Util.castList(Timestamp.class, q.getResultList());
        return results;
    }

    public List<PublishedResult> getPublishedResults() {
        Query q = this.entityManager.createQuery("SELECT c FROM PublishedResult c");
        q.setHint("javax.persistence.cache.storeMode", "REFRESH");

        List<PublishedResult> results = Util.castList(PublishedResult.class, q.getResultList());
        return results;
        
    }
    
    public List<PublishedResult> getMaxDice() {
        Query q = this.entityManager.createQuery("SELECT c,max(c.dice) FROM PublishedResult c ");
        q.setHint("javax.persistence.cache.storeMode", "REFRESH");

        List<PublishedResult> results = Util.castList(PublishedResult.class, q.getResultList());
        return results;
        
    }
    public List<PublishedResult> getPublishedResults(String modality,String organID,String metric) {
        Query q = this.entityManager.createQuery("SELECT c FROM PublishedResult c WHERE c.modality=?1 and c.organname=?2 and c.metric=?3");
        q.setHint("javax.persistence.cache.storeMode", "REFRESH");
        q.setParameter(1, modality);
        q.setParameter(2, organID);
        q.setParameter(3, metric);

        List<PublishedResult> results = Util.castList(PublishedResult.class, q.getResultList());
        return results;
        
    }
    
    public PublishedResult checkExistingResults(String participant, Timestamp timestamp, String metric,String modality,String organID,String shortMetric,String cellValue) {
        Query q = this.entityManager.createQuery("SELECT c FROM PublishedResult c Where c.prtcpnt=?1 and c.timestamp=?2 and c.organname=?3 and c.modality=?4");
        q.setHint("javax.persistence.cache.storeMode", "REFRESH");
        q.setParameter(1, participant);
        q.setParameter(2, timestamp);
        q.setParameter(3, organID);
        q.setParameter(4, modality);
        
        try {
            Object returnValue = q.getSingleResult();
            return (PublishedResult) returnValue;
        } catch (Exception e) {
            return null;
        }
    }

    public String publishResult(User participant, String modality, String region, int configuration, String shortMetric, String longMetric, String organID, Timestamp timestamp) {
        DecimalFormat df = new DecimalFormat("#.###");
        String cellValue = df.format(this.getSingleOrganResultAvg(participant.getUniqueid(), organID, modality, region, configuration, shortMetric, timestamp.toString()));
<<<<<<< HEAD
        PublishedResult dbResponse = checkExistingResults(participant.getFirstname() + " " + participant.getLastname(), timestamp, longMetric,modality,organID,shortMetric.toLowerCase(),cellValue);
=======
        PublishedResult dbResponse = checkExistingResults(participant.getFirstname() + " " + participant.getLastname(), timestamp, longMetric);
>>>>>>> cfded8b5ee5ccf9b241b821e80661c128853c5ba
        if (dbResponse == null) {
            Query q = this.entityManager.createNativeQuery("INSERT INTO `regsystem_visceral`.`published_results` (`prtcpnt`, `affiliation`,`modality`, `"+shortMetric+"`, `organname`,`organvalue`, `TIMESTAMP`) VALUES (?1, ?2, ?3, ?4, ?5,?6,?7);");
            q.setParameter(1, participant.getFirstname() + " " + participant.getLastname());
            q.setParameter(2, participant.getAffiliation());
            q.setParameter(3, modality);
            q.setParameter(4, cellValue);
            q.setParameter(5, organID);
            q.setParameter(6, cellValue);
            q.setParameter(7, timestamp.toString());
            
            return q.executeUpdate() > 0 ? "success" : "failed";
        } else {
            Query q = this.entityManager.createNativeQuery("UPDATE `regsystem_visceral`.`published_results` SET `"+shortMetric+"`=?1 WHERE `id`=" + dbResponse.getId() + ";");
            q.setParameter(1, cellValue);
            return q.executeUpdate() > 0 ? "success" : "failed";
        }
    }

    public boolean deletePublishedResult(PublishedResult result) {
        try {
            PublishedResult inDBResult = entityManager.find(PublishedResult.class, result.getId());
            entityManager.remove(inDBResult);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean addResult(SegmentationResult result) {
        try {
            entityManager.persist(result);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    
    public boolean removeAllPublishedResults() {
        Query q = this.entityManager.createNativeQuery("DELETE FROM `regsystem_visceral`.`published_results`;");
        return q.executeUpdate() > 0;
    }
}