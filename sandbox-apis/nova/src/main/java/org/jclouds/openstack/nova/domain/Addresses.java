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
package org.jclouds.openstack.nova.domain;

import com.google.common.collect.Sets;
import com.google.gson.annotations.SerializedName;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author Adrian Cole
 */
public class Addresses {

    @SerializedName("public")
    private Set<Map<String, String>> publicAddresses = Sets.newHashSet();
    @SerializedName("private")
    private Set<Map<String, String>> privateAddresses = Sets.newHashSet();

    public Addresses() {
    }

//   public Addresses(Set<Map<String, String>> publicAddresses, Set<Map<String, String>> privateAddresses) {
//      this.publicAddresses = publicAddresses;
//      this.privateAddresses = privateAddresses;
//   }

    public Addresses(Set<String> publicAddresses, Set<String> privateAddresses) {
        this.publicAddresses.clear();
        this.privateAddresses.clear();
        for (String address : publicAddresses) {
            HashMap<String, String> addressMap = new HashMap<String, String>();
            addressMap.put("version", "4");
            addressMap.put("addr", "address");
            this.publicAddresses.add(addressMap);
        }
        for (String address : privateAddresses) {
            HashMap<String, String> addressMap = new HashMap<String, String>();
            addressMap.put("version", "4");
            addressMap.put("addr", "address");
            this.privateAddresses.add(addressMap);
        }

    }

    public void setPublicAddresses(Set<Map<String, String>> publicAddresses) {
        this.publicAddresses = publicAddresses;
    }

    public Set<String> getPublicAddresses() {
        HashSet<String> addresses = new HashSet<String>();
        for (Map<String, String> address : publicAddresses) {
            if (address.containsKey("addr")) {
                addresses.add(address.get("addr"));
            }
        }
        return addresses;
    }

    public void setPrivateAddresses(Set<Map<String, String>> privateAddresses) {
        this.privateAddresses = privateAddresses;
    }

    public Set<String> getPrivateAddresses() {
        HashSet<String> addresses = new HashSet<String>();
        for (Map<String, String> address : privateAddresses) {
            if (address.containsKey("addr")) {
                addresses.add(address.get("addr"));
            }
        }
        return addresses;
    }

    @Override
    public String toString() {
        return "Addresses [privateAddresses=" + privateAddresses + ", publicAddresses="
                + publicAddresses + "]";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((privateAddresses == null) ? 0 : privateAddresses.hashCode());
        result = prime * result + ((publicAddresses == null) ? 0 : publicAddresses.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Addresses other = (Addresses) obj;
        if (privateAddresses == null) {
            if (other.privateAddresses != null)
                return false;
        } else if (!privateAddresses.equals(other.privateAddresses))
            return false;
        if (publicAddresses == null) {
            if (other.publicAddresses != null)
                return false;
        } else if (!publicAddresses.equals(other.publicAddresses))
            return false;
        return true;
    }

}
