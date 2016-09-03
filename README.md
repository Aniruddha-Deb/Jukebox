# Jukebox
Android music player app 

Jukebox is an app which plays music on your smartphone in a user-friendly manner. It is still currently a barebones work-in-project but it is envisioned in the future that jukebox will have a flat, easy to use UI with visual effects for the music being played. 

## Features 

On startup, jukebox queries the files on the user's device and lists them in a listView

![list_songs_activity](/doc/list_songs_activity.png)

Upon clicking a song, it reads and displays the song's tag data and plays the song

![song_over_the_horizon](/doc/player_activity_1.png)

## Installation

Download the build.apk file in the releases tab and install on your android smartphone. After installation, run the app by tapping on it from your desktop. 

## Bugs

It is still a broken mess, so the bugs are many (tapping on mulitple songs runs them both together) but will be fixed in upcoming builds

## TODOs

Foremost priority is to create a mediaController and control the mediaPlayer class playing the song. This will also introduce features such as play, pause, fast-forward, rewind, seek etc. Also need to fix a lot of bugs associated with the playerActivity. 

