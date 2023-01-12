# ConsoleVision
https://patbeagan.dev/projects/consolevideo
![Screenshot_2022-04-27_23-45-04](https://user-images.githubusercontent.com/10187351/165678333-b3c45bbe-1a8b-49ae-91ab-48feb0337482.png)

# What is this project about?

`ConsoleVision` is a library that will take a bitmap and convert it to a ANSI

With this tool, you'll be able to display images without leaving your terminal. This can be useful when you are just sshing into another machine and don't have GUI access. 

To make this more convenient, there is an option to run the tool as a server instead, so the only thing that you need installed is `curl`

# What's included?

<details>
<summary>As a Library</summary>


The library jar file includes an implementation of [ANSI](https://mudhalla.net/tintin/info/ansicolor/) for the JVM. It has some similar content to [Jansi](http://fusesource.github.io/jansi/) (which I was unaware of at the time), but it includes extensions that make it more useful for image processing.

</details>

<details>
<summary>As an App</summary>

The app supports a variety of command line flags which will allow for:
- colorspace reduction
- color normalization
- image resizing
- compatibility mode (for old terminals that only support 256 colors)

<img width="611" alt="Screen Shot 2022-02-05 at 8 43 56 AM" src="https://user-images.githubusercontent.com/10187351/152646523-cf415c81-47cf-45e5-9a27-600ac00789d4.png">

|Normalized Colors| Default| Custom palette|Reduced colorspace|
|-|-|-|-|
|<img width="705" alt="Screen Shot 2022-02-05 at 8 51 15 AM" src="https://user-images.githubusercontent.com/10187351/152647110-105c8015-f7a7-4a98-aaff-6947722651b6.png">|<img width="700" alt="Screen Shot 2022-02-05 at 8 50 53 AM" src="https://user-images.githubusercontent.com/10187351/152647111-787eeef5-dd59-4ef8-8e0d-47678f44f953.png">|<img width="719" alt="Screen Shot 2022-02-05 at 9 03 37 AM" src="https://user-images.githubusercontent.com/10187351/152647252-c9035db3-a684-4818-8455-f917dade6700.png">|<img width="717" alt="Screen Shot 2022-02-05 at 9 02 59 AM" src="https://user-images.githubusercontent.com/10187351/152647253-1c6b5be0-bb7f-4b58-a98e-ba10c253106a.png">|

</details>

<details>
<summary>As a Server</summary>

Running the tool as a server will allow you to use a limited feature set of the command line tool, in a more convenient way. 
- To upload a photo, POST to the `/upload` endpoint. You'll receive an image hash. 
- To retrieve a photo, GET to the `/im/{id}` endpoint, using an image hash.
- To retrieve the last photo, GET to the `/last` endpoint
- To retrieve a random photo, GET to the `/random` endpoint

| Retreival by image id | Uploading an image | Retrieval of random, previously uploaded image | Retrieval of last uploaded image |
|-|-|-|-|
|<img width="411" alt="Screen Shot 2022-02-05 at 8 28 51 AM" src="https://user-images.githubusercontent.com/10187351/152645913-c084b2a2-b985-4981-b871-e2c68371237a.png">|<img width="786" alt="Screen Shot 2022-02-05 at 8 35 50 AM" src="https://user-images.githubusercontent.com/10187351/152646215-276c0f01-3d00-4f2d-8ee5-b0d622cfc2d2.png">|<img width="636" alt="Screen Shot 2022-02-05 at 8 30 56 AM" src="https://user-images.githubusercontent.com/10187351/152645966-a8441a25-af4a-4c81-952e-7e3fdb38b575.png">|<img width="815" alt="Screen Shot 2022-02-05 at 8 38 25 AM" src="https://user-images.githubusercontent.com/10187351/152646331-2a8cbee6-907c-4573-8d13-71dadf66b59e.png">|


You can add the following to your ~/.bashrc or ~/.zshrc file to add an upload command.
Replace localhost with the server you are accessing. 

```bash
cvupload () {
	curl -X POST -F 'image=@'"${'$'}1" localhost:3000/upload
}
cvimage () {
	curl localhost:3000/im/"${'$'}1"
}
```

<img width="599" alt="Screen Shot 2022-02-05 at 11 37 36 AM" src="https://user-images.githubusercontent.com/10187351/152652505-d409c54e-ac4b-47c6-8d13-a64872511b6e.png">

---

I have a server where this is deployed as well, if you just want to test it out. 
```bash
curl 3.221.34.94/im/eefbb5b84ef2d8824f3fcaf64c54a63a
```
</details>

---

## Other things to note? 

Initial functionality that allowed for playing videos has been taken out. The video player that was being used, [humble](https://github.com/artclarke/humble-video/blob/master/humble-video-demos/src/main/java/io/humble/video/demos/DecodeAndPlayVideo.java),  was not able to be packaged into a shadow jar.

## Roadmap

- [] Convert all functionality to multiplatform
- [] Display as [quad blocks](https://en.wikipedia.org/wiki/Block_Elements)
- [] Create graphics library
- [] Standardize screen size
- [] Handle click events
- [] Handle keyboard input
- [] More filters
- [] Widget library
- [] Publish to package managers
- [] Only rerender screen diff
- [] Verify terminal compatibility
- [] Gif support
- [] Pass compression style in as an argument