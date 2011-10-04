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
package org.jclouds.aws.ec2.domain;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @see <a href= "http://docs.amazonwebservices.com/AWSEC2/latest/APIReference/ApiReference-ItemType-TagSetItemType.html" />
 * @author grkvlt@apache.org
 */
public class Tag implements Comparable<Tag> {
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String resourceId;
        private String resourceType;
        private String key;
        private String value;

        public void clear() {
            this.resourceId = null;
            this.resourceType = null;
            this.key = null;
            this.value = null;
        }

        public Builder resourceId(String resourceId) {
            this.resourceId = resourceId;
            return this;
        }

        public Builder resourceType(String resourceType) {
            this.resourceType = resourceType;
            return this;
        }

        public Builder key(String key) {
            this.key = key;
            return this;
        }

        public Builder value(String value) {
            this.value = value;
            return this;
        }

        public Tag build() {
            return new Tag(resourceId, resourceType, key, value);
        }
    }

    private final String resourceId;
    private final String resourceType;
    private final String key;
    private final String value;

    public Tag(String resourceId, String resourceType, String key, String value) {
        this.resourceId = checkNotNull(resourceId, "resourceId");
        this.resourceType = checkNotNull(resourceType, "resourceType");
        this.key = checkNotNull(key, "key");
        this.value = checkNotNull(value, "value");
    }

    public String getResourceId() {
        return resourceId;
    }

    public String getResourceType() {
        return resourceType;
    }

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }

    @Override
    public int compareTo(Tag t) {
        return key.compareTo(t.key);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((resourceId == null) ? 0 : resourceId.hashCode());
        result = prime * result + ((resourceType == null) ? 0 : resourceType.hashCode());
        result = prime * result + ((key == null) ? 0 : key.hashCode());
        result = prime * result + ((value == null) ? 0 : value.hashCode());
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
        Tag other = (Tag) obj;
        if (resourceId == null) {
            if (other.resourceId != null)
                return false;
        } else if (!resourceId.equals(other.resourceId))
            return false;
        if (resourceType == null) {
            if (other.resourceType != null)
                return false;
        } else if (!resourceType.equals(other.resourceType))
            return false;
        if (value == null) {
            if (other.value != null)
                return false;
        } else if (!value.equals(other.value))
            return false;
        if (key == null) {
            if (other.key != null)
                return false;
        } else if (!key.equals(other.key))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "[resourceId=" + resourceId + ", resourceType=" + resourceType + ", key=" + key + ", value=" + value + "]";
    }
}