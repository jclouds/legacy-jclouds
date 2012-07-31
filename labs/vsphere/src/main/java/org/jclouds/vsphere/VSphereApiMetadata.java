package org.jclouds.vsphere;

import static org.jclouds.compute.config.ComputeServiceProperties.TEMPLATE;
<<<<<<< HEAD
import static org.jclouds.vsphere.config.VSphereConstants.CLONING;
=======
>>>>>>> a337b6ca8e4b24dee2f093235a7291fc83b31b3f

import java.net.URI;
import java.util.Properties;

import org.jclouds.apis.ApiMetadata;
import org.jclouds.apis.internal.BaseApiMetadata;
import org.jclouds.compute.ComputeServiceContext;
import org.jclouds.vsphere.config.VSphereComputeServiceContextModule;

/**
 * Implementation of {@link ApiMetadata} for an example of library integration (VSphereManager)
 * 
 * @author Andrea Turli
 */
public class VSphereApiMetadata extends BaseApiMetadata {

   private static final long serialVersionUID = 7050419752716105398L;

   @Override
   public Builder toBuilder() {
      return new Builder().fromApiMetadata(this);
   }

   public VSphereApiMetadata() {
      this(new Builder());
   }

   protected VSphereApiMetadata(Builder builder) {
      super(builder);
   }

   public static Properties defaultProperties() {
      Properties properties = BaseApiMetadata.defaultProperties();
<<<<<<< HEAD
      
      String cloneStrategy = System.getProperty("test.vsphere.cloning", "linked");
      properties.setProperty(CLONING, cloneStrategy);
      
=======
>>>>>>> a337b6ca8e4b24dee2f093235a7291fc83b31b3f
      properties.setProperty(TEMPLATE,
                        "osFamily=UBUNTU,osVersionMatches=12.04,os64Bit=true,osArchMatches=x86,loginUser=toor:password,authenticateSudo=true");
      return properties;
   }
   
   public static class Builder extends BaseApiMetadata.Builder {

      protected Builder(){
         id("vsphere")
         .name("vSphere API")
         .identityName("User")
         .defaultIdentity("root")
         .credentialName("password")
         .defaultCredential("vmware")
         .defaultEndpoint("https://192.168.221.130/sdk")
         .documentation(URI.create("http://www.jclouds.org/documentation/userguide/compute"))
         .defaultProperties(VSphereApiMetadata.defaultProperties())
         .view(ComputeServiceContext.class)
         .defaultModule(VSphereComputeServiceContextModule.class);
      }

      @Override
      public VSphereApiMetadata build() {
         return new VSphereApiMetadata(this);
      }
      
      @Override
      public Builder fromApiMetadata(ApiMetadata in) {
         super.fromApiMetadata(in);
         return this;
      }      

   }
}