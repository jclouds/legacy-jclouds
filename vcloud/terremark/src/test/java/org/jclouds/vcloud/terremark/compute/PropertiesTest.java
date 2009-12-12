package org.jclouds.vcloud.terremark.compute;

import static org.testng.Assert.assertEquals;

import java.io.IOException;
import java.util.Properties;

import org.jclouds.vcloud.terremark.TerremarkVCloudContextBuilder;
import org.jclouds.vcloud.terremark.TerremarkVCloudPropertiesBuilder;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import com.google.common.io.Resources;

/**
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "compute.PropertiesTest")
public class PropertiesTest {
   private Properties properties;

   @BeforeTest
   public void setUp() throws IOException {
      properties = new Properties();
      properties.load(Resources.newInputStreamSupplier(Resources.getResource("compute.properties"))
               .getInput());
   }

   public void testRimu() {
      assertEquals(properties.getProperty("terremark.contextbuilder"),
               TerremarkVCloudContextBuilder.class.getName());
      assertEquals(properties.getProperty("terremark.propertiesbuilder"),
               TerremarkVCloudPropertiesBuilder.class.getName());
   }

}
