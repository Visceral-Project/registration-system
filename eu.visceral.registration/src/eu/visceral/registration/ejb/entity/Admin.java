package eu.visceral.registration.ejb.entity;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the admin database table.
 * 
 */
@Entity
@Table(name="admin")
public class Admin implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private int id;

	private String affiliation;

	private String email;

	private String firstname;

	@Column(name="forgot_password_id")
	private String forgotPasswordId;

	@Column(name="get_conf_emails")
	private boolean getConfEmails;

	private String lastname;

	private String password;

	private String phone;

	//bi-directional many-to-one association to Group
	@ManyToOne(cascade={CascadeType.ALL})
	@JoinColumn(name="groups_id")
	private Group group;

    public Admin() {
    }

	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getAffiliation() {
		return this.affiliation;
	}

	public void setAffiliation(String affiliation) {
		this.affiliation = affiliation;
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

	public boolean getGetConfEmails() {
		return this.getConfEmails;
	}

	public void setGetConfEmails(boolean getConfEmails) {
		this.getConfEmails = getConfEmails;
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

	public String getPhone() {
		return this.phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public Group getGroup() {
		return this.group;
	}

	public void setGroup(Group group) {
		this.group = group;
	}
	
}