package org.jclouds.vcloud.terremark.functions;

import java.net.URI;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.rest.ResourceNotFoundException;
import org.jclouds.vcloud.domain.NamedResource;
import org.jclouds.vcloud.endpoints.Org;
import org.jclouds.vcloud.terremark.endpoints.KeysList;

import com.google.common.base.Function;
import com.google.common.base.Supplier;

/**
 * 
 * @author Adrian Cole
 */
@Singleton
public class OrgNameToKeysListEndpoint implements Function<Object, URI> {
   private final Supplier<Map<String, NamedResource>> orgNameToEndpoint;
   private final String defaultOrg;

   @Inject
   public OrgNameToKeysListEndpoint(@KeysList Supplier<Map<String, NamedResource>> orgNameToEndpoint,
         @Org String defaultUri) {
      this.orgNameToEndpoint = orgNameToEndpoint;
      this.defaultOrg = defaultUri;
   }

   public URI apply(Object from) {
      try {
         return orgNameToEndpoint.get().get(from == null ? defaultOrg : from).getLocation();
      } catch (NullPointerException e) {
         throw new ResourceNotFoundException("org " + from + " not found in " + orgNameToEndpoint.get().keySet());
      }
   }

}