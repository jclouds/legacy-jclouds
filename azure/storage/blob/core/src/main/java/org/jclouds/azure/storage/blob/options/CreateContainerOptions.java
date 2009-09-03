package org.jclouds.azure.storage.blob.options;

import java.util.Map.Entry;

import org.jclouds.azure.storage.reference.AzureStorageHeaders;
import org.jclouds.http.options.BaseHttpRequestOptions;

import com.google.common.collect.Multimap;

/**
 * Contains options supported in the REST API for the Create Container operation. <h2>
 * Usage</h2> The recommended way to instantiate a CreateContainerOptions object is to statically
 * import CreateContainerOptions.* and invoke a static creation method followed by an instance
 * mutator (if needed):
 * <p/>
 * <code>
 * import static org.jclouds.azure.storage.blob.options.PutBucketOptions.Builder.*
 * import org.jclouds.azure.storage.blob.AzureBlobConnection;
 * <p/>
 * AzureBlobConnection connection = // get connection
 * boolean createdWithPublicAcl = connection.createContainer("containerName", withPublicAcl());
 * <code> *
 * 
 * @see <a href="http://msdn.microsoft.com/en-us/library/dd179466.aspx" />
 * @author Adrian Cole
 */
public class CreateContainerOptions extends BaseHttpRequestOptions {
   public static final CreateContainerOptions NONE = new CreateContainerOptions();

   /**
    * Indicates whether a container may be accessed publicly
    */
   public CreateContainerOptions withPublicAcl() {
      this.headers.put("x-ms-prop-publicaccess", "true");
      return this;
   }

   /**
    * A name-value pair to associate with the container as metadata.
    * 
    * Note that these are stored at the server under the prefix: x-ms-meta-
    */
   public CreateContainerOptions withMetadata(Multimap<String, String> metadata) {
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
       * @see CreateContainerOptions#withPublicAcl()
       */
      public static CreateContainerOptions withPublicAcl() {
         CreateContainerOptions options = new CreateContainerOptions();
         return options.withPublicAcl();
      }

      /**
       * @see CreateContainerOptions#withMetadata(Multimap<String, String>)
       */
      public static CreateContainerOptions withMetadata(Multimap<String, String> metadata) {
         CreateContainerOptions options = new CreateContainerOptions();
         return options.withMetadata(metadata);
      }

   }
}
