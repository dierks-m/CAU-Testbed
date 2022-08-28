if pgrep picocom; then killall -9 picocom; fi
if pgrep screen; then screen -X -S nrf52screen quit; fi
if pgrep screen; then killall -9 screen; fi
if pgrep contiki-timestamp; then killall -9 contiki-timestamp; fi
