package org.jclouds.openstack.nova.v1_1.functions;

import static org.testng.Assert.assertEquals;

import java.net.URI;

import org.jclouds.date.internal.SimpleDateFormatDateService;
import org.jclouds.openstack.nova.v1_1.domain.Extension;
import org.testng.annotations.Test;

/**
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "ExtensionToNameSpaceTest")
public class ExtensionToNameSpaceTest {
   private final ExtensionToNameSpace fn = new ExtensionToNameSpace();

   public void testReturnsNamespace() {
      URI ns = URI.create("http://docs.openstack.org/ext/keypairs/api/v1.1");
      assertEquals(
            fn.apply(Extension.builder().alias("os-keypairs").name("Keypairs").namespace(ns)
                  .updated(new SimpleDateFormatDateService().iso8601SecondsDateParse("2011-08-08T00:00:00+00:00"))
                  .description("Keypair Support").build()), ns);
   }
}
