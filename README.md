# Jukebox
Android music player app 

Jukebox is an app which plays music on your smartphone in a user-friendly manner. It is still currently a barebones work-in-project but it is envisioned in the future that jukebox will have a flat, easy to use UI with visual effects for the music being played. 

## Features 

On startup, jukebox queries the files on the user's device and lists them in a listView

![list_songs_activity](/doc/list_songs_activity.png)

Upon clicking a song, it reads and displays the song's tag data and plays the song

![song_over_the_horizon](/doc/player_activity_slider.png)

## Installation

Download the [build.apk](https://github.com/Aniruddha-Deb/Jukebox/releases/download/v0.0-indev/build.apk) file and install on your android smartphone. After installation, run the app by tapping on it from your desktop. 

## Bugs

The slider thumb in low-res phones (such as my samsung gt-i9060) is misplaced. This does not occur in hi-res phones (oneplus 2, moto x) and needs to be fixed

## TODOs

Make a service which plays the music when the activity is stopped and the screen is locked.
