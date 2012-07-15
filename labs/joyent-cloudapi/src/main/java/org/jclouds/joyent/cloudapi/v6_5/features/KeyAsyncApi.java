package org.jclouds.joyent.cloudapi.v6_5.features;

import java.util.Set;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;

import org.jclouds.http.filters.BasicAuthentication;
import org.jclouds.joyent.cloudapi.v6_5.binders.BindKeyToJsonPayload;
import org.jclouds.joyent.cloudapi.v6_5.domain.Key;
import org.jclouds.rest.annotations.BinderParam;
import org.jclouds.rest.annotations.ExceptionParser;
import org.jclouds.rest.annotations.Headers;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.SkipEncoding;
import org.jclouds.rest.functions.ReturnEmptySetOnNotFoundOr404;
import org.jclouds.rest.functions.ReturnNullOnNotFoundOr404;

import com.google.common.util.concurrent.ListenableFuture;

/**
 * @author Adrian Cole
 * @see KeyApi
 * @see <a href="http://apidocs.joyent.com/cloudApiapidoc/cloudapi/#keys">api doc</a>
 */
@SkipEncoding({ '/', '=' })
@Headers(keys = "X-Api-Version", values = "{jclouds.api-version}")
@RequestFilters(BasicAuthentication.class)
public interface KeyAsyncApi {
   /**
    * @see KeyApi#list
    */
   @GET
   @Path("/my/keys")
   @Consumes(MediaType.APPLICATION_JSON)
   @ExceptionParser(ReturnEmptySetOnNotFoundOr404.class)
   ListenableFuture<Set<Key>> list();

   /**
    * @see KeyApi#get
    */
   @GET
   @Path("/my/keys/{name}")
   @Consumes(MediaType.APPLICATION_JSON)
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   ListenableFuture<Key> get(@PathParam("name") String name);
   
   /**
    * @see KeyApi#create
    */
   @POST
   @Path("/my/keys")
   @Consumes(MediaType.APPLICATION_JSON)
   ListenableFuture<Key> create(@BinderParam(BindKeyToJsonPayload.class) Key key);
   
   /**
    * @see KeyApi#delete
    */
   @DELETE
   @Consumes(MediaType.APPLICATION_JSON)
   @Path("/my/keys/{name}")
   ListenableFuture<Void> delete(@PathParam("name") String name);
   
}
