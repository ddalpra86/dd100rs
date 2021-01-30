package org.dalpra.acme.rest.json.resources;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.dalpra.acme.rest.json.Legume;
import org.jboss.logging.Logger;
import org.jboss.resteasy.annotations.jaxrs.PathParam;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import io.quarkus.panache.common.Sort;

@Path("/legumes")
@ApplicationScoped
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class LegumeResource {

	private static final Logger LOGGER = Logger.getLogger(FruitResource.class.getName());

    @GET
    public List<Legume> get() {
        return Legume.listAll(Sort.by("name"));
    }

    @GET
    @Path("{id}")
    public Legume getSingle(@PathParam Long id) {
    	Legume entity = Legume.findById(id);
        if (entity == null) {
            throw new WebApplicationException("Legume with id of " + id + " does not exist.", 404);
        }
        return entity;
    }
    

    @POST
    @Transactional
    public Response create(Legume legume) {
        if (legume.id != null) {
            throw new WebApplicationException("Id was invalidly set on request.", 422);
        }

        legume.persist();
        return Response.ok(legume).status(201).build();
    }
    
    @PUT
    @Path("{id}")
    @Transactional
    public Legume update(@PathParam Long id, Legume legume) {
        if (legume.name == null) {
            throw new WebApplicationException("Legume Name was not set on request.", 422);
        }

        Legume entity = Legume.findById(id);

        if (entity == null) {
            throw new WebApplicationException("Legume with id of " + id + " does not exist.", 404);
        }

        entity.name = legume.name;

        return entity;
    }

    @DELETE
    @Path("{id}")
    @Transactional
    public Response delete(@PathParam Long id) {
    	Legume entity = Legume.findById(id);
        if (entity == null) {
            throw new WebApplicationException("Legume with id of " + id + " does not exist.", 404);
        }
        entity.delete();
        return Response.status(204).build();
    }
    
    @Provider
    public static class ErrorMapper implements ExceptionMapper<Exception> {

        @Inject
        ObjectMapper objectMapper;

        @Override
        public Response toResponse(Exception exception) {
            LOGGER.error("Failed to handle request", exception);

            int code = 500;
            if (exception instanceof WebApplicationException) {
                code = ((WebApplicationException) exception).getResponse().getStatus();
            }

            ObjectNode exceptionJson = objectMapper.createObjectNode();
            exceptionJson.put("exceptionType", exception.getClass().getName());
            exceptionJson.put("code", code);

            if (exception.getMessage() != null) {
                exceptionJson.put("error", exception.getMessage());
            }

            return Response.status(code)
                    .entity(exceptionJson)
                    .build();
        }

    }
}