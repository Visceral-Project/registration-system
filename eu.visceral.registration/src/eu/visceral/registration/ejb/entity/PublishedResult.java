package eu.visceral.registration.ejb.entity;

import java.io.Serializable;
import javax.persistence.*;
import java.sql.Timestamp;


/**
 * The persistent class for the published_results database table.
 * 
 */
@Entity
@Table(name="published_results")
public class PublishedResult implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private int id;

	private String affiliation;

	private String modality;
	
	private String metric;
	
	private String organname;
	
	private float organvalue;

	/**private float organ1247;

	private float organ1302;

	private float organ1326;

	private float organ170;

	private float organ187;

	private float organ237;

	private float organ2473;

	private float organ29193;

	private float organ29662;

	private float organ29663;

	private float organ30324;

	private float organ30325;

	private float organ32248;

	private float organ32249;

	private float organ40357;

	private float organ40358;

	private float organ480;

	private float organ58;

	private float organ7578;

	private float organ86;**/
	
	private String dice;
	
	private String adjrind;
	
	private String iccorr;
	
	private String avgdist;
	
	private String prtcpnt;

	@Column(name="TIMESTAMP")
	private Timestamp timestamp;

    public PublishedResult() {
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
	
	public String getModality() {
        return this.modality;
    }
    
	public String getMetric() {
		return this.metric;
	}

	public void setMetric(String metric) {
		this.metric = metric;
	}

	/**public float getOrgan1247() {
		return this.organ1247;
	}

	public void setOrgan1247(float organ1247) {
		this.organ1247 = organ1247;
	}

	public float getOrgan1302() {
		return this.organ1302;
	}

	public void setOrgan1302(float organ1302) {
		this.organ1302 = organ1302;
	}

	public float getOrgan1326() {
		return this.organ1326;
	}

	public void setOrgan1326(float organ1326) {
		this.organ1326 = organ1326;
	}

	public float getOrgan170() {
		return this.organ170;
	}

	public void setOrgan170(float organ170) {
		this.organ170 = organ170;
	}

	public float getOrgan187() {
		return this.organ187;
	}

	public void setOrgan187(float organ187) {
		this.organ187 = organ187;
	}

	public float getOrgan237() {
		return this.organ237;
	}

	public void setOrgan237(float organ237) {
		this.organ237 = organ237;
	}

	public float getOrgan2473() {
		return this.organ2473;
	}

	public void setOrgan2473(float organ2473) {
		this.organ2473 = organ2473;
	}

	public float getOrgan29193() {
		return this.organ29193;
	}

	public void setOrgan29193(float organ29193) {
		this.organ29193 = organ29193;
	}

	public float getOrgan29662() {
		return this.organ29662;
	}

	public void setOrgan29662(float organ29662) {
		this.organ29662 = organ29662;
	}

	public float getOrgan29663() {
		return this.organ29663;
	}

	public void setOrgan29663(float organ29663) {
		this.organ29663 = organ29663;
	}

	public float getOrgan30324() {
		return this.organ30324;
	}

	public void setOrgan30324(float organ30324) {
		this.organ30324 = organ30324;
	}

	public float getOrgan30325() {
		return this.organ30325;
	}

	public void setOrgan30325(float organ30325) {
		this.organ30325 = organ30325;
	}

	public float getOrgan32248() {
		return this.organ32248;
	}

	public void setOrgan32248(float organ32248) {
		this.organ32248 = organ32248;
	}

	public float getOrgan32249() {
		return this.organ32249;
	}

	public void setOrgan32249(float organ32249) {
		this.organ32249 = organ32249;
	}

	public float getOrgan40357() {
		return this.organ40357;
	}

	public void setOrgan40357(float organ40357) {
		this.organ40357 = organ40357;
	}

	public float getOrgan40358() {
		return this.organ40358;
	}

	public void setOrgan40358(float organ40358) {
		this.organ40358 = organ40358;
	}

	public float getOrgan480() {
		return this.organ480;
	}

	public void setOrgan480(float organ480) {
		this.organ480 = organ480;
	}

	public float getOrgan58() {
		return this.organ58;
	}

	public void setOrgan58(float organ58) {
		this.organ58 = organ58;
	}

	public float getOrgan7578() {
		return this.organ7578;
	}

	public void setOrgan7578(float organ7578) {
		this.organ7578 = organ7578;
	}

	public float getOrgan86() {
		return this.organ86;
	}

	public void setOrgan86(float organ86) {
		this.organ86 = organ86;
	}**/

	public String getPrtcpnt() {
		return this.prtcpnt;
	}

	public void setPrtcpnt(String prtcpnt) {
		this.prtcpnt = prtcpnt;
	}

	public Timestamp getTimestamp() {
		return this.timestamp;
	}

	public void setTimestamp(Timestamp timestamp) {
		this.timestamp = timestamp;
	}

    /**
     * @return the organname
     */
    public String getOrganname() {
        return organname;
    }

    /**
     * @param organname the organname to set
     */
    public void setOrganname(String organname) {
        this.organname = organname;
    }

    /**
     * @return the organvalue
     */
    public float getOrganvalue() {
        return organvalue;
    }

    /**
     * @param organvalue the organvalue to set
     */
    public void setOrganvalue(float organvalue) {
        this.organvalue = organvalue;
    }

    /**
     * @return the dice
     */
    public String getDice() {
        return dice;
    }

    /**
     * @param dice the dice to set
     */
    public void setDice(String dice) {
        this.dice = dice;
    }

    /**
     * @return the avgdist
     */
    public String getAvgdist() {
        return avgdist;
    }

    /**
     * @param avgdist the avgdist to set
     */
    public void setAvgdist(String avgdist) {
        this.avgdist = avgdist;
    }

    /**
     * @return the adjrind
     */
    public String getAdjrind() {
        return adjrind;
    }

    /**
     * @param adjrind the adjrind to set
     */
    public void setAdjrind(String adjrind) {
        this.adjrind = adjrind;
    }

    /**
     * @return the iccorr
     */
    public String getIccorr() {
        return iccorr;
    }

    /**
     * @param iccorr the iccorr to set
     */
    public void setIccorr(String iccorr) {
        this.iccorr = iccorr;
    }

    
    
}