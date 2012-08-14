package org.jclouds.imagemaker;

import static com.google.common.base.Preconditions.checkState;

import java.util.Properties;

import javax.annotation.Resource;
import javax.inject.Named;

import org.jclouds.Constants;
import org.jclouds.ContextBuilder;
import org.jclouds.compute.ComputeServiceContext;
import org.jclouds.compute.RunNodesException;
import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.imagemaker.config.ImageMakerModule;
import org.jclouds.logging.Logger;
import org.jclouds.logging.slf4j.config.SLF4JLoggingModule;
import org.jclouds.sshj.config.SshjSshClientModule;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.inject.Module;

public class ImageMakerLiveTest {

   @Resource
   @Named(ComputeServiceConstants.COMPUTE_LOGGER)
   protected Logger logger = Logger.NULL;

   ComputeServiceContext context;

   @BeforeClass
   public void setUp() {

      Properties overrides = new Properties();
      overrides.setProperty(Constants.PROPERTY_TRUST_ALL_CERTS, "true");
      overrides.setProperty(Constants.PROPERTY_RELAX_HOSTNAME, "true");
      String provider = setIfTestSystemPropertyPresent(overrides, "image-maker.provider");
      String identity = setIfTestSystemPropertyPresent(overrides, provider + ".identity");
      String credential = setIfTestSystemPropertyPresent(overrides, provider + ".credential");
      String endpoint = setIfTestSystemPropertyPresent(overrides, provider + ".endpoint");
      setIfTestSystemPropertyPresent(overrides, provider + ".api-version");
      setIfTestSystemPropertyPresent(overrides, provider + ".build-version");

      context = ContextBuilder
               .newBuilder(provider)
               .credentials(identity, credential)
               .endpoint(endpoint)
               .overrides(overrides)
               .modules(ImmutableSet.<Module> of(new SLF4JLoggingModule(), new SshjSshClientModule(),
                        new ImageMakerModule())).build(ComputeServiceContext.class);
   }

   protected String setIfTestSystemPropertyPresent(Properties overrides, String key) {
      if (System.getProperties().containsKey("test." + key)) {
         String val = System.getProperty("test." + key);
         overrides.setProperty(key, val);
         return val;
      }
      if (System.getProperties().containsKey(key)) {
         String val = System.getProperty(key);
         overrides.setProperty(key, val);
         return val;
      }
      return null;
   }

   @Test(groups = "live", testName = "testImageMaker")
   public void testImageMaker() throws RunNodesException {
      checkState(context.getComputeService().getImageExtension().isPresent(),
               "image extension is not present in the configured context, "
                        + "please use a context that supports image extension");

      NodeMetadata node = Iterables.get(context.getComputeService().createNodesInGroup("test-image-maker", 1), 0);

      ImageMaker maker = context.utils().injector().getInstance(ImageMaker.class);
      Image image = maker.makeImage(node, "test", "test-image-maker");

      // here the node should have the packages describes in image-maker.yaml installed
      checkPackagesWereInstalled(node);

      context.getComputeService().destroyNode(node.getId());
      context.getComputeService().getImageExtension().get().deleteImage(image.getId());
   }

   public void checkPackagesWereInstalled(NodeMetadata node) {
      // TODO check the packages were installed (checked manually for now)
   }
}
