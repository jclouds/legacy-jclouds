package org.jclouds.joyent.cloudapi.v6_5.features;

import java.util.Set;
import org.jclouds.joyent.cloudapi.v6_5.domain.Dataset;

/**
 * Provides synchronous access to Datasets.
 * <p/>
 * 
 * @author Gerald Pereira
 * @see DatasetAsyncApi
 * @see <a href="http://apidocs.joyent.com/sdcapidoc/cloudapi/index.html#datasets">api doc</a>
 */
public interface DatasetApi {

   /**
    * Provides a list of datasets available in this datacenter.
    * 
    * @return
    */
   Set<Dataset> list();

   /**
    * Gets an individual dataset by id.
    * 
    * @param id
    *           the id of the dataset
    * @return
    */
   Dataset get(String id);
}
