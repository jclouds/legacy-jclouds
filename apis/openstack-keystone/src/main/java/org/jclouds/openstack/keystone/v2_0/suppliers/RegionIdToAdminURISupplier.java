package org.jclouds.openstack.keystone.v2_0.suppliers;

import java.net.URI;
import java.util.Map;

import com.google.common.base.Supplier;
import com.google.inject.ImplementedBy;
import com.google.inject.assistedinject.Assisted;

/**
 * @author Adam Lowe
 */
@ImplementedBy(RegionIdToAdminURIFromAccessForTypeAndVersionSupplier.class)
public interface RegionIdToAdminURISupplier extends Supplier<Map<String, Supplier<URI>>> {
   static interface Factory {
      /**
       *
       * @param apiType
       *           type of the api, according to the provider. ex. {@code compute} {@code
       *           object-store}
       * @param apiVersion
       *           version of the api
       * @return regions mapped to default uri
       */
      RegionIdToAdminURISupplier createForApiTypeAndVersion(@Assisted("apiType") String apiType,
                                                       @Assisted("apiVersion") String apiVersion);
   }
}
