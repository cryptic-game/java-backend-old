#!/usr/bin/env sh

echo 'Starting Java-Server...'
./server/bin/server --env &

sleep 20

echo 'Starting Java-Daemon...'
./java-daemon/bin/java-daemon --env &

echo 'Starting Python-Daemon...'
# Add Python Daemon start script

# wait forever not to exit the container
while true; do
  tail -f /dev/null &
  wait ${!}
done
