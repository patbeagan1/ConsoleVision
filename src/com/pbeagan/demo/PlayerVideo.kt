package com.pbeagan.demo

import io.humble.video.Decoder
import io.humble.video.Demuxer
import io.humble.video.DemuxerStream
import io.humble.video.Global
import io.humble.video.MediaDescriptor
import io.humble.video.MediaPacket
import io.humble.video.MediaPicture
import io.humble.video.Rational
import io.humble.video.awt.MediaPictureConverter
import io.humble.video.awt.MediaPictureConverterFactory
import java.awt.image.BufferedImage
import java.io.IOException

class PlayerVideo(val filename: String?, private val display: Display) {
    private val demuxer: Demuxer = Demuxer.make().apply {
        open(
            filename!!,
            null,
            false,
            true,
            null,
            null
        )
    }

    var videoStreamId = -1
    var streamStartTime: Long = Global.NO_PTS
    lateinit var videoDecoder: Decoder

    private var _image: BufferedImage? = null
    private var image: BufferedImage
        get() = _image!!
        set(value) {
            _image = value
        }

    private val systemStartTime = System.nanoTime()
    private val systemTimeBase: Rational = Rational.make(1, 1_000_000_000)
    private val streamTimebase: Rational by lazy { videoDecoder.timeBase }

    @Throws(InterruptedException::class, IOException::class)
    fun run() {
        initializeDecoder()
        videoDecoder.open(null, null)
        val picture: MediaPicture = MediaPicture.make(
            videoDecoder.width,
            videoDecoder.height,
            videoDecoder.pixelFormat
        )
        val converter: MediaPictureConverter = MediaPictureConverterFactory.createConverter(
            MediaPictureConverterFactory.HUMBLE_BGR_24,
            picture
        )

        runDecodingLoop(picture, converter)
        flushDecoder(picture, converter)
        demuxer.close()
    }

    private fun initializeDecoder() {
        for (i in 0 until demuxer.numStreams) {
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

    @Throws(InterruptedException::class)
    private fun displayVideoAtCorrectTime(
        picture: MediaPicture,
        converter: MediaPictureConverter
    ): BufferedImage {
        _image ?: run { _image = converter.toImage(null, picture) }
        image = converter.toImage(image, picture)

        waitForPictureTimeStamp(picture)

        display.printFrame(image)

        return image
    }

    private fun waitForPictureTimeStamp(picture: MediaPicture) {
        var streamTimestamp: Long = picture.timeStamp
        streamTimestamp = systemTimeBase.rescale(streamTimestamp - streamStartTime, streamTimebase)
        var systemTimestamp = System.nanoTime()
        while (streamTimestamp > (systemTimestamp - systemStartTime) * 1_000_000) {
            systemTimestamp = System.nanoTime()
        }
    }
}