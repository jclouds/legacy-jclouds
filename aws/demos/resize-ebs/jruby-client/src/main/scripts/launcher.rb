require "src/main/scripts/ebs_volumes_j_ruby.rb"

instance_id = "AMAZON_INSTANCE_ID (i-xxxxxx)"
region = org.jclouds.aws.domain.Region::US_EAST_1
new_size = 6
remote_login = "ubuntu"
remote_password = ""
path_to_key = ""

ebs_volumes = EbsVolumesJRuby.new("YOUR_ACCESS_KEY_ID", "YOUR_SECRET_KEY")

ebs_volumes.resize instance_id, region, new_size

key_pair = org.jclouds.util.Utils::toStringAndClose(
            java.io.FileInputStream.new(path_to_key))
credentials = org.jclouds.domain.Credentials.new(remote_login, remote_password)

java.lang.Thread::sleep(25000);

ebs_volumes.run_remote_resize_commands credentials, key_pair