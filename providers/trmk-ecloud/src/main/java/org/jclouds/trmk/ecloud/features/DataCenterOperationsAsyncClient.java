package org.jclouds.trmk.ecloud.features;

import static org.jclouds.trmk.ecloud.TerremarkECloudMediaType.DATACENTERSLIST_XML;

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
import org.jclouds.trmk.ecloud.functions.OrgURIToDataCentersListEndpoint;
import org.jclouds.trmk.vcloud_0_8.domain.DataCenter;
import org.jclouds.trmk.vcloud_0_8.domain.KeyPair;
import org.jclouds.trmk.vcloud_0_8.filters.SetVCloudTokenCookie;
import org.jclouds.trmk.vcloud_0_8.xml.DataCentersHandler;

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
