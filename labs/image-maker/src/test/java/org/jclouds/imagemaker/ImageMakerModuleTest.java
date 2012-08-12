package org.jclouds.imagemaker;

import static org.testng.Assert.assertSame;

import org.jclouds.Context;
import org.jclouds.ContextBuilder;
import org.jclouds.compute.stub.StubApiMetadata;
import org.jclouds.imagemaker.config.ImageMakerModule;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;

public class ImageMakerModuleTest {

   @Test
   public void testImageMakerModule() {
      Context c = ContextBuilder.newBuilder(new StubApiMetadata()).modules(ImmutableList.of(new ImageMakerModule()))
               .build();
      ImageMaker maker = c.getUtils().injector().getInstance(ImageMaker.class);
      assertSame(maker.registeredProcessors().values().size(), 2);
   }

}
