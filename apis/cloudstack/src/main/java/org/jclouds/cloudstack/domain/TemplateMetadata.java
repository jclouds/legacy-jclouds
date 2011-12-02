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
package org.jclouds.cloudstack.domain;

import com.google.common.base.Objects;

/**
 * @author Richard Downer
 */
public class TemplateMetadata {

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String name;
        private long osTypeId;
        private String displayText;
        private Long snapshotId;
        private Long volumeId;
        private Long virtualMachineId;
        private Boolean passwordEnabled;

        /**
         * @param name
         *            the name of the template
         */
        public Builder name(String name) {
            this.name = name;
            return this;
        }

        /**
         * @param osTypeId
         *            the ID of the OS Type that best represents the OS of this template.
         */
        public Builder osTypeId(long osTypeId) {
            this.osTypeId = osTypeId;
            return this;
        }

        /**
         * @param displayText
         *            the display text of the template. This is usually used for display purposes.
         */
        public Builder displayText(String displayText) {
            this.displayText = displayText;
            return this;
        }

        /**
         * @param snapshotId
         *      the ID of the snapshot the template is being created from.
         *          Either this parameter, or volumeId has to be passed in
         */
        public Builder snapshotId(Long snapshotId) {
            this.snapshotId = snapshotId;
            return this;
        }

        /**
         * @param volumeId
         *          the ID of the disk volume the template is being created from.
         *          Either this parameter, or snapshotId has to be passed in
         */
        public Builder volumeId(Long volumeId) {
            this.volumeId = volumeId;
            return this;
        }

        /**
         * @param virtualMachineId
         *          the ID of the disk volume the template is being created from
         */
        public Builder virtualMachineId(Long virtualMachineId) {
            this.virtualMachineId = virtualMachineId;
            return this;
        }

        /**
         * the template supports the password reset feature.
         */
        public Builder passwordEnabled() {
            this.passwordEnabled = true;
            return this;
        }

        public TemplateMetadata build() {
            TemplateMetadata template = new TemplateMetadata(name, osTypeId, displayText);
            template.setPasswordEnabled(passwordEnabled);
            template.setSnapshotId(snapshotId);
            template.setVirtualMachineId(virtualMachineId);
            template.setVolumeId(volumeId);
            return template;
        }
    }

    private String name;
    private long osTypeId;
    private String displayText;

    private Long snapshotId;
    private Long volumeId;
    private Long virtualMachineId;;
    private Boolean passwordEnabled;

    public TemplateMetadata(String name, long osTypeId, String displayText) {
        this.name = name;
        this.osTypeId = osTypeId;
        this.displayText = displayText;
    }

    /**
     * present only for serializer
     */
    TemplateMetadata() {
    }

    /**
     * @return the ID of the snapshot the template is being created from
     */
    public Long getSnapshotId() {
        return snapshotId;
    }

    public void setSnapshotId(Long snapshotId) {
        this.snapshotId = snapshotId;
    }

    /**
     * @return the ID of the disk volume the template is being created from
     */
    public Long getVolumeId() {
        return volumeId;
    }

    public void setVolumeId(Long volumeId) {
        this.volumeId = volumeId;
    }

    /**
     * @return Optional, VM ID
     */
    public Long getVirtualMachineId() {
        return virtualMachineId;
    }

    public void setVirtualMachineId(Long virtualMachineId) {
        this.virtualMachineId = virtualMachineId;
    }

    /**
     * @return true if the template supports the password reset feature; default is false
     */
    public Boolean getPasswordEnabled() {
        return passwordEnabled;
    }

    public void setPasswordEnabled(Boolean passwordEnabled) {
        this.passwordEnabled = passwordEnabled;
    }

    /**
     * @return the name of the template
     */
    public String getName() {
        return name;
    }

    /**
     * @return the ID of the OS Type that best represents the OS of this template.
     */
    public long getOsTypeId() {
        return osTypeId;
    }

    /**
     * @return the display text of the template. This is usually used for display purposes.
     */
    public String getDisplayText() {
        return displayText;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        TemplateMetadata that = (TemplateMetadata) o;

        if (osTypeId != that.osTypeId)
            return false;
        if (snapshotId != null ? !snapshotId.equals(that.snapshotId) : that.snapshotId != null)
            return false;
        if (volumeId != null ? !volumeId.equals(that.volumeId) : that.volumeId != null)
            return false;
        if (virtualMachineId != null ? !virtualMachineId.equals(that.virtualMachineId) : that.virtualMachineId != null)
            return false;
        if (passwordEnabled != null ? !passwordEnabled.equals(that.passwordEnabled) : that.passwordEnabled != null)
            return false;
        if (displayText != null ? !displayText.equals(that.displayText) : that.displayText != null)
            return false;
        if (name != null ? !name.equals(that.name) : that.name != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(name, displayText, osTypeId, snapshotId, volumeId, passwordEnabled, virtualMachineId);
    }

    @Override
    public String toString() {
        return "[" + "name='" + name + '\'' + ", osTypeId=" + osTypeId + ", displayText='" + displayText + '\'' + ']';
    }

}
