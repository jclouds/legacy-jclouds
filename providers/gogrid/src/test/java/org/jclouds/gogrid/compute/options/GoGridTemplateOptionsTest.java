/**
 *
 * Copyright (C) 2011 Cloud Conscious, LLC. <info@cloudconscious.com>
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
package org.jclouds.gogrid.compute.options;

import static org.jclouds.gogrid.compute.options.GoGridTemplateOptions.Builder.*;
import static org.testng.Assert.*;

import org.jclouds.compute.options.TemplateOptions;
import org.jclouds.gogrid.domain.IpType;
import org.testng.annotations.Test;

/**
 * Tests possible uses of {@code GoGridTemplateOptions} and {@code GoGridTemplateOptions.Builder.*}
 * with {@link IpType} option.
 * 
 * @author Andrew Kennedy
 */
public class GoGridTemplateOptionsTest {
   @Test
   public void testipTypePrivate() {
      GoGridTemplateOptions options = new GoGridTemplateOptions();
      options.ipType(IpType.PRIVATE);
      assertEquals(options.getIpType(), IpType.PRIVATE);
   }

   @Test
   public void testipTypePublic() {
      GoGridTemplateOptions options = ipType(IpType.PUBLIC);
      assertEquals(options.getIpType(), IpType.PUBLIC);
   }

   public void testAs() {
      TemplateOptions options = new GoGridTemplateOptions();
      assertEquals(options.as(GoGridTemplateOptions.class), options);
   }

   @Test
   public void testNullIpType() {
      GoGridTemplateOptions options = new GoGridTemplateOptions();
      assertEquals(options.getIpType(), null);
   }
}
