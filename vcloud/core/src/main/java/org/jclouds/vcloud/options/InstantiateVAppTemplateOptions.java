package org.jclouds.vcloud.options;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.net.URI;

/**
 * 
 * @author Adrian Cole
 * 
 */
public class InstantiateVAppTemplateOptions {

   private String cpuCount;
   private String megabytes;
   private String network;

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
      this.network = checkNotNull(networkLocation, "networkLocation").toASCIIString();
      return this;
   }

   public String getCpuCount() {
      return cpuCount;
   }

   public String getMegabytes() {
      return megabytes;
   }

   public String getNetwork() {
      return network;
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
