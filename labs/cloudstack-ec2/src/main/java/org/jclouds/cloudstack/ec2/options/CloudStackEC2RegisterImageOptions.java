package org.jclouds.cloudstack.ec2.options;

import org.jclouds.ec2.options.internal.BaseEC2RequestOptions;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created with IntelliJ IDEA.
 * User: anshul
 * Date: 11/29/12
 * Time: 3:35 PM
 * To change this template use File | Settings | File Templates.
 */
public class CloudStackEC2RegisterImageOptions extends BaseEC2RequestOptions {

    /**
     * The architecture of the image.
     */
    public CloudStackEC2RegisterImageOptions asArchitecture(String architecture) {
        formParameters.put("Architecture", checkNotNull(architecture, "architecture"));
        return this;
    }

    String getArchitecture() {
        return getFirstFormOrNull("Architecture");
    }

    /**
     *The description of the AMI. "Up to 255 characters."
     */
    public CloudStackEC2RegisterImageOptions withDescription(String info) {
        formParameters.put("Description", checkNotNull(info, "info"));
        return this;
    }

    String getDescription() {
        return getFirstFormOrNull("Description");
    }

    /**
     * The ID of the kernel to select.
     */
    public CloudStackEC2RegisterImageOptions withKernelId(String kernelId) {
        formParameters.put("KernelId", checkNotNull(kernelId, "kernelId"));
        return this;
    }

    String getKernelId() {
        return getFirstFormOrNull("KernelId");
    }

    /**
     * The ID of the RAM disk to select. Some kernels require additional drivers at launch. Check the
     * kernel requirements for information on whether you need to specify a RAM disk. To find kernel
     * requirements, refer to the Resource Center and search for the kernel ID.
     */
    public CloudStackEC2RegisterImageOptions withRamdisk(String ramDiskId) {
        formParameters.put("RamdiskId", checkNotNull(ramDiskId, "ramDiskId"));
        return this;
    }

    String getRamdiskId() {
        return getFirstFormOrNull("RamdiskId");
    }

    public static class Builder {
        /**
         * @see CloudStackEC2RegisterImageOptions#asArchitecture(String)
         */
        public static CloudStackEC2RegisterImageOptions asArchitecture(String architecture) {
            CloudStackEC2RegisterImageOptions options = new CloudStackEC2RegisterImageOptions();
            return options.asArchitecture(architecture);
        }

        /**
         * @see CloudStackEC2RegisterImageOptions#withDescription(String)
         */
        public static CloudStackEC2RegisterImageOptions withDescription(String additionalInfo) {
            CloudStackEC2RegisterImageOptions options = new CloudStackEC2RegisterImageOptions();
            return options.withDescription(additionalInfo);
        }

        /**
         * @see CloudStackEC2RegisterImageOptions#withKernelId(String)
         */
        public static CloudStackEC2RegisterImageOptions withKernelId(String kernelId) {
            CloudStackEC2RegisterImageOptions options = new CloudStackEC2RegisterImageOptions();
            return options.withKernelId(kernelId);
        }

        /**
         * @see CloudStackEC2RegisterImageOptions#withRamdisk(String)
         */
        public static CloudStackEC2RegisterImageOptions withRamdisk(String ramdiskId) {
            CloudStackEC2RegisterImageOptions options = new CloudStackEC2RegisterImageOptions();
            return options.withRamdisk(ramdiskId);
        }

    }
}
