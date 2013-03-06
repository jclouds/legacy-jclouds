if ! hash gem 2>/dev/null; then
(
mkdir /tmp/$$
curl -q -s -S -L --connect-timeout 10 --max-time 600 --retry 20 -X GET  http://production.cf.rubygems.org/rubygems/rubygems-1.8.10.tgz |(mkdir -p /tmp/$$ &&cd /tmp/$$ &&tar -xpzf -)
mkdir -p /tmp/rubygems
mv /tmp/$$/*/* /tmp/rubygems
rm -rf /tmp/$$
cd /tmp/rubygems
ruby setup.rb --no-format-executable
rm -fr /tmp/rubygems
)
fi
gem update --system
gem update --no-rdoc --no-ri
