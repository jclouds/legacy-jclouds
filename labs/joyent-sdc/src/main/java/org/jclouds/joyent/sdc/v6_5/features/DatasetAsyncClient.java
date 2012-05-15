package org.jclouds.joyent.sdc.v6_5.features;

import java.util.Set;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;

import org.jclouds.http.filters.BasicAuthentication;
import org.jclouds.joyent.sdc.v6_5.domain.Dataset;
import org.jclouds.rest.annotations.ExceptionParser;
import org.jclouds.rest.annotations.Headers;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.SkipEncoding;
import org.jclouds.rest.functions.ReturnEmptySetOnNotFoundOr404;
import org.jclouds.rest.functions.ReturnNullOnNotFoundOr404;

import com.google.common.util.concurrent.ListenableFuture;

/**
 * Provides asynchronous access to Dataset via their REST API.
 * <p/>
 * 
 * @author Gerald Pereira
 * @see DatasetClient
 * @see <a href="http://apidocs.joyent.com/sdcapidoc/cloudapi">api doc</a>
 */
@SkipEncoding({ '/', '=' })
@Headers(keys = "X-Api-Version", values = "{jclouds.api-version}")
@RequestFilters(BasicAuthentication.class)
public interface DatasetAsyncClient {
   /**
    * @see DatasetClient#listMachines
    */
   @GET
   @Path("/my/datasets")
   @Consumes(MediaType.APPLICATION_JSON)
   @ExceptionParser(ReturnEmptySetOnNotFoundOr404.class)
   ListenableFuture<Set<Dataset>> listDatasets();

   /**
    * @see DatasetClient#getMachineDetails
    */
   @GET
   @Path("/my/datasets/{id}")
   @Consumes(MediaType.APPLICATION_JSON)
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   ListenableFuture<Dataset> getDataset(@PathParam("id") String id);
}
