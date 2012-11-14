package org.jclouds.openstack.cinder.v1.features;

import java.util.concurrent.TimeUnit;

import org.jclouds.concurrent.Timeout;
import org.jclouds.openstack.cinder.v1.domain.Volume;
import org.jclouds.openstack.cinder.v1.options.CreateVolumeOptions;

import com.google.common.collect.FluentIterable;

/**
 * Provides synchronous access to Volumes.
 * 
 * This API strictly handles creating and managing Volumes. To attach a Volume to a Server you need to use the
 * @see VolumeAttachmentApi
 * 
 * @see VolumeAsyncApi
 * @see <a href="http://api.openstack.org/">API Doc</a>
 * @author Everett Toews
 */
@Timeout(duration = 180, timeUnit = TimeUnit.SECONDS)
public interface VolumeApi {
   /**
    * Returns a summary list of Volumes.
    *
    * @return The list of Volumes
    */
   FluentIterable<? extends Volume> list();

   /**
    * Returns a detailed list of Volumes.
    *
    * @return The list of Volumes
    */
   FluentIterable<? extends Volume> listInDetail();

   /**
    * Return data about the given Volume.
    *
    * @param volumeId Id of the Volume
    * @return Details of a specific Volume
    */
   Volume get(String volumeId);

   /**
    * Creates a new Volume
    * 
    * @param volumeId Id of the Volume
    * @param options See CreateVolumeOptions
    * @return The new Volume
    */
   Volume create(int sizeGB, CreateVolumeOptions... options);

   /**
    * Delete a Volume. The Volume status must be Available or Error.
    *
    * @param volumeId Id of the Volume
    * @return true if successful, false otherwise
    */
   boolean delete(String volumeId);
}
