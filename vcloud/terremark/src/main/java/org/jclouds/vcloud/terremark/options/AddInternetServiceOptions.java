package org.jclouds.vcloud.terremark.options;

import java.util.Map;

import org.jclouds.http.HttpRequest;
import org.jclouds.vcloud.terremark.binders.BindAddInternetServiceToXmlEntity;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Maps;

/**
 * 
 * @author Adrian Cole
 * 
 */
public class AddInternetServiceOptions extends BindAddInternetServiceToXmlEntity {

   @VisibleForTesting
   String description = null;
   @VisibleForTesting
   String enabled = "true";

   public void bindToRequest(HttpRequest request, Map<String, String> postParams) {
      Map<String, String> copy = Maps.newHashMap();
      copy.putAll(postParams);
      copy.put("description", description);
      copy.put("enabled", enabled);
      super.bindToRequest(request, copy);
   }

   public AddInternetServiceOptions disabled() {
      this.enabled = "false";
      return this;
   }

   public AddInternetServiceOptions withDescription(String description) {
      this.description = description;
      return this;
   }

   public static class Builder {

      /**
       * @see AddInternetServiceOptions#withDescription(String)
       */
      public static AddInternetServiceOptions withDescription(String description) {
         AddInternetServiceOptions options = new AddInternetServiceOptions();
         return options.withDescription(description);
      }

      /**
       * @see AddInternetServiceOptions#disabled()
       */
      public static AddInternetServiceOptions disabled() {
         AddInternetServiceOptions options = new AddInternetServiceOptions();
         return options.disabled();
      }
   }
}
