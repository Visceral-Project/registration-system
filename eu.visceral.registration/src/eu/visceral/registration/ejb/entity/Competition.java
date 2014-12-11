package eu.visceral.registration.ejb.entity;

import java.io.Serializable;
import javax.persistence.*;
import java.util.Date;
import java.util.List;


/**
 * The persistent class for the competition database table.
 * 
 */
@Entity
@Table(name="competition")
public class Competition implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private int competitionid;

    @Temporal( TemporalType.DATE)
	private Date enddate;

	private String name;

    @Temporal( TemporalType.DATE)
	private Date startdate;

	//bi-directional many-to-many association to User
	@ManyToMany(mappedBy="competitions", cascade={CascadeType.ALL})
	private List<User> users;

    public Competition() {
    }

	public int getCompetitionid() {
		return this.competitionid;
	}

	public void setCompetitionid(int competitionid) {
		this.competitionid = competitionid;
	}

	public Date getEnddate() {
		return this.enddate;
	}

	public void setEnddate(Date enddate) {
		this.enddate = enddate;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Date getStartdate() {
		return this.startdate;
	}

	public void setStartdate(Date startdate) {
		this.startdate = startdate;
	}

	public List<User> getUsers() {
		return this.users;
	}

	public void setUsers(List<User> users) {
		this.users = users;
	}
	
}