class EbsVolumesJRuby

  def initialize access_key_id, secret_key
    @access_key_id = access_key_id
    @secret_key = secret_key
  end

  def resize instance_id, region, new_size

    @manager = org.jclouds.tools.ebsresize.InstanceVolumeManager.new(@access_key_id, @secret_key)

    ebs_api = @manager.get_ebs_api
    instance_api = @manager.get_instance_api
    api = @manager.get_api

    @instance = instance_api.get_instance_by_id_and_region instance_id, region

    instance_api.stop_instance @instance

    volume = ebs_api.get_root_volume_for_instance @instance

    ebs_api.detach_volume_from_stopped_instance volume, @instance

    new_volume = ebs_api.clone_volume_with_new_size volume, new_size

    ebs_api.attach_volume_to_stopped_instance new_volume, @instance

    instance_api.start_instance @instance

    api.get_elastic_block_store_services().delete_volume_in_region @instance.get_region(), volume.get_id()
  end

  def run_remote_resize_commands credentials, key_pair
    @manager.run_remote_resize_commands @instance, credentials, key_pair
  end

end