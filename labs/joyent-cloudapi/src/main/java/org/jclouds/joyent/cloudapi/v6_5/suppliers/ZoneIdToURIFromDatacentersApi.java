package org.jclouds.joyent.cloudapi.v6_5.suppliers;

import java.net.URI;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.joyent.cloudapi.v6_5.features.DatacenterApi;
import org.jclouds.location.suppliers.ZoneIdToURISupplier;
import org.jclouds.util.Suppliers2;

import com.google.common.base.Supplier;
import com.google.common.collect.Maps;

@Singleton
public class ZoneIdToURIFromDatacentersApi implements ZoneIdToURISupplier {

   private final DatacenterApi api;

   @Inject
   public ZoneIdToURIFromDatacentersApi(DatacenterApi api) {
      this.api = api;
   }

   @Override
   public Map<String, Supplier<URI>> get() {
      return Maps.transformValues(api.getDatacenters(), Suppliers2.<URI> ofInstanceFunction());
   }

   @Override
   public String toString() {
      return "getDatacenters()";
   }
}
