package org.jclouds.terremark.ecloud.features;

import java.net.URI;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.jclouds.concurrent.Timeout;

/**
 * Tag Based Operations
 * <p/>
 * 
 * @see TagOperationsAsyncClient
 * @author Adrian Cole
 */
@Timeout(duration = 300, timeUnit = TimeUnit.SECONDS)
public interface TagOperationsClient {

   /**
    * This call returns the list of all tags belonging to the organization.
    * 
    * @return tags
    */
   Map<String, Integer> getTagNameToUsageCountInOrg(URI orgId);

   /**
    * This call returns the list of all tags by list id.
    * 
    * @return tags
    */
   Map<String, Integer> getTagNameToUsageCount(URI tagsList);

}
