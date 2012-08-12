package org.jclouds.imagemaker;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertSame;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Map;

import org.jclouds.imagemaker.config.ImageDescriptor;
import org.jclouds.imagemaker.config.ImageMakerModule;
import org.testng.annotations.Test;

import com.google.common.collect.Iterables;

public class ImageDescriptorTest {

   @Test
   public void testLoadYaml() throws IOException, URISyntaxException {
      Map<String, ImageDescriptor> images = new ImageMakerModule().imageDescriptors().get();
      assertSame(images.size(), 1);
      ImageDescriptor desc = Iterables.get(images.values(), 0);
      assertEquals(desc.id, "test");
      assertSame(desc.cached_packages.size(), 1);
      assertSame(desc.installed_packages.size(), 1);
      assertEquals(Iterables.get(desc.cached_packages.get("apt"), 0), "nginx");
      assertEquals(Iterables.get(desc.installed_packages.get("apt"), 0), "vi");
   }
}
