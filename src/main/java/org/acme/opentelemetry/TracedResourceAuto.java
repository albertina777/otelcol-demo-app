package org.acme.opentelemetry;

import java.net.MalformedURLException;
import java.net.URL;

import org.eclipse.microprofile.rest.client.RestClientBuilder;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import io.quarkus.logging.Log;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;

@Path("/")
@RegisterRestClient
public class TracedResourceAuto {

    @Context
    private UriInfo uriInfo;

    @GET
    @Path("sayHello/{name}")
    @Produces(MediaType.TEXT_PLAIN)
    public String sayHello(@PathParam("name") String name) {
       // Log.info("sayhello: " + name);
       String response;
       try {
           response = "sayHello from" + name;
           
       } catch (Exception e) {
           response=e.getMessage();
          
       }             
     return response;
    }

    @GET
    @Path("sayRemote/{name}")
    @Produces(MediaType.TEXT_PLAIN)
    public String sayRemote(@PathParam("name") String name,
            @Context final Request request,
            @Context final UriInfo ui,
            @Context UriInfo uriInfo) {
       // Log.info("sayRemote: " + name);

     
        String serviceName = System.getenv("SERVICE_NAME");
        if (serviceName == null) {
            serviceName = uriInfo.getBaseUri().toString();
        }
        
       // Log.info("Uri: " + uriInfo.getBaseUri());
        uriInfo.getRequestUri().getHost();
                
        //Log.info(serviceName);
        URL myURL = null;
        try {
            myURL = new URL(serviceName);
        } catch (MalformedURLException e) {
            // Auto-generated catch block
            e.printStackTrace();
        }

        ResourceClient resourceClient = RestClientBuilder.newBuilder()
                .baseUrl(myURL)
                //.baseUri(uriInfo.getBaseUri())
                .build(ResourceClient.class);

        String response;
        try {
            response = resourceClient.hello(name) + " from " + serviceName;
            
        } catch (Exception e) {
            response=e.getMessage();
            response = response + "\n \nYou can set SERVICE_NAME in your environment with the correct URL ";
            response = response + "e.g. https://"+ uriInfo.getRequestUri().getHost();
        }                
         
        
        return response;
    }

    private String formatGreeting(String name) {
  
        return name;
    }
    
    @GET
    @Path("hello")
    @Produces(MediaType.TEXT_PLAIN)
    public String hello() {
        //Log.info(System.getenv("OTELCOL_SERVER"));
        //Log.info("hello");
        return "hello";
    }
 
    @GET
    @Path("hello/{name}")
    @Produces(MediaType.TEXT_PLAIN)
    public String hello(@PathParam("name") String name) {
        //Log.info("hello: " + name);
    
        return "hello: " + name;
    }

    @GET
    @Path("chain")
    @Produces(MediaType.TEXT_PLAIN)
    public String chain() {
        //Log.info("Uri: " + uriInfo.getBaseUri());
        ResourceClient resourceClient = RestClientBuilder.newBuilder()
                .baseUri(uriInfo.getBaseUri())
                .build(ResourceClient.class);
        return "chain -> " + resourceClient.hello("test");
    }
}
