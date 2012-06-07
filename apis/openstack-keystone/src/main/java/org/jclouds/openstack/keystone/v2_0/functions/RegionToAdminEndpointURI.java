package org.jclouds.openstack.keystone.v2_0.functions;

import javax.inject.Inject;

import org.jclouds.location.functions.RegionToEndpoint;
import org.jclouds.openstack.keystone.v2_0.suppliers.RegionIdToAdminURISupplier;

/**
 * @author Adam Lowe
 */
public class RegionToAdminEndpointURI extends RegionToEndpoint {
   @Inject
   public RegionToAdminEndpointURI(RegionIdToAdminURISupplier regionToEndpointSupplier) {
      super(regionToEndpointSupplier);
   }
}
