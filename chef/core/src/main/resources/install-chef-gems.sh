if [ ! -f /usr/bin/chef-client ]; then
  apt-get update
  apt-get install -y ruby ruby1.8-dev build-essential wget libruby-extras libruby1.8-extras
  mkdir -p /tmp/bootchef
  (
    cd /tmp/bootchef
    wget http://rubyforge.org/frs/download.php/69365/rubygems-1.3.6.tgz
    tar xvf rubygems-1.3.6.tgz
    cd rubygems-1.3.6
    ruby setup.rb
    cp /usr/bin/gem1.8 /usr/bin/gem
  )
  rm -rf /tmp/bootchef
  gem install chef ohai --no-rdoc --no-ri --verbose
fi
