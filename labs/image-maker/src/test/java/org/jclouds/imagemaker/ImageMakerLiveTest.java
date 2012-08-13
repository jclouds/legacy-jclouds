package org.jclouds.imagemaker;

import static com.google.common.base.Preconditions.checkState;

import javax.annotation.Resource;
import javax.inject.Named;

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
      String provider = "";
      String identity = "";
      String credential = "";
      context = ContextBuilder
               .newBuilder(provider)
               .credentials(identity, credential)
               .modules(ImmutableSet.<Module> of(new SLF4JLoggingModule(), new SshjSshClientModule(),
                        new ImageMakerModule())).build(ComputeServiceContext.class);
   }

   @Test(groups = "live", testName = "testImageMakerAsANodePoolBaseNodeBuilder")
   public void testImageMakerAsANodePoolBaseNodeBuilder() throws RunNodesException {
      checkState(context.getComputeService().getImageExtension().isPresent(),
               "image extension is not present in the configured context, "
                        + "please use a context that supports image extension");

      NodeMetadata node = Iterables.get(context.getComputeService().createNodesInGroup("test-image-maker", 1), 0);

      ImageMaker maker = context.utils().injector().getInstance(ImageMaker.class);
      Image image = maker.makeImage(node, "test", "test-image-maker");

      // here the node should have the packages describes in image-maker.yaml installed
      // TODO test packages are installed/cached

      // create a nodepool with the given image

      // TODO test the packages are installed/cached in the nodepools base node

      // destroy
   }

   public void checkPackagesWereInstalled() {

   }
}
