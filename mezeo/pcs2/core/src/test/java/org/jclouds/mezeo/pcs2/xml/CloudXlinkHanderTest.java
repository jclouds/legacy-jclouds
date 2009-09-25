package org.jclouds.mezeo.pcs2.xml;

import static org.testng.Assert.assertEquals;

import java.io.InputStream;
import java.net.URI;
import java.util.Map;

import org.jclouds.http.functions.BaseHandlerTest;
import org.jclouds.mezeo.pcs2.PCSCloud;
import org.jclouds.mezeo.pcs2.xml.CloudXlinkHandler.PCSCloudResponseImpl;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableMap;

/**
 * Tests behavior of {@code ParseFlavorListFromGsonResponseTest}
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "pcs2.CloudXlinkHanderTest")
public class CloudXlinkHanderTest extends BaseHandlerTest {
   Map<String, URI> map = ImmutableMap
            .<String, URI> builder()
            .put(
                     "rootContainer",
                     URI
                              .create("https://pcsbeta.mezeo.net/v2/containers/0B5C8F50-8E72-11DE-A1D4-D73479DA6257"))
            .put("contacts", URI.create("https://pcsbeta.mezeo.net/v2/contacts")).put("shares",
                     URI.create("https://pcsbeta.mezeo.net/v2/shares")).put("projects",
                     URI.create("https://pcsbeta.mezeo.net/v2/projects")).put("metacontainers",
                     URI.create("https://pcsbeta.mezeo.net/v2/metacontainers")).put("account",
                     URI.create("https://pcsbeta.mezeo.net/v2/account")).put("tags",
                     URI.create("https://pcsbeta.mezeo.net/v2/tags")).put("recyclebin",
                     URI.create("https://pcsbeta.mezeo.net/v2/recyclebin")).build();

   public void testApplyInputStream() {
      InputStream is = getClass().getResourceAsStream("/discovery.xml");
      PCSCloud.Response list = new PCSCloudResponseImpl(map);

      PCSCloud.Response result = (PCSCloud.Response) factory.create(
               injector.getInstance(CloudXlinkHandler.class)).parse(is);

      assertEquals(result, list);
   }
}
