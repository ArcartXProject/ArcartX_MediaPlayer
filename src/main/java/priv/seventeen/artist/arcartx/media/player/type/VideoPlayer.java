/*
    Copyright (C) 2025 17Artist

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program. If not, see <https://www.gnu.org/licenses/>.
 */
package priv.seventeen.artist.arcartx.media.player.type;

import lombok.Getter;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.Java2DFrameConverter;
import priv.seventeen.artist.arcartx.common.game.Minecraft;
import priv.seventeen.artist.arcartx.media.utils.Utils;

import java.awt.image.BufferedImage;
import java.util.Queue;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @program: VideoPlayer
 * @description: 视频播放器
 * @author: 17Artist
 * @create: 2024-10-22 03:23
 **/
public class VideoPlayer {

    private final ExecutorService audioExecutor = Executors.newSingleThreadExecutor();

    public volatile boolean pause =false;
    private volatile boolean playing = false;
    private volatile boolean closed = false;
    private volatile boolean loop = false;
    private volatile double seek = -1;
    private volatile double totalTime = 0;
    private volatile double currentTime = 0;
    public volatile boolean shutdown = false;

    // 播放相关组件
    private final FFmpegFrameGrabber frameGrabber;


    private final Java2DFrameConverter converter;
    private final AudioPlayer audioPlayer;

    @Getter private int videoWidth = 0;

    @Getter private int videoHeight = 0;

    private final static Minecraft minecraft = Minecraft.INSTANCE;

    private final AtomicReference<Data> data =new AtomicReference<>();

    public VideoPlayer(String url){
        this(new FFmpegFrameGrabber(url));
    }


    private VideoPlayer(FFmpegFrameGrabber frameGrabber){
        this.frameGrabber = frameGrabber;
        this.audioPlayer = new AudioPlayer(){
            @Override
            public float getVolume() {
                return VideoPlayer.this.getVolume();
            }
        };
        this.converter = new Java2DFrameConverter();
    }

    // 画面更新回调
    public void imageUpdateCallBack(int[] pixels){
        this.data.set(new Data(pixels, this.getVideoWidth(), this.getVideoHeight()));
    }

    public Data getAndClearData() {
        return this.data.getAndSet(null);
    }

    public void playOrPause(){
        boolean last = this.pause;
        this.pause = !last;
    }

    public void seek(boolean forward){
        if(!playing){
            return;
        }
        double percent = this.currentTime / this.totalTime;
        if(forward){
            percent += 0.05D;
        } else {
            percent -= 0.05D;
        }
        percent = Math.max(0, Math.min(1, percent));
        this.currentTime = (long) (this.totalTime * percent);
        if(this.pause){
            this.pause = false;
        }
        this.seek = this.currentTime;
    }

    public void loop(boolean loop){
        this.loop = loop;
    }

    public double getProgress(){
        if(!playing){
            return 0;
        }
        if(seek == -1){
            return this.currentTime / this.totalTime;
        } else {
            return seek / this.totalTime;
        }

    }

    public void setProgress(double progress){
        if(!playing){
            return;
        }
        this.currentTime =this.totalTime * progress;
        if(this.pause){
            this.pause = false;
        }
        this.seek = this.currentTime;
    }

    protected float getVolume(){
        return minecraft.getSoundSourceVolume("MASTER");
    }

    private boolean checkError = false;

    public void startTick(){
        try {
            this.closed = false;
            this.checkError = false;
            this.pause = false;
            this.frameGrabber.start();
            this.totalTime = this.frameGrabber.getLengthInTime();
            this.videoHeight = this.frameGrabber.getImageHeight();
            this.videoWidth = this.frameGrabber.getImageWidth();
            this.audioPlayer.init(this.frameGrabber);
            this.playing = true;
        } catch (Exception e){
            checkError = true;
            shutdown();
        }
    }

    // 图像解析队列
    Queue<CompletableFuture<int[]>> imageQueue = new ConcurrentLinkedQueue<>();

    public void runTick(){
        // 0.5秒检测一次进度条
        long lastCheckTime = System.currentTimeMillis();
        long startTime = System.currentTimeMillis() * 1000L;
        while (true){
            if(this.checkError || this.shutdown || this.closed){
                shutdown();
                break;
            }
            if(this.pause){
                startTime = System.currentTimeMillis() * 1000L - (long) currentTime;
                continue;
            }
            try {
                if(lastCheckTime < System.currentTimeMillis()){
                    lastCheckTime = System.currentTimeMillis() + 500;
                    double seekTime = this.seek;
                    if(seekTime != -1){
                        currentTime = seekTime;
                        startTime = System.currentTimeMillis() * 1000L - (long) seekTime;
                        this.seek = -1;
                        imageQueue.forEach(imageFuture -> {
                            imageFuture.cancel(true);
                        });
                        imageQueue.clear();
                        this.frameGrabber.setTimestamp((long) seekTime);
                    }
                }

                Frame frame = this.frameGrabber.grab();

                if(frame == null){
                    if(this.loop){
                        this.frameGrabber.setTimestamp(0);
                        this.currentTime = 0;
                        imageQueue.forEach(imageFuture -> {
                            imageFuture.cancel(true);
                        });
                        imageQueue.clear();
                    }
                    continue;
                }

                if(frame.samples != null){
                    Frame clone = frame.clone();
                    audioExecutor.submit(() ->  this.audioPlayer.processAudio(clone.samples));
                }


                CompletableFuture<int[]> future = imageQueue.peek();
                if(future != null && future.isDone() ){
                    imageQueue.poll();
                    if(future.get() != null){
                        imageUpdateCallBack(future.get());
                    }
                }

                if(frame.image != null){
                    this.currentTime = frame.timestamp;
                    long time = frame.timestamp + startTime;
                    if(time > System.currentTimeMillis() * 1000L){
                        Thread.sleep((time - System.currentTimeMillis() * 1000L) / 1000L);
                    }
                    BufferedImage bufferedImage = this.converter.getBufferedImage(frame);
                    imageQueue.offer(CompletableFuture.supplyAsync(() -> Utils.convertBufferedImageToIntArray(bufferedImage)));
                }

            } catch (Exception e){
                checkError = true;
                shutdown();
                break;
            }

        }
    }



    public void play(){
       startTick();
       runTick();
    }

    private void close(){
        if(closed){
            return;
        }
        closed = true;
        try {
            if(this.frameGrabber != null){
                this.frameGrabber.stop();
                this.frameGrabber.release();
                this.frameGrabber.close();
            }
            this.converter.close();
            this.audioPlayer.close();
        } catch (Exception e){
            //
        }
    }

    private void shutdown(){
        shutdown = true;
        audioExecutor.shutdown();
        close();
    }

    public record Data(int[] pixels, int width, int height) {}



}
