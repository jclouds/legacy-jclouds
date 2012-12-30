package org.jclouds.joyent.cloudapi.v6_5.features;

import java.util.Set;
/**
 * Provides synchronous access to Packages.
 * <p/>
 * 
 * @author Gerald Pereira
 * @see PackageAsyncApi
 * @see <a href="http://apidocs.joyent.com/sdcapidoc/cloudapi/index.html#packages">api doc</a>
 */
public interface PackageApi {

   /**
    * Provides a list of packages available in this datacenter.
    * 
    * @return
    */
   Set<org.jclouds.joyent.cloudapi.v6_5.domain.Package> list();

   /**
    * Gets an individual package by id.
    * 
    * @param name
    *           the name of the package
    * @return
    */
   org.jclouds.joyent.cloudapi.v6_5.domain.Package get(String name);
}
