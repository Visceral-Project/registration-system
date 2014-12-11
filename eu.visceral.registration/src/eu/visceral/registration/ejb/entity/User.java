package eu.visceral.registration.ejb.entity;

import java.io.Serializable;
import javax.persistence.*;
import java.util.List;


/**
 * The persistent class for the user database table.
 * 
 */
@Entity
@Table(name="user")
public class User implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private int userid;

	private String affiliation;

	@Column(name="copyright_signed")
	private boolean copyrightSigned;

	private String email;

	private String firstname;

	@Column(name="forgot_password_id")
	private String forgotPasswordId;

	private String lastname;

	private String password;

	@Column(name="pdf_uploaded")
	private boolean pdfUploaded;

	private String phone;

	private String preferance;

	private String uniqueid;

	//bi-directional many-to-one association to Address
	@ManyToOne(cascade={CascadeType.ALL})
	private Address address;

	//bi-directional many-to-many association to Competition
	@ManyToMany(cascade={CascadeType.ALL})
	@JoinTable(
		name="user_has_competition"
		, joinColumns={
			@JoinColumn(name="user_userid")
			}
		, inverseJoinColumns={
			@JoinColumn(name="competition_competitionid")
			}
		)
	private List<Competition> competitions;

	//bi-directional many-to-one association to Vm
	@OneToMany(mappedBy="user")
	private List<Vm> vms;

    public User() {
    }

	public int getUserid() {
		return this.userid;
	}

	public void setUserid(int userid) {
		this.userid = userid;
	}

	public String getAffiliation() {
		return this.affiliation;
	}

	public void setAffiliation(String affiliation) {
		this.affiliation = affiliation;
	}

	public boolean getCopyrightSigned() {
		return this.copyrightSigned;
	}

	public void setCopyrightSigned(boolean copyrightSigned) {
		this.copyrightSigned = copyrightSigned;
	}

	public String getEmail() {
		return this.email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getFirstname() {
		return this.firstname;
	}

	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}

	public String getForgotPasswordId() {
		return this.forgotPasswordId;
	}

	public void setForgotPasswordId(String forgotPasswordId) {
		this.forgotPasswordId = forgotPasswordId;
	}

	public String getLastname() {
		return this.lastname;
	}

	public void setLastname(String lastname) {
		this.lastname = lastname;
	}

	public String getPassword() {
		return this.password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public boolean getPdfUploaded() {
		return this.pdfUploaded;
	}

	public void setPdfUploaded(boolean pdfUploaded) {
		this.pdfUploaded = pdfUploaded;
	}

	public String getPhone() {
		return this.phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getPreferance() {
		return this.preferance;
	}

	public void setPreferance(String preferance) {
		this.preferance = preferance;
	}

	public String getUniqueid() {
		return this.uniqueid;
	}

	public void setUniqueid(String uniqueid) {
		this.uniqueid = uniqueid;
	}

	public Address getAddress() {
		return this.address;
	}

	public void setAddress(Address address) {
		this.address = address;
	}
	
	public List<Competition> getCompetitions() {
		return this.competitions;
	}

	public void setCompetitions(List<Competition> competitions) {
		this.competitions = competitions;
	}
	
	public List<Vm> getVms() {
		return this.vms;
	}

	public void setVms(List<Vm> vms) {
		this.vms = vms;
	}
	
}