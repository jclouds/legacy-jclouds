package org.jclouds.aws.ec2.binders;

import org.jclouds.aws.ec2.domain.BlockDeviceMapping;
import org.jclouds.aws.ec2.domain.RunningInstance;
import org.jclouds.aws.ec2.domain.UserIdGroupPair;
import org.jclouds.http.HttpRequest;
import org.jclouds.rest.Binder;
import org.jclouds.rest.internal.GeneratedHttpRequest;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static java.lang.String.format;

/**
 * @author Oleksiy Yarmula
 */
public class BindBlockDeviceMappingToIndexedFormParams implements Binder {

    private final String deviceNamePattern = "BlockDeviceMapping.%d.DeviceName";
    private final String volumeIdPattern = "BlockDeviceMapping.%d.Ebs.VolumeId";
    private final String deleteOnTerminationPattern = "BlockDeviceMapping.%d.Ebs.DeleteOnTermination";

    @SuppressWarnings("unchecked")
    public void bindToRequest(HttpRequest request, Object input) {
        checkArgument(checkNotNull(request, "request") instanceof GeneratedHttpRequest,
                "this binder is only valid for GeneratedHttpRequests");
        checkArgument(checkNotNull(input, "input") instanceof BlockDeviceMapping,
                "this binder is only valid for BlockDeviceMapping");
        BlockDeviceMapping blockDeviceMapping = (BlockDeviceMapping) input;
        GeneratedHttpRequest generatedRequest = (GeneratedHttpRequest) request;

        int amazonOneBasedIndex = 1; //according to docs, counters must start with 1
        for(RunningInstance.EbsBlockDevice ebsBlockDevice : blockDeviceMapping.getEbsBlockDevices()) {

            //not null by contract
            generatedRequest.addFormParam(format(volumeIdPattern, amazonOneBasedIndex),
                        ebsBlockDevice.getVolumeId());

            if(ebsBlockDevice.getDeviceName() != null) {
                generatedRequest.addFormParam(format(deviceNamePattern, amazonOneBasedIndex),
                        ebsBlockDevice.getDeviceName());
            }

            generatedRequest.addFormParam(format(deleteOnTerminationPattern, amazonOneBasedIndex),
                        String.valueOf(ebsBlockDevice.isDeleteOnTermination()));


            amazonOneBasedIndex++;
        }

    }

}
