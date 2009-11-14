package org.jclouds.vcloud.terremark.options;

import java.util.Map;

import org.jclouds.http.HttpRequest;
import org.jclouds.vcloud.terremark.binders.BindAddNodeServiceToXmlEntity;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Maps;

/**
 * 
 * @author Adrian Cole
 * 
 */
public class AddNodeOptions extends BindAddNodeServiceToXmlEntity {

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

   public AddNodeOptions disabled() {
      this.enabled = "false";
      return this;
   }

   public AddNodeOptions withDescription(String description) {
      this.description = description;
      return this;
   }

   public static class Builder {

      /**
       * @see AddNodeOptions#withDescription(String)
       */
      public static AddNodeOptions withDescription(String description) {
         AddNodeOptions options = new AddNodeOptions();
         return options.withDescription(description);
      }

      /**
       * @see AddNodeOptions#disabled()
       */
      public static AddNodeOptions disabled() {
         AddNodeOptions options = new AddNodeOptions();
         return options.disabled();
      }
   }
}
