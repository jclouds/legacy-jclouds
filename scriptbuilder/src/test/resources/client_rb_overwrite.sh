cat > /etc/chef/client.rb <<-'END_OF_JCLOUDS_FILE'
	log_level :info
	log_location STDOUT
	chef_server_url "http://localhost:4000"
END_OF_JCLOUDS_FILE
