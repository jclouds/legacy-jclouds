package org.jclouds.terremark.ecloud.features;

import static org.jclouds.vcloud.terremark.TerremarkECloudMediaType.TAGSLISTLIST_XML;

import java.net.URI;
import java.util.Map;

import javax.annotation.Nullable;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;

import org.jclouds.rest.annotations.EndpointParam;
import org.jclouds.rest.annotations.ExceptionParser;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.XMLResponseParser;
import org.jclouds.rest.functions.ReturnEmptyMapOnNotFoundOr404;
import org.jclouds.terremark.ecloud.functions.OrgURIToTagsListEndpoint;
import org.jclouds.terremark.ecloud.xml.TagNameToUsageCountHandler;
import org.jclouds.vcloud.filters.SetVCloudTokenCookie;

import com.google.common.util.concurrent.ListenableFuture;

/**
 * 
 * @see <a href=
 *      "http://support.theenterprisecloud.com/kb/default.asp?id=954&Lang=1&SID="
 *      />
 * @see TagOperationsClient
 * 
 * @author Adrian Cole
 */
@RequestFilters(SetVCloudTokenCookie.class)
public interface TagOperationsAsyncClient {

   /**
    * @see TagOperationsClient#getTagNameToUsageCountInOrg
    */
   @GET
   @Consumes(TAGSLISTLIST_XML)
   @XMLResponseParser(TagNameToUsageCountHandler.class)
   @ExceptionParser(ReturnEmptyMapOnNotFoundOr404.class)
   ListenableFuture<? extends Map<String, Integer>> getTagNameToUsageCountInOrg(
         @Nullable @EndpointParam(parser = OrgURIToTagsListEndpoint.class) URI org);

   /**
    * @see TagOperationsClient#getTagNameToUsageCount
    */
   @GET
   @Consumes(TAGSLISTLIST_XML)
   @XMLResponseParser(TagNameToUsageCountHandler.class)
   @ExceptionParser(ReturnEmptyMapOnNotFoundOr404.class)
   ListenableFuture<? extends Map<String, Integer>> getTagNameToUsageCount(@EndpointParam URI tagList);
}
