package org.jclouds.terremark.ecloud.features;

import java.net.URI;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.jclouds.concurrent.Timeout;
import org.jclouds.vcloud.terremark.domain.DataCenter;

/**
 * Data Center Operations access to DataCenterOperations functionality in vCloud
 * <p/>
 * There are times where knowing a data center is necessary to complete certain
 * operations (i.e. uploading a catalog item). The data centers for an
 * organization are those data centers that contain at least one of the
 * organization's environments.
 * 
 * @see DataCenterOperationsAsyncClient
 * @author Adrian Cole
 */
@Timeout(duration = 300, timeUnit = TimeUnit.SECONDS)
public interface DataCenterOperationsClient {

   /**
    * This call will get the list of data centers that contain at least one of
    * the organization's environments.
    * 
    * 
    * @return data centers
    */
   Set<DataCenter> listDataCentersInOrg(URI orgId);

   /**
    * This call will get the list of data centers by list id.
    * 
    * @return data centers
    */
   Set<DataCenter> listDataCenters(URI dataCentersList);

}
