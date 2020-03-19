#!/usr/bin/env sh
echo 'Bootstrapping Java-Server...'
./server/bin/server &
sleep 20
echo 'Bootstrapping Java-Daemon...'
./java-daemon/bin/java-daemon &
# Add Python Daemon start script

# wait forever not to exit the container
while true
do
  tail -f /dev/null & wait ${!}
done
