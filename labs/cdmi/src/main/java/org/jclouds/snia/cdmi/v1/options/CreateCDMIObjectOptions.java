package org.jclouds.snia.cdmi.v1.options;

import java.util.Map;
import java.util.Map.Entry;

import org.jclouds.http.options.BaseHttpRequestOptions;

import com.google.gson.JsonObject;

/**
 * Contains options supported in the REST API for the CREATE container operation. <h2>
 * 
 * @author Kenneth Nagin
 */
public class CreateCDMIObjectOptions extends BaseHttpRequestOptions {
   protected JsonObject jsonObjectBody = new JsonObject();

   /**
    * A name-value pair to associate with the container as metadata.
    */
   public CreateCDMIObjectOptions metadata(Map<String, String> metadata) {
      JsonObject jsonObjectMetadata = new JsonObject();
      if (metadata != null) {
         for (Entry<String, String> entry : metadata.entrySet()) {
            jsonObjectMetadata.addProperty(entry.getKey(), entry.getValue());
         }
      }
      jsonObjectBody.add("metadata", jsonObjectMetadata);
      this.payload = jsonObjectBody.toString();
      return this;
   }

   public static class Builder {
      public static CreateCDMIObjectOptions withMetadata(Map<String, String> metadata) {
         CreateCDMIObjectOptions options = new CreateCDMIObjectOptions();
         return (CreateCDMIObjectOptions) options.metadata(metadata);
      }
   }
}
