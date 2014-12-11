package eu.visceral.registration.ejb.entity;

import java.io.Serializable;
import javax.persistence.*;
import java.sql.Timestamp;


/**
 * The persistent class for the segmentation_results database table.
 * 
 */
@Entity
@Table(name="segmentation_results")
public class SegmentationResult implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="idsegmentation_results")
	private int idsegmentationResults;

	@Column(name="ACURCY")
	private float acurcy;

	@Column(name="ADJRIND")
	private float adjrind;

	@Column(name="AUC")
	private float auc;

	@Column(name="AVGDIST")
	private float avgdist;

	@Column(name="CNFIG")
	private int cnfig;

	@Column(name="DICE")
	private float dice;

	@Column(name="FALLOUT")
	private float fallout;

	@Column(name="FMEASR")
	private float fmeasr;

	@Column(name="GCOERR")
	private float gcoerr;

	@Column(name="HDRFDST")
	private float hdrfdst;

	@Column(name="ICCORR")
	private float iccorr;

	@Column(name="JACRD")
	private float jacrd;

	@Column(name="KAPPA")
	private float kappa;

	@Column(name="MAHLNBS")
	private float mahlnbs;

	@Column(name="MDLTY_NME")
	private String mdltyNme;

	@Column(name="MDLTY_NUM")
	private int mdltyNum;

	@Column(name="MUTINF")
	private float mutinf;

	@Column(name="ORGAN")
	private int organ;

	@Column(name="PRCISON")
	private float prcison;

	@Column(name="PROBDST")
	private float probdst;

	@Column(name="PRTCPNT")
	private String prtcpnt;

	@Column(name="PTIENT")
	private int ptient;

	@Column(name="REGION")
	private String region;

	@Column(name="RNDIND")
	private float rndind;

	@Column(name="SNSVTY")
	private float snsvty;

	@Column(name="SPCFTY")
	private float spcfty;

	@Column(name="TIMESTAMP")
	private Timestamp timestamp;

	@Column(name="VARINFO")
	private float varinfo;

	@Column(name="VOLSMTY")
	private float volsmty;

    public SegmentationResult() {
    }

	public int getIdsegmentationResults() {
		return this.idsegmentationResults;
	}

	public void setIdsegmentationResults(int idsegmentationResults) {
		this.idsegmentationResults = idsegmentationResults;
	}

	public float getAcurcy() {
		return this.acurcy;
	}

	public void setAcurcy(float acurcy) {
		this.acurcy = acurcy;
	}

	public float getAdjrind() {
		return this.adjrind;
	}

	public void setAdjrind(float adjrind) {
		this.adjrind = adjrind;
	}

	public float getAuc() {
		return this.auc;
	}

	public void setAuc(float auc) {
		this.auc = auc;
	}

	public float getAvgdist() {
		return this.avgdist;
	}

	public void setAvgdist(float avgdist) {
		this.avgdist = avgdist;
	}

	public int getCnfig() {
		return this.cnfig;
	}

	public void setCnfig(int cnfig) {
		this.cnfig = cnfig;
	}

	public float getDice() {
		return this.dice;
	}

	public void setDice(float dice) {
		this.dice = dice;
	}

	public float getFallout() {
		return this.fallout;
	}

	public void setFallout(float fallout) {
		this.fallout = fallout;
	}

	public float getFmeasr() {
		return this.fmeasr;
	}

	public void setFmeasr(float fmeasr) {
		this.fmeasr = fmeasr;
	}

	public float getGcoerr() {
		return this.gcoerr;
	}

	public void setGcoerr(float gcoerr) {
		this.gcoerr = gcoerr;
	}

	public float getHdrfdst() {
		return this.hdrfdst;
	}

	public void setHdrfdst(float hdrfdst) {
		this.hdrfdst = hdrfdst;
	}

	public float getIccorr() {
		return this.iccorr;
	}

	public void setIccorr(float iccorr) {
		this.iccorr = iccorr;
	}

	public float getJacrd() {
		return this.jacrd;
	}

	public void setJacrd(float jacrd) {
		this.jacrd = jacrd;
	}

	public float getKappa() {
		return this.kappa;
	}

	public void setKappa(float kappa) {
		this.kappa = kappa;
	}

	public float getMahlnbs() {
		return this.mahlnbs;
	}

	public void setMahlnbs(float mahlnbs) {
		this.mahlnbs = mahlnbs;
	}

	public String getMdltyNme() {
		return this.mdltyNme;
	}

	public void setMdltyNme(String mdltyNme) {
		this.mdltyNme = mdltyNme;
	}

	public int getMdltyNum() {
		return this.mdltyNum;
	}

	public void setMdltyNum(int mdltyNum) {
		this.mdltyNum = mdltyNum;
	}

	public float getMutinf() {
		return this.mutinf;
	}

	public void setMutinf(float mutinf) {
		this.mutinf = mutinf;
	}

	public int getOrgan() {
		return this.organ;
	}

	public void setOrgan(int organ) {
		this.organ = organ;
	}

	public float getPrcison() {
		return this.prcison;
	}

	public void setPrcison(float prcison) {
		this.prcison = prcison;
	}

	public float getProbdst() {
		return this.probdst;
	}

	public void setProbdst(float probdst) {
		this.probdst = probdst;
	}

	public String getPrtcpnt() {
		return this.prtcpnt;
	}

	public void setPrtcpnt(String prtcpnt) {
		this.prtcpnt = prtcpnt;
	}

	public int getPtient() {
		return this.ptient;
	}

	public void setPtient(int ptient) {
		this.ptient = ptient;
	}

	public String getRegion() {
		return this.region;
	}

	public void setRegion(String region) {
		this.region = region;
	}

	public float getRndind() {
		return this.rndind;
	}

	public void setRndind(float rndind) {
		this.rndind = rndind;
	}

	public float getSnsvty() {
		return this.snsvty;
	}

	public void setSnsvty(float snsvty) {
		this.snsvty = snsvty;
	}

	public float getSpcfty() {
		return this.spcfty;
	}

	public void setSpcfty(float spcfty) {
		this.spcfty = spcfty;
	}

	public Timestamp getTimestamp() {
		return this.timestamp;
	}

	public void setTimestamp(Timestamp timestamp) {
		this.timestamp = timestamp;
	}

	public float getVarinfo() {
		return this.varinfo;
	}

	public void setVarinfo(float varinfo) {
		this.varinfo = varinfo;
	}

	public float getVolsmty() {
		return this.volsmty;
	}

	public void setVolsmty(float volsmty) {
		this.volsmty = volsmty;
	}

}