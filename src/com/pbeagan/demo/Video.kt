package com.pbeagan.demo

import com.pbeagan.demo.TerminalColorStyle.CURSOR_TO_START
import io.humble.video.Decoder
import io.humble.video.Demuxer
import io.humble.video.DemuxerStream
import io.humble.video.Global
import io.humble.video.MediaDescriptor
import io.humble.video.MediaPacket
import io.humble.video.MediaPicture
import io.humble.video.Rational
import io.humble.video.awt.ImageFrame
import io.humble.video.awt.MediaPictureConverter
import io.humble.video.awt.MediaPictureConverterFactory
import java.awt.image.BufferedImage
import java.io.File
import java.io.IOException
import javax.imageio.ImageIO

class DecodeAndPlayVideo(private val filename: String?, palette: String?) {
    private val demuxer: Demuxer = getDemuxer(filename!!)
    private val paletteImage = palette?.let { ImageIO.read(File(palette)) }
    private val paletteColors = paletteImage?.let {
        val colorSet = mutableSetOf<Int>()
        (it.minY until it.height).forEach { y ->
            (it.minX until it.width).forEach { x ->
                colorSet.add(it.getRGB(x, y))
            }
        }

        colorSet
    }
    //    private val paletteColors by lazy {
//        paletteImage?.let {
////        val colorSet = mutableSetOf<Int>()
////        (it.minY..it.height).forEach { y ->
////            (it.minX..it.width).forEach { x ->
////                colorSet.add(it.getRGB(x, y))
////            }
////        }
//            it.getRGB(
//                0,
//                0,
//                it.width,
//                it.height,
//                null,
//                0,
//                1
//            ).toSet()
////        colorSet
//        }
//    }
    private val numStreams: Int = demuxer.numStreams
    var videoStreamId = -1
    var streamStartTime: Long = Global.NO_PTS
    lateinit var videoDecoder: Decoder

    var image: BufferedImage? = null

//    private val window: ImageFrame = ImageFrame.make()
//        ?: throw RuntimeException("Attempting this demo on a headless machine, and that will not work. Sad day for you.")

    val systemStartTime = System.nanoTime()
    private val systemTimeBase: Rational = Rational.make(1, 1_000_000_000)
    private val streamTimebase: Rational by lazy { videoDecoder.timeBase }

    /**
     * Opens a file, and plays the video from it on a screen at the right rate.
     */
    @Throws(InterruptedException::class, IOException::class)
    fun playVideo() {
        initializeDecoder()
        videoDecoder.open(null, null)
        val picture: MediaPicture = MediaPicture.make(
            videoDecoder.width,
            videoDecoder.height,
            videoDecoder.pixelFormat
        )
        val converter: MediaPictureConverter = MediaPictureConverterFactory.createConverter(
            MediaPictureConverterFactory.HUMBLE_BGR_24,
            picture)

        runDecodingLoop(picture, converter)
        flushDecoder(picture, converter)
        demuxer.close()
//        window.dispose()
    }

    private fun initializeDecoder() {
        for (i in 0 until numStreams) {
            val stream: DemuxerStream = demuxer.getStream(i)
            streamStartTime = stream.startTime
            val decoder: Decoder = stream.decoder

            if (decoder.codecType === MediaDescriptor.Type.MEDIA_VIDEO) {
                videoStreamId = i
                videoDecoder = decoder
                // stop at the first one.
                break
            }
        }
        if (videoStreamId == -1) throw RuntimeException("could not find video stream in container: $filename")
    }

    private fun runDecodingLoop(
        picture: MediaPicture,
        converter: MediaPictureConverter
    ) {
        /**
         * Now, we start walking through the container looking at each packet. This
         * is a decoding loop, and as you work with Humble you'll write a lot
         * of these.
         *
         * Notice how in this loop we reuse all of our objects to avoid
         * reallocating them. Each call to Humble resets objects to avoid
         * unnecessary reallocation.
         */
        val packet: MediaPacket = MediaPacket.make()
        while (demuxer.read(packet) >= 0) {
            /**
             * Now we have a packet, let's see if it belongs to our video stream
             */
            if (packet.streamIndex == videoStreamId) {
                /**
                 * A packet can actually contain multiple sets of samples (or frames of samples
                 * in decoding speak).  So, we may need to call decode  multiple
                 * times at different offsets in the packet's data.  We capture that here.
                 */
                var offset = 0
                var bytesRead = 0
                do {
                    bytesRead += videoDecoder.decode(picture, packet, offset)
                    if (picture.isComplete) {
                        image = displayVideoAtCorrectTime(
                            picture,
                            converter
                        )
                    }
                    offset += bytesRead
                } while (offset < packet.size)
            }
        }
    }

    private fun flushDecoder(picture: MediaPicture, converter: MediaPictureConverter) {
        do {
            videoDecoder.decode(picture, null, 0)
            if (picture.isComplete) {
                image = displayVideoAtCorrectTime(
                    picture,
                    converter
                )
            }
        } while (picture.isComplete)
    }

    private fun getDemuxer(filename: String): Demuxer {
        val demuxer: Demuxer = Demuxer.make()
        demuxer.open(
            filename,
            null,
            false,
            true,
            null,
            null
        )
        return demuxer
    }

    private val imagePrinter = ImagePrinter()

    /**
     * Takes the video picture and displays it at the right time.
     */
    @Throws(InterruptedException::class)
    private fun displayVideoAtCorrectTime(
        picture: MediaPicture, converter: MediaPictureConverter
    ): BufferedImage? {
        var image: BufferedImage? = image
        var streamTimestamp: Long = picture.timeStamp
        // convert streamTimestamp into system units (i.e. nano-seconds)
        streamTimestamp = systemTimeBase.rescale(streamTimestamp - streamStartTime, streamTimebase)
        // get the current clock time, with our most accurate clock
        var systemTimestamp = System.nanoTime()
        // loop in a sleeping loop until we're within 1 ms of the time for that video frame.
        // a real video player needs to be much more sophisticated than this.
        while (streamTimestamp > (systemTimestamp - systemStartTime) * 1_000_000) {
//        Thread.sleep(1000 / 20)
            systemTimestamp = System.nanoTime()
        }
        // finally, convert the image from Humble format into Java images.
        image = converter.toImage(image, picture)
        // And ask the UI thread to repaint with the new image.
//        window.setImage(image)
        val width = image.width
        val height = image.height
        val aspectRatio = width.toDouble() / height.toDouble()
        val maxDimenAny = 80.0
        val maxDimen = minOf(maxDimenAny / height, maxDimenAny / height)

        print(CURSOR_TO_START)
        image.scale(maxDimen)
            .also { bufferedImage ->
//                imagePrinter.printImageCompressed(bufferedImage)


                imagePrinter.printImageReducedPalette(bufferedImage, paletteColors)
            }

        return image
    }

}

