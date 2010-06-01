#set( $lcaseProviderName = ${providerName.toLowerCase()} )
#set( $ucaseProviderName = ${providerName.toUpperCase()} )
/**
 *
 * Copyright (C) 2010 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ====================================================================
 */
package ${package}.compute;

import static org.testng.Assert.assertEquals;

import java.io.IOException;
import java.util.Properties;

import ${package}.${providerName}ContextBuilder;
import ${package}.${providerName}PropertiesBuilder;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import com.google.common.io.Resources;

/**
 * @author ${author}
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

   public void test${providerName}() {
      assertEquals(properties.getProperty("${lcaseProviderName}.contextbuilder"),
               ${providerName}ContextBuilder.class.getName());
      assertEquals(properties.getProperty("${lcaseProviderName}.propertiesbuilder"),
               ${providerName}PropertiesBuilder.class.getName());
   }

}
