package org.jclouds.rackspace.cloudidentity.v2_0.config;

import org.jclouds.location.config.LocationModule;
import org.jclouds.openstack.keystone.v2_0.KeystoneAsyncClient;
import org.jclouds.openstack.keystone.v2_0.KeystoneClient;
import org.jclouds.openstack.keystone.v2_0.config.KeystoneRestClientModule;
import org.jclouds.rest.ConfiguresRestClient;

/**
 * Configures the Keystone connection.
 * 
 * @author Adam Lowe
 */
@ConfiguresRestClient
public class CloudIdentityRestClientModule extends KeystoneRestClientModule<KeystoneClient, KeystoneAsyncClient> {

   @Override
   protected void installLocations() {
      // TODO: select this from KeystoneProperties.VERSION; note you select from
      // a guice provided property, so it will have to come from somewhere else, maybe we move
      // this to the the ContextBuilder
      install(CloudIdentityAuthenticationModule.forRegions());
      install(new LocationModule());
   }
}
