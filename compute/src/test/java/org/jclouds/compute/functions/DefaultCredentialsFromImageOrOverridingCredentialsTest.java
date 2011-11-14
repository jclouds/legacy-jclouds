/**
 * Licensed to jclouds, Inc. (jclouds) under one or more
 * contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  jclouds licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.jclouds.compute.functions;

import static org.easymock.EasyMock.expect;
import static org.easymock.classextension.EasyMock.createMock;
import static org.easymock.classextension.EasyMock.replay;
import static org.easymock.classextension.EasyMock.verify;
import static org.testng.Assert.assertEquals;

import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.Template;
import org.jclouds.compute.options.TemplateOptions;
import org.jclouds.domain.Credentials;
import org.testng.annotations.Test;

/**
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "DefaultCredentialsFromImageOrOverridingCredentialsTest")
public class DefaultCredentialsFromImageOrOverridingCredentialsTest {
   private static final DefaultCredentialsFromImageOrOverridingCredentials fn = new DefaultCredentialsFromImageOrOverridingCredentials();

   public void testWhenCredentialsNotPresentInImageOrTemplateOptionsReturnNull() {
      Credentials expected = null;

      Image image = createMock(Image.class);
      Template template = createMock(Template.class);

      expect(template.getImage()).andReturn(image);
      expect(image.getDefaultCredentials()).andReturn(null);
      expect(template.getOptions()).andReturn(new TemplateOptions());

      replay(template);
      replay(image);

      assertEquals(fn.apply(template), expected);

      verify(template);
      verify(image);

   }

   public void testWhenCredentialsNotPresentInImageReturnsOneInTemplateOptions() {
      Credentials expected = new Credentials("ubuntu", "password");

      Image image = createMock(Image.class);
      Template template = createMock(Template.class);

      expect(template.getImage()).andReturn(image);
      expect(image.getDefaultCredentials()).andReturn(null);
      expect(template.getOptions()).andReturn(TemplateOptions.Builder.overrideCredentialsWith(expected));

      replay(template);
      replay(image);

      assertEquals(fn.apply(template), expected);

      verify(template);
      verify(image);

   }

   public void testWhenCredentialsNotPresentInTemplateOptionsReturnsOneInImage() {
      Credentials expected = new Credentials("ubuntu", "password");

      Image image = createMock(Image.class);
      Template template = createMock(Template.class);

      expect(template.getImage()).andReturn(image);
      expect(image.getDefaultCredentials()).andReturn(expected);
      expect(template.getOptions()).andReturn(new TemplateOptions());

      replay(template);
      replay(image);

      assertEquals(fn.apply(template), expected);

      verify(template);
      verify(image);

   }

   public void testWhenCredentialsPresentInImageOverridesIdentityFromCredentialsInTemplateOptions() {
      Credentials expected = new Credentials("ubuntu", "password");

      Image image = createMock(Image.class);
      Template template = createMock(Template.class);

      expect(template.getImage()).andReturn(image);
      expect(image.getDefaultCredentials()).andReturn(new Credentials("user", "password"));
      expect(template.getOptions()).andReturn(TemplateOptions.Builder.overrideLoginUserWith("ubuntu"));

      replay(template);
      replay(image);

      assertEquals(fn.apply(template), expected);

      verify(template);
      verify(image);

   }

   public void testWhenCredentialsPresentInImageOverridesCredentialFromCredentialsInTemplateOptions() {
      Credentials expected = new Credentials("ubuntu", "password");

      Image image = createMock(Image.class);
      Template template = createMock(Template.class);

      expect(template.getImage()).andReturn(image);
      expect(image.getDefaultCredentials()).andReturn(new Credentials("ubuntu", "password2"));
      expect(template.getOptions()).andReturn(TemplateOptions.Builder.overrideLoginCredentialWith("password"));

      replay(template);
      replay(image);

      assertEquals(fn.apply(template), expected);

      verify(template);
      verify(image);

   }
}
