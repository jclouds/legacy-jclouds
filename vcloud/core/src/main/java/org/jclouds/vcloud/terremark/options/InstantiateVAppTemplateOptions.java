package org.jclouds.vcloud.terremark.options;

import static com.google.common.base.Preconditions.checkArgument;

import java.net.URI;
import java.util.Map;

import javax.inject.Inject;

import org.jclouds.http.HttpRequest;
import org.jclouds.vcloud.endpoints.Network;
import org.jclouds.vcloud.terremark.binders.BindInstantiateVAppTemplateParamsToXmlEntity;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Maps;

/**
 * 
 * @author Adrian Cole
 * 
 */
public class InstantiateVAppTemplateOptions extends BindInstantiateVAppTemplateParamsToXmlEntity {
   @Inject
   @Network
   private URI defaultNetwork;

   @VisibleForTesting
   String cpuCount = "1";
   @VisibleForTesting
   String megabytes = "512";
   @VisibleForTesting
   String network;

   public void bindToRequest(HttpRequest request, Map<String, String> postParams) {
      Map<String, String> copy = Maps.newHashMap();
      copy.putAll(postParams);
      copy.put("count", cpuCount);
      copy.put("megabytes", megabytes);
      copy.put("network", network != null ? network : defaultNetwork.toASCIIString());
      super.bindToRequest(request, copy);
   }

   public InstantiateVAppTemplateOptions cpuCount(int cpuCount) {
      checkArgument(cpuCount >= 1, "cpuCount must be positive");
      this.cpuCount = cpuCount + "";
      return this;
   }

   public InstantiateVAppTemplateOptions megabytes(int megabytes) {
      checkArgument(megabytes % 512 == 0, "megabytes must be in an increment of 512");
      this.megabytes = megabytes + "";
      return this;
   }

   public InstantiateVAppTemplateOptions inNetwork(URI networkLocation) {
      this.network = networkLocation.toASCIIString();
      return this;
   }

   public static class Builder {

      /**
       * @see InstantiateVAppTemplateOptions#cpuCount(int)
       */
      public static InstantiateVAppTemplateOptions cpuCount(int cpuCount) {
         InstantiateVAppTemplateOptions options = new InstantiateVAppTemplateOptions();
         return options.cpuCount(cpuCount);
      }

      /**
       * @see InstantiateVAppTemplateOptions#megabytes(int)
       */
      public static InstantiateVAppTemplateOptions megabytes(int megabytes) {
         InstantiateVAppTemplateOptions options = new InstantiateVAppTemplateOptions();
         return options.megabytes(megabytes);
      }

      /**
       * @see InstantiateVAppTemplateOptions#inNetwork(URI)
       */
      public static InstantiateVAppTemplateOptions inNetwork(URI networkLocation) {
         InstantiateVAppTemplateOptions options = new InstantiateVAppTemplateOptions();
         return options.inNetwork(networkLocation);
      }
   }
}
