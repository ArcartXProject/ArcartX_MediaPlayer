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

import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.Frame;
import priv.seventeen.artist.arcartx.common.game.Minecraft;
import priv.seventeen.artist.arcartx.media.player.BgmPlayerManager;



/**
 * @program: BgmPlayer
 * @description: 音乐播放器
 * @author: 17Artist
 * @create: 2024-10-22 03:23
 **/
public class BgmPlayer {

    private final FFmpegFrameGrabber frameGrabber;
    private final AudioPlayer audioPlayer;

    public volatile boolean shutdown = false;

    private volatile boolean closed = false;

    private final static Minecraft minecraft = Minecraft.INSTANCE;

    public BgmPlayer(String url){
        this(new FFmpegFrameGrabber(url));
    }


    private BgmPlayer(FFmpegFrameGrabber frameGrabber){
        this.frameGrabber = frameGrabber;
        this.audioPlayer = new AudioPlayer(){
            @Override
            public float getVolume() {
                return BgmPlayer.this.getVolume();
            }
        };
    }



    protected float getVolume(){
        return minecraft.getSoundSourceVolume("MASTER") * BgmPlayerManager.getVolume();
    }

    private boolean checkError = false;

    public void startTick(){
        try {
            this.checkError = false;
            this.frameGrabber.start();
            this.audioPlayer.init(this.frameGrabber);
        } catch (Exception e){
            checkError = true;
            close();
        }
    }

    public void runTick(){
        while (true){
            if(this.checkError || this.closed || this.shutdown){
                close();
                break;
            }
            try {
                Frame frame = this.frameGrabber.grabSamples();

                if(frame == null){
                    if(BgmPlayerManager.isLoop()){
                        this.frameGrabber.setTimestamp(0);
                    }
                    continue;
                }

                this.audioPlayer.processAudio(frame.samples);

            } catch (Exception e){
                checkError = true;
                close();
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
            this.audioPlayer.close();
        } catch (Exception e){
            //
        }
    }


}
