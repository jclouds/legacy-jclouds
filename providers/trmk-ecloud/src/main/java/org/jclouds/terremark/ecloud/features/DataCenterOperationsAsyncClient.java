package org.jclouds.terremark.ecloud.features;

import static org.jclouds.vcloud.terremark.TerremarkECloudMediaType.DATACENTERSLIST_XML;

import java.net.URI;
import java.util.Set;

import javax.annotation.Nullable;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;

import org.jclouds.rest.annotations.EndpointParam;
import org.jclouds.rest.annotations.ExceptionParser;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.XMLResponseParser;
import org.jclouds.rest.functions.ReturnEmptySetOnNotFoundOr404;
import org.jclouds.terremark.ecloud.functions.OrgURIToDataCentersListEndpoint;
import org.jclouds.vcloud.filters.SetVCloudTokenCookie;
import org.jclouds.vcloud.terremark.domain.DataCenter;
import org.jclouds.vcloud.terremark.domain.KeyPair;
import org.jclouds.vcloud.terremark.xml.DataCentersHandler;

import com.google.common.util.concurrent.ListenableFuture;

/**
 * 
 * @see <a href=
 *      "http://support.theenterprisecloud.com/kb/default.asp?id=960&Lang=1&SID="
 *      />
 * @see DataCenterOperationsClient
 * 
 * @author Adrian Cole
 */
@RequestFilters(SetVCloudTokenCookie.class)
public interface DataCenterOperationsAsyncClient {

   /**
    * @see DataCenterOperationsClient#listDataCentersInOrg
    */
   @GET
   @Consumes(DATACENTERSLIST_XML)
   @XMLResponseParser(DataCentersHandler.class)
   @ExceptionParser(ReturnEmptySetOnNotFoundOr404.class)
   ListenableFuture<? extends Set<KeyPair>> listDataCentersInOrg(
         @Nullable @EndpointParam(parser = OrgURIToDataCentersListEndpoint.class) URI org);

   /**
    * @see DataCenterOperationsClient#listDataCenters
    */
   @GET
   @Consumes(DATACENTERSLIST_XML)
   @XMLResponseParser(DataCentersHandler.class)
   @ExceptionParser(ReturnEmptySetOnNotFoundOr404.class)
   ListenableFuture<? extends Set<DataCenter>> listDataCenters(@EndpointParam URI dataCenters);
}
