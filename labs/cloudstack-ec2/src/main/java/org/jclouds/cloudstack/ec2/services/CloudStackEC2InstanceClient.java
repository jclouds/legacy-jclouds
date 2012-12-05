package org.jclouds.cloudstack.ec2.services;

import org.jclouds.concurrent.Timeout;
import org.jclouds.ec2.domain.Reservation;
import org.jclouds.ec2.domain.RunningInstance;
import org.jclouds.ec2.options.RunInstancesOptions;
import org.jclouds.ec2.services.InstanceClient;
import org.jclouds.javax.annotation.Nullable;

import java.util.concurrent.TimeUnit;

/**
 * Created with IntelliJ IDEA.
 * User: anshul
 * Date: 11/28/12
 * Time: 11:05 AM
 * To change this template use File | Settings | File Templates.
 */
@Timeout(duration = 90, timeUnit = TimeUnit.SECONDS)
public interface CloudStackEC2InstanceClient extends InstanceClient {
    /**
     * {@inheritDoc}
     */
    @Override
    @Timeout(duration = 10, timeUnit = TimeUnit.MINUTES)
    Reservation<? extends RunningInstance> runInstancesInRegion(@Nullable String region,
                                                                @Nullable String nullableAvailabilityZone, String imageId,
                                                                int minCount, int maxCount, RunInstancesOptions... options);

}
