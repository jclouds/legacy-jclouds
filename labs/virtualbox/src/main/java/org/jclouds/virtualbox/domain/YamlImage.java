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
package org.jclouds.virtualbox.domain;

import static org.jclouds.compute.util.ComputeServiceUtils.parseOsFamilyOrUnrecognized;

import java.util.List;
import java.util.Map;

import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.ImageBuilder;
import org.jclouds.compute.domain.OperatingSystem;
import org.jclouds.compute.domain.OsFamily;
import org.jclouds.domain.LoginCredentials;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * Serializes to the following
 * 
 * <pre>
 * id: myTestId
 *       name: ubuntu-11.04-server-i386
 *       description: ubuntu 11.04 server (i386)
 *       os_arch: x86
 *       os_family: linux
 *       os_description: ubuntu
 *       os_version: 11.04
 *       iso: http://releases.ubuntu.com/11.04/ubuntu-11.04-server-i386.iso
 *       keystroke_sequence: |
 *                 <Esc><Esc><Enter> 
 *                 /install/vmlinuz noapic preseed/url=http://10.0.2.2:8080/src/test/resources/preseed.cfg 
 *                 debian-installer=en_US auto locale=en_US kbd-chooser/method=us 
 *                 hostname=vmName 
 *                 fb=false debconf/frontend=noninteractive 
 *                 keyboard-configuration/layout=USA keyboard-configuration/variant=USA console-setup/ask_detect=false 
 *                 initrd=/install/initrd.gz -- <Enter>
 *       preseed_cfg: |                
 *                       ## Options to set on the command line
 *                       d-i debian-installer/locale string en_US.utf8
 *                       d-i console-setup/ask_detect boolean false
 *                       d-i console-setup/layout string USA
 *                       d-i netcfg/get_hostname string unassigned-hostname
 *                       d-i netcfg/get_domain string unassigned-domain
 *                       # Continue without a default route
 *                       # Not working , specify a dummy in the DHCP
 *                       d-i time/zone string UTC
 *                       d-i clock-setup/utc-auto boolean true
 *                       d-i clock-setup/utc boolean true
 *                       d-i kbd-chooser/method	select	American English
 *                       d-i netcfg/wireless_wep string
 *                       d-i base-installer/kernel/override-image string linux-server
 *                       # Choices: Dialog, Readline, Gnome, Kde, Editor, Noninteractive
 *                       d-i debconf debconf/frontend select Noninteractive
 *                       d-i pkgsel/install-language-support boolean false
 *                       tasksel tasksel/first multiselect standard, ubuntu-server
 *                       d-i partman-auto/method string lvm
 *                       d-i partman-lvm/confirm boolean true
 *                       d-i partman-lvm/device_remove_lvm boolean true
 *                       d-i partman-auto/choose_recipe select atomic
 *                       d-i partman/confirm_write_new_label boolean true
 *                       d-i partman/confirm_nooverwrite boolean true
 *                       d-i partman/choose_partition select finish
 *                       d-i partman/confirm boolean true
 *                       # Write the changes to disks and configure LVM?
 *                       d-i partman-lvm/confirm boolean true
 *                       d-i partman-lvm/confirm_nooverwrite boolean true
 *                       d-i partman-auto-lvm/guided_size string max
 *                       ## Default user, we can get away with a recipe to change this
 *                       d-i passwd/user-fullname string toor
 *                       d-i passwd/username string toor
 *                       d-i passwd/user-password password password
 *                       d-i passwd/user-password-again password password
 *                       d-i user-setup/encrypt-home boolean false
 *                       d-i user-setup/allow-password-weak boolean true
 *                       # Individual additional packages to install
 *                       d-i pkgsel/include string openssh-server ntp
 *                       # Whether to upgrade packages after debootstrap.
 *                       # Allowed values: none, safe-upgrade, full-upgrade
 *                       d-i pkgsel/upgrade select full-upgrade
 *                       d-i grub-installer/only_debian boolean true
 *                       d-i grub-installer/with_other_os boolean true
 *                       d-i finish-install/reboot_in_progress note
 *                       #For the update
 *                       d-i pkgsel/update-policy select none
 *                       # debconf-get-selections --install
 *                       #Use mirror
 *                       choose-mirror-bin mirror/http/proxy string
 * </pre>
 * 
 * @author Kelvin Kakugawa
 * @author Adrian Cole
 */
public class YamlImage {
   public String id;
   public String name;
   public String description;
   public String hostname;
   public String location_id;
   public String os_arch;
   public String os_family;
   public String os_description;
   public String os_version;
   public String iso_md5;
   public String iso;
   public String keystroke_sequence;
   public String preseed_cfg;
   public int login_port = 22;
   public boolean os_64bit;
   public String group;
   public List<String> tags = Lists.newArrayList();
   public Map<String, String> metadata = Maps.newLinkedHashMap();
   public String username;
   public String credential;
   public String credential_url;
   public String sudo_password;

   public static Function<YamlImage, Image> toImage = new Function<YamlImage, Image>() {
      @Override
      public Image apply(YamlImage arg0) {
         if (arg0 == null)
            return null;
         
         OsFamily family = parseOsFamilyOrUnrecognized(arg0.os_family);

         OperatingSystem operatingSystem = OperatingSystem.builder().description(arg0.os_description).family(family)
                  .version(arg0.os_version).is64Bit(arg0.os_64bit).arch(arg0.os_arch).build();

         return new ImageBuilder().id(arg0.id).name(arg0.name).description(arg0.description)
                  .operatingSystem(operatingSystem).status(Image.Status.AVAILABLE)
                  .defaultCredentials(new LoginCredentials(arg0.username, arg0.credential, null, true))
                  .build();
      }
   };

   public Image toImage() {
      return toImage.apply(this);
   }

   @Override
   public String toString() {
      return "YamlImage [id=" + id + ", name=" + name + ", description=" + description + ", hostname=" + hostname
               + ", location_id=" + location_id + ", os_arch=" + os_arch + ", os_family=" + os_family
               + ", os_description=" + os_description + ", os_version=" + os_version + ", iso=" + iso 
               + ", keystroke_sequence=" + keystroke_sequence + ", preseed_cfg=" + preseed_cfg + ", login_port="
               + login_port + ", os_64bit=" + os_64bit + ", group=" + group + ", tags=" + tags + ", metadata="
               + metadata + ", username=" + username + ", credential=" + credential + ", credential_url="
               + credential_url + ", sudo_password=" + sudo_password + "]";
   }
}
