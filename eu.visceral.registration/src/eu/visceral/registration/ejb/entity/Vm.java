package eu.visceral.registration.ejb.entity;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the vm database table.
 * 
 */
@Entity
@Table(name="vm")
public class Vm implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private int id;

	private String dnsname;

	private String os;

	private String password;

	private String port;

	private String protocol;

	@Column(name="show_init_key")
	private boolean showInitKey;

	private String username;

	//bi-directional many-to-one association to User
    @ManyToOne
	private User user;

    public Vm() {
    }

	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getDnsname() {
		return this.dnsname;
	}

	public void setDnsname(String dnsname) {
		this.dnsname = dnsname;
	}

	public String getOs() {
		return this.os;
	}

	public void setOs(String os) {
		this.os = os;
	}

	public String getPassword() {
		return this.password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getPort() {
		return this.port;
	}

	public void setPort(String port) {
		this.port = port;
	}

	public String getProtocol() {
		return this.protocol;
	}

	public void setProtocol(String protocol) {
		this.protocol = protocol;
	}

	public boolean getShowInitKey() {
		return this.showInitKey;
	}

	public void setShowInitKey(boolean showInitKey) {
		this.showInitKey = showInitKey;
	}

	public String getUsername() {
		return this.username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public User getUser() {
		return this.user;
	}

	public void setUser(User user) {
		this.user = user;
	}
	
}