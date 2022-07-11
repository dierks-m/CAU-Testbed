if pgrep picocom; then
    killall -9 picocom;
fi

if pgrep screen; then
    screen -X -S zoulscreen quit;
fi