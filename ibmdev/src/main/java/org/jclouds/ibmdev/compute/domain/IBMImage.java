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

package org.jclouds.ibmdev.compute.domain;

import org.jclouds.compute.domain.Architecture;
import org.jclouds.compute.domain.OsFamily;
import org.jclouds.compute.domain.internal.ImageImpl;
import org.jclouds.domain.Credentials;
import org.jclouds.domain.Location;

import com.google.common.collect.ImmutableMap;

/**
 * @author Adrian Cole
 */
public class IBMImage extends ImageImpl {

   /** The serialVersionUID */
   private static final long serialVersionUID = -8520373150950058296L;

   private final org.jclouds.ibmdev.domain.Image rawImage;

   public IBMImage(org.jclouds.ibmdev.domain.Image in, Location location) {
      // TODO parse correct OS
      // TODO manifest fails to parse due to encoding issues in the path
      // TODO get correct default credentials
      // http://www-180.ibm.com/cloud/enterprise/beta/ram/community/_rlvid.jsp.faces?_rap=pc_DiscussionForum.doDiscussionTopic&_rvip=/community/discussionForum.jsp&guid={DA689AEE-783C-6FE7-6F9F-DFEE9763F806}&v=1&submission=false&fid=1068&tid=1527
      super(in.getId(), in.getName(), in.getId(), location, null, ImmutableMap.<String, String> of(), in
               .getDescription(), in.getCreatedTime().getTime() + "",
               (in.getPlatform().indexOf("Red Hat") != -1) ? OsFamily.RHEL : OsFamily.SUSE, in.getPlatform(), (in
                        .getPlatform().indexOf("32") != -1) ? Architecture.X86_32 : Architecture.X86_64,
               new Credentials("idcuser", null));
      this.rawImage = in;
   }

   public org.jclouds.ibmdev.domain.Image getRawImage() {
      return rawImage;
   }

}