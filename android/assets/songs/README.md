Tealily
=======

This is a minimal music markup language for libgdx inspired by lilypond.

Notable differences:
--------------------
* Notes don't go by proximity but a baseline (octave 4) and the "," and "'" allow one to play a note from an octave up or down. (But each note an octave up or down will need to keep the mark.)
* eb is e-flat. c# is c-sharp.
* The first line of the music file (saved with .tly extension) must have the tempo followed by the number of bars.

Example:
--------

> 100 1

> ab4. c# b4'
