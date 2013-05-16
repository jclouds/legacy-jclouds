package org.jclouds.blobstore.functions;

import static org.testng.Assert.assertEquals;

import java.io.File;

import org.jclouds.ContextBuilder;
import org.jclouds.blobstore.domain.Blob;
import org.jclouds.blobstore.domain.Blob.Factory;
import org.testng.annotations.Test;

/**
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit")
public class BlobNameTest {
   BlobName fn = new BlobName();
   
   private static final Factory BLOB_FACTORY = ContextBuilder.newBuilder("transient").buildInjector().getInstance(Blob.Factory.class);

   @Test
   public void testCorrect() throws SecurityException, NoSuchMethodException {

      Blob blob = BLOB_FACTORY.create(null);
      blob.getMetadata().setName("foo");

      assertEquals(fn.apply(blob), "foo");
   }

   @Test(expectedExceptions = IllegalArgumentException.class)
   public void testMustBeBlob() {
      fn.apply(new File("foo"));
   }

   @Test(expectedExceptions = { NullPointerException.class, IllegalStateException.class })
   public void testNullIsBad() {
      fn.apply(null);
   }
}
