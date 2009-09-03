package org.jclouds.azure.storage.blob.options;

import org.jclouds.azure.storage.options.CreateOptions;

import com.google.common.collect.Multimap;

/**
 * Contains options supported in the REST API for the Create Container operation. <h2>
 * Usage</h2> The recommended way to instantiate a CreateContainerOptions object is to statically
 * import CreateContainerOptions.* and invoke a static creation method followed by an instance
 * mutator (if needed):
 * <p/>
 * <code>
 * import static org.jclouds.azure.storage.blob.options.CreateContainerOptions.Builder.*
 * import org.jclouds.azure.storage.blob.AzureBlobConnection;
 * <p/>
 * AzureBlobConnection connection = // get connection
 * boolean createdWithPublicAcl = connection.createContainer("containerName", withPublicAcl());
 * <code> *
 * 
 * @see <a href="http://msdn.microsoft.com/en-us/library/dd179466.aspx" />
 * @author Adrian Cole
 */
public class CreateContainerOptions extends CreateOptions {
   public static final CreateContainerOptions NONE = new CreateContainerOptions();

   @Override
   public CreateContainerOptions withMetadata(Multimap<String, String> metadata) {
      return (CreateContainerOptions) super.withMetadata(metadata);
   }

   /**
    * Indicates whether a container may be accessed publicly
    */
   public CreateContainerOptions withPublicAcl() {
      this.headers.put("x-ms-prop-publicaccess", "true");
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
         return (CreateContainerOptions) options.withMetadata(metadata);
      }

   }
}
