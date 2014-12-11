package eu.visceral.registration.rest;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.faces.bean.RequestScoped;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.sun.jersey.api.core.InjectParam;

import eu.visceral.registration.ejb.eao.VisceralEAO;
import eu.visceral.registration.ejb.entity.Vm;
import eu.visceral.registration.managedbeans.SendEmail;

/*
 * Plain old Java Object it does not extend as class or implements an interface
 * 
 * The class registers its methods for the HTTP GET request using the @GET annotation. 
 * Using the @Produces annotation, it defines that it can deliver several MIME types,
 * text, XML and HTML.
 *
 *The browser requests per default the HTML MIME type.
 */

@Path("/resetvm")
@RequestScoped
public class ResetVMSubmission {

    @InjectParam
    private VisceralEAO service;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response resetStatus(@QueryParam("id") String id) throws IOException {
        if (id != null && !id.isEmpty()) {
            Vm vm = service.getVM(id);
            if (vm != null && service.resetSubmitStatus(vm.getUser())) {
                String url = "https://visceral.eu:8181/VisceralVMService/vm/grantaccess/" + id;
                URL obj = new URL(url);
                HttpURLConnection con = (HttpURLConnection) obj.openConnection();
                con.setRequestMethod("GET");

                SendEmail mail = new SendEmail();
                mail.sendVmReset(vm.getUser());

                return Response.status(200).entity("{\"success\":\"The submission status has been reset and the user has been notified\"}").build();
            } else {
                return Response.status(404).entity("{\"error\":\"VM isn't submitted or doesn't exist on the database\"}").build();
            }
        } else {
            return Response.status(404).entity("{\"error\":\"The VM DNS Name wasn't provided\"}").build();
        }
    }
}