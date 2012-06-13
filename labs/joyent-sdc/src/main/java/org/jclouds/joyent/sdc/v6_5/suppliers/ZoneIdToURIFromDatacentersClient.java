package org.jclouds.joyent.sdc.v6_5.suppliers;

import java.net.URI;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.joyent.sdc.v6_5.features.DatacenterClient;
import org.jclouds.location.suppliers.ZoneIdToURISupplier;
import org.jclouds.util.Suppliers2;

import com.google.common.base.Supplier;
import com.google.common.collect.Maps;

@Singleton
public class ZoneIdToURIFromDatacentersClient implements ZoneIdToURISupplier {

   private final DatacenterClient client;

   @Inject
   public ZoneIdToURIFromDatacentersClient(DatacenterClient client) {
      this.client = client;
   }

   @Override
   public Map<String, Supplier<URI>> get() {
      return Maps.transformValues(client.getDatacenters(), Suppliers2.<URI> ofInstanceFunction());
   }

   @Override
   public String toString() {
      return "getDatacenters()";
   }
}