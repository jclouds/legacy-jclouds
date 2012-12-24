package org.jclouds.joyent.cloudapi.v6_5.features;

import java.util.Set;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;

import org.jclouds.http.filters.BasicAuthentication;
import org.jclouds.rest.annotations.ExceptionParser;
import org.jclouds.rest.annotations.Headers;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.functions.ReturnEmptySetOnNotFoundOr404;
import org.jclouds.rest.functions.ReturnNullOnNotFoundOr404;

import com.google.common.util.concurrent.ListenableFuture;

/**
 * Provides asynchronous access to Dataset via their REST API.
 * <p/>
 * 
 * @author Gerald Pereira
 * @see PackageApi
 * @see <a href="http://apidocs.joyent.com/sdcapidoc/cloudapi/index.html#packages">api doc</a>
 */
@Headers(keys = "X-Api-Version", values = "{jclouds.api-version}")
@RequestFilters(BasicAuthentication.class)
public interface PackageAsyncApi {
   /**
    * @see PackageApi#list
    */
   @GET
   @Path("/my/packages")
   @Consumes(MediaType.APPLICATION_JSON)
   @ExceptionParser(ReturnEmptySetOnNotFoundOr404.class)
   ListenableFuture<Set<org.jclouds.joyent.cloudapi.v6_5.domain.Package>> list();

   /**
    * @see PackageApi#get
    */
   @GET
   @Path("/my/packages/{name}")
   @Consumes(MediaType.APPLICATION_JSON)
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   ListenableFuture<org.jclouds.joyent.cloudapi.v6_5.domain.Package> get(@PathParam("name") String name);
}
