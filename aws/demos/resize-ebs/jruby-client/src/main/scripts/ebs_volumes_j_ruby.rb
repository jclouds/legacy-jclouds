=begin
    Copyright (C) 2010 Cloud Conscious, LLC. <info@cloudconscious.com>

    ====================================================================
    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
    ====================================================================
=end

=begin
    For more information, refer to:
    http://alestic.com/2009/12/ec2-ebs-boot-resize
=end
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