package org.jclouds.joyent.cloudapi.v6_5.features;

import java.util.Set;

import javax.inject.Named;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;

import org.jclouds.Fallbacks.EmptySetOnNotFoundOr404;
import org.jclouds.Fallbacks.NullOnNotFoundOr404;
import org.jclouds.Fallbacks.VoidOnNotFoundOr404;
import org.jclouds.http.filters.BasicAuthentication;
import org.jclouds.joyent.cloudapi.v6_5.domain.Key;
import org.jclouds.rest.annotations.BinderParam;
import org.jclouds.rest.annotations.Fallback;
import org.jclouds.rest.annotations.Headers;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.binders.BindToJsonPayload;

import com.google.common.util.concurrent.ListenableFuture;

/**
 * @author Adrian Cole
 * @see KeyApi
 * @see <a href="http://apidocs.joyent.com/sdcapidoc/cloudapi/index.html#keys">api doc</a>
 */
@Headers(keys = "X-Api-Version", values = "{jclouds.api-version}")
@RequestFilters(BasicAuthentication.class)
public interface KeyAsyncApi {
   /**
    * @see KeyApi#list
    */
   @Named("ListKeys")
   @GET
   @Path("/my/keys")
   @Consumes(MediaType.APPLICATION_JSON)
   @Fallback(EmptySetOnNotFoundOr404.class)
   ListenableFuture<Set<Key>> list();

   /**
    * @see KeyApi#get
    */
   @Named("GetKey")
   @GET
   @Path("/my/keys/{name}")
   @Consumes(MediaType.APPLICATION_JSON)
   @Fallback(NullOnNotFoundOr404.class)
   ListenableFuture<Key> get(@PathParam("name") String name);
   
   /**
    * @see KeyApi#create
    */
   @Named("CreateKey")
   @POST
   @Path("/my/keys")
   @Consumes(MediaType.APPLICATION_JSON)
   ListenableFuture<Key> create(@BinderParam(BindToJsonPayload.class) Key key);
   
   /**
    * @see KeyApi#delete
    */
   @Named("DeleteKey")
   @DELETE
   @Consumes(MediaType.APPLICATION_JSON)
   @Path("/my/keys/{name}")
   @Fallback(VoidOnNotFoundOr404.class)
   ListenableFuture<Void> delete(@PathParam("name") String name);
   
}
