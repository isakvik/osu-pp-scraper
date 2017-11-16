# osu! pp scraper
This project reads a top performance list, downloads all the maps, runs oppai.exe on them, and gives you their aim/speed/accuracy values, as well as your combined pp stats. Put your API key into ScraperConfiguration.java before use, and edit the user ID string in Program.java to whoever you want to see the performance list for.
Example output can be seen [here](https://gn.s-ul.eu/DaYo7e7m).

The JSON handling is done by the [Gson library](https://github.com/google/gson), and is needed for compiling. No dependency manager for you.

Fair warning: the code is pretty bad. 

also TODOs:
* output to HTTP instead of txt dear god
