if pgrep picocom; then
  killall -9 picocom
fi

if pgrep screen; then
  screen -X -S zoulscreen quit
fi

if pgrep serial_forwarder; then
  killall -9 serial_forwarder
fi

if pgrep cat; then
  killall -9 cat
fi

if pgrep netcat; then
  killall -9 netcat
fi

if pgrep tee; then
  killall -9 tee
fi
