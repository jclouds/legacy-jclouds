package org.jclouds.azure.storage.options;

import java.util.Map.Entry;

import org.jclouds.azure.storage.reference.AzureStorageHeaders;
import org.jclouds.http.options.BaseHttpRequestOptions;

import com.google.common.collect.Multimap;

/**
 * Contains common options supported in the REST API for the Create operation. <h2>
 * Usage</h2> The recommended way to instantiate a CreateOptions object is to statically import
 * CreateOptions.* and invoke a static creation method followed by an instance mutator (if
 * needed):
 * <p/>
 * <code>
 * import static org.jclouds.azure.storage.options.CreateOptions.Builder.*
 * import org.jclouds.azure.storage.queue.AzureQueueConnection;
 * <p/>
 * AzureQueueConnection connection = // get connection
 * Multimap<String,String> metadata = // ...
 * boolean createdWithPublicAcl = connection.createQueue("containerName", withMetdata(metadata));
 * <code> *
 * 
 * @see <a href="http://msdn.microsoft.com/en-us/library/dd179466.aspx" />
 * @author Adrian Cole
 */
public class CreateOptions extends BaseHttpRequestOptions {
   public static final CreateOptions NONE = new CreateOptions();

   /**
    * A name-value pair to associate with the container as metadata.
    * 
    * Note that these are stored at the server under the prefix: x-ms-meta-
    */
   public CreateOptions withMetadata(Multimap<String, String> metadata) {
      for (Entry<String, String> entry : metadata.entries()) {
         if (entry.getKey().startsWith(AzureStorageHeaders.USER_METADATA_PREFIX))
            headers.put(entry.getKey(), entry.getValue());
         else
            headers
                     .put(AzureStorageHeaders.USER_METADATA_PREFIX + entry.getKey(), entry
                              .getValue());
      }
      return this;
   }

   public static class Builder {

      /**
       * @see CreateOptions#withMetadata(Multimap<String, String>)
       */
      public static CreateOptions withMetadata(Multimap<String, String> metadata) {
         CreateOptions options = new CreateOptions();
         return options.withMetadata(metadata);
      }

   }
}
