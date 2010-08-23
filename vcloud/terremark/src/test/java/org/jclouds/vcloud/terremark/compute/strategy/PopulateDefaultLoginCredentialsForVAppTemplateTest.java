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

package org.jclouds.vcloud.terremark.compute.strategy;

import static org.easymock.EasyMock.expect;
import static org.easymock.classextension.EasyMock.createMock;
import static org.easymock.classextension.EasyMock.replay;
import static org.easymock.classextension.EasyMock.verify;
import static org.testng.Assert.assertEquals;

import java.io.IOException;
import java.io.InputStream;

import org.jclouds.domain.Credentials;
import org.jclouds.vcloud.domain.VCloudExpressVAppTemplate;
import org.testng.annotations.Test;

import com.google.common.io.ByteStreams;

/**
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "terremark.PopulateDefaultLoginCredentialsForVAppTemplateTest")
public class PopulateDefaultLoginCredentialsForVAppTemplateTest {

   @Test
   public void testRhel() throws IOException {
      InputStream is = getClass().getResourceAsStream("/terremark/rhel_description.txt");
      String description = new String(ByteStreams.toByteArray(is));
      VCloudExpressVAppTemplate template = createMock(VCloudExpressVAppTemplate.class);
      expect(template.getDescription()).andReturn(description).atLeastOnce();
      replay(template);
      ParseVAppTemplateDescriptionToGetDefaultLoginCredentials converter = new ParseVAppTemplateDescriptionToGetDefaultLoginCredentials();
      Credentials creds = converter.execute(template);
      assertEquals(creds.identity, "vcloud");
      assertEquals(creds.credential, "$Ep455l0ud!2");
      verify(template);
   }

   @Test
   public void testLamp() throws IOException {
      InputStream is = getClass().getResourceAsStream("/terremark/lamp_description.txt");
      String description = new String(ByteStreams.toByteArray(is));
      VCloudExpressVAppTemplate template = createMock(VCloudExpressVAppTemplate.class);
      expect(template.getDescription()).andReturn(description).atLeastOnce();
      replay(template);
      ParseVAppTemplateDescriptionToGetDefaultLoginCredentials converter = new ParseVAppTemplateDescriptionToGetDefaultLoginCredentials();
      Credentials creds = converter.execute(template);
      assertEquals(creds.identity, "ecloud");
      assertEquals(creds.credential, "$Ep455l0ud!2");
      verify(template);
   }

   @Test
   public void testFt() throws IOException {
      InputStream is = getClass().getResourceAsStream("/terremark/ft_description.txt");
      String description = new String(ByteStreams.toByteArray(is));
      VCloudExpressVAppTemplate template = createMock(VCloudExpressVAppTemplate.class);
      expect(template.getDescription()).andReturn(description).atLeastOnce();
      replay(template);
      ParseVAppTemplateDescriptionToGetDefaultLoginCredentials converter = new ParseVAppTemplateDescriptionToGetDefaultLoginCredentials();
      Credentials creds = converter.execute(template);
      assertEquals(creds.identity, "vpncubed");
      assertEquals(creds.credential, "vpncubed");
      verify(template);
   }
   
   @Test
   public void testWindows() throws IOException {
      InputStream is = getClass().getResourceAsStream("/terremark/windows_description.txt");
      String description = new String(ByteStreams.toByteArray(is));
      VCloudExpressVAppTemplate template = createMock(VCloudExpressVAppTemplate.class);
      expect(template.getDescription()).andReturn(description).atLeastOnce();
      replay(template);
      ParseVAppTemplateDescriptionToGetDefaultLoginCredentials converter = new ParseVAppTemplateDescriptionToGetDefaultLoginCredentials();
      Credentials creds = converter.execute(template);
      assertEquals(creds.identity, "Administrator");
      assertEquals(creds.credential, null);
      verify(template);
   }
}
