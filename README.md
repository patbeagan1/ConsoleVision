# ConsoleVideo
A kotlin demo for displaying images in the terminal.

You can read more about this project at my site:
https://patbeagan.dev/projects/consolevideo

```bash
f () {
	curl -X POST -F 'image=@'"$1" localhost:8080/upload
}
```

```bash
ff () {
	curl -X POST -F 'image=@'"$1" 3.221.34.94/upload
}
```

```bash
curl 3.221.34.94/im/eefbb5b84ef2d8824f3fcaf64c54a63a
```

# What is this project about?

With this tool, you'll be able to display images without leaving your terminal. This can be useful when you are just sshing into another machine and don't have GUI access. 

To make this more convenient, there is an option to run the tool as a server instead, so the only thing that you need installed is `curl`

# What's included?

There are 2 deliverables from this repo
- **lib** - a library that will take a bitmap and convert it to a ANSI
- **app** - a wrapper that makes the library available via CLI and as a server.

The app supports a variety of command line flags which will allow for:
- colorspace reduction
- color normalization
- image resizing
- compatibility mode (for old terminals that only support 256 colors)

Initial functionality that allowed for playing videos has been taken out. The video player that was being used, [humble](https://github.com/artclarke/humble-video/blob/master/humble-video-demos/src/main/java/io/humble/video/demos/DecodeAndPlayVideo.java),  was not able to be packaged into a shadow jar.
