package org.jclouds.deltacloud.options;

import static com.google.common.base.Preconditions.checkNotNull;

import org.jclouds.http.options.BaseHttpRequestOptions;

/**
 * Contains options supported in the Deltacloud API for the Create Instance operation. <h2>
 * Usage</h2> The recommended way to instantiate a CreateInstanceOptions object is to statically
 * import CreateInstanceOptions.Builder.* and invoke a static creation method followed by an
 * instance mutator (if needed):
 * <p/>
 * <code>
 * import static org.jclouds.deltacloud.options.CreateInstanceOptions.Builder.*
 * <p/>
 * DeltacloudClient connection = // get connection
 * ListenableFuture<Instance> instance = client.createInstance(collection, "imageId", named("robot"));
 * <code>
 * 
 * @author Adrian Cole
 * @see <a href="http://deltacloud.org/api.html#h1" />
 */
public class CreateInstanceOptions extends BaseHttpRequestOptions {
   public static final CreateInstanceOptions NONE = new CreateInstanceOptions();

   /**
    * A short label to identify the instance.
    * 
    */
   public CreateInstanceOptions named(String name) {
      formParameters.put("name", checkNotNull(name, "name"));
      return this;
   }

   public String getName() {
      return this.getFirstFormOrNull("name");
   }

   public static class Builder {

      /**
       * @see CreateInstanceOptions#named
       */
      public static CreateInstanceOptions named(String name) {
         CreateInstanceOptions options = new CreateInstanceOptions();
         return options.named(name);
      }

   }
}
