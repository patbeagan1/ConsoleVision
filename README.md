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
