# songstarters

A tool for creating randomized, algorithmic percussion tracks.

Example output: http://cynerva.github.io/songstarters/example.ogg

The output usually isn't very interesting on its own, but it can sound a lot
more interesting when you overlay multiple tracks on top of eachother. For now,
though, that's outside the scope of this project.

This was designed for my own personal use, so it's a bit rough around the
edges. However, if you're feeling adventurous, feel free to have a go at it!

## Dependencies

Songstarters is written in Lua, and depends on ffmpeg as well.

On ubuntu:
```
sudo apt install lua5.2 ffmpeg
```

## Usage

For songstarters to work, you'll have to add some samples (drums, perhaps):

```
mkdir sounds
cp /path/to/kick.wav sounds
cp /path/to/snare.wav sounds
...
```

Once you have samples, you should be able to run it:
```
./run
```

This will create a `render.flac` file, and then play it to speakers as well.

The `run` script doesn't support any args, so you'll have to edit it to change
any parameters.

## License

Songstarters is made available under a
[CC0 1.0 Universal](https://creativecommons.org/publicdomain/zero/1.0/)
license.
