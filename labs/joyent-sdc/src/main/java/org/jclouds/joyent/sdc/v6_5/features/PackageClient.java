package org.jclouds.joyent.sdc.v6_5.features;

import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.jclouds.concurrent.Timeout;

/**
 * Provides synchronous access to Packages.
 * <p/>
 * 
 * @author Gerald Pereira
 * @see PackageAsyncClient
 * @see <a href="http://apidocs.joyent.com/sdcapidoc/cloudapi">api doc</a>
 */
@Timeout(duration = 30, timeUnit = TimeUnit.SECONDS)
public interface PackageClient {

   /**
    * Provides a list of packages available in this datacenter.
    * 
    * @return
    */
   Set<org.jclouds.joyent.sdc.v6_5.domain.Package> listPackages();

   /**
    * Gets an individual package by id.
    * 
    * @param name
    *           the name of the package
    * @return
    */
   org.jclouds.joyent.sdc.v6_5.domain.Package getPackage(String name);
}
