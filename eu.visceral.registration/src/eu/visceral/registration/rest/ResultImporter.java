package eu.visceral.registration.rest;

import java.sql.Timestamp;

import javax.faces.bean.RequestScoped;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.sun.jersey.api.core.InjectParam;

import eu.visceral.registration.ejb.eao.VisceralEAO;
import eu.visceral.registration.ejb.entity.SegmentationResult;
import eu.visceral.registration.util.measurement.Measurement;

@Path("/import")
@RequestScoped
public class ResultImporter {

    @InjectParam
    private VisceralEAO service;

    @POST
    @Consumes(MediaType.APPLICATION_XML)
    @Produces(MediaType.APPLICATION_JSON)
    public Response test(@QueryParam("u") String adminEmail, @QueryParam("p") String password, @QueryParam("timestamp") Long timestamp, @QueryParam("filename") String filename, Measurement measurement) {
        if (service.isAdminValid(adminEmail, password)) {
            Timestamp ts = new Timestamp(timestamp);
            String[] measurementProps = filename.split("_");

            if (measurementProps.length == 7) {
                SegmentationResult result = new SegmentationResult();

                result.setAcurcy(Float.parseFloat(measurement.getMetrics().getACURCY().getValue().toString()));
                result.setAdjrind(Float.parseFloat(measurement.getMetrics().getADJRIND().getValue().toString()));
                result.setAuc(Float.parseFloat(measurement.getMetrics().getAUC().getValue().toString()));
                result.setAvgdist(Float.parseFloat(measurement.getMetrics().getAVGDIST().getValue().toString()));
                result.setCnfig(Integer.parseInt(measurementProps[6]));
                result.setDice(Float.parseFloat(measurement.getMetrics().getDICE().getValue().toString()));
                result.setFallout(Float.parseFloat(measurement.getMetrics().getFALLOUT().getValue().toString()));
                result.setFmeasr(Float.parseFloat(measurement.getMetrics().getFMEASR().getValue().toString()));
                result.setGcoerr(Float.parseFloat(measurement.getMetrics().getGCOERR().getValue().toString()));
                result.setHdrfdst(Float.parseFloat(measurement.getMetrics().getHDRFDST().getValue().toString()));
                result.setIccorr(Float.parseFloat(measurement.getMetrics().getICCORR().getValue().toString()));
                result.setJacrd(Float.parseFloat(measurement.getMetrics().getJACRD().getValue().toString()));
                result.setKappa(Float.parseFloat(measurement.getMetrics().getKAPPA().getValue().toString()));
                result.setMahlnbs(Float.parseFloat(measurement.getMetrics().getMAHLNBS().getValue().toString()));
                result.setMdltyNme(measurementProps[2]);
                result.setMdltyNum(Integer.parseInt(measurementProps[1]));
                result.setMutinf(Float.parseFloat(measurement.getMetrics().getMUTINF().getValue().toString()));
                result.setOrgan(Integer.parseInt(measurementProps[4]));
                result.setPrcison(Float.parseFloat(measurement.getMetrics().getPRCISON().getValue().toString()));
                result.setProbdst(Float.parseFloat(measurement.getMetrics().getPROBDST().getValue().toString()));
                result.setPrtcpnt(measurementProps[5]);
                result.setPtient(Integer.parseInt(measurementProps[0]));
                result.setRegion(measurementProps[3]);
                result.setRndind(Float.parseFloat(measurement.getMetrics().getRNDIND().getValue().toString()));
                result.setSnsvty(Float.parseFloat(measurement.getMetrics().getSNSVTY().getValue().toString()));
                result.setSpcfty(Float.parseFloat(measurement.getMetrics().getSPCFTY().getValue().toString()));
                result.setTimestamp(ts);
                result.setVarinfo(Float.parseFloat(measurement.getMetrics().getVARINFO().getValue().toString()));
                result.setVolsmty(Float.parseFloat(measurement.getMetrics().getVOLSMTY().getValue().toString()));

                if (service.addResult(result)) {
                    return Response.status(200).entity("{\"success\":\"The measure was added on the database.\"}").build();
                } else {
                    return Response.status(404).entity("{\"error\":\"This measure (" + filename + ") wasn't added on the database. Please try again.\"}").build();
                }
            } else {
                return Response.status(404).entity("{\"error\":\"The filename doesn't contain the mandatory information\"}").build();
            }
        } else {
            return Response.status(403).entity("{\"error\":\"Unauthorized\"}").build();
        }
    }
}