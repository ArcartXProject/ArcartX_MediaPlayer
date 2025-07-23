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

import org.bytedeco.ffmpeg.global.avutil;
import org.bytedeco.javacv.FFmpegFrameGrabber;

import javax.sound.sampled.*;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

/**
 * @program: VideoPlayer
 * @description: 音频播放器
 * @author: 17Artist
 * @create: 2024-10-22 03:24
 **/
public class AudioPlayer {

    private AudioFormat audioFormat = null;
    private SourceDataLine sourceDataLine;
    private int sampleFormat;
    private boolean checkError = false;


    public AudioPlayer(){

    }

    public float getVolume(){
        return 1.0F;
    }

    // 代码来源：https://blog.csdn.net/A694543965/article/details/78387156
    public void processAudio(Buffer[] samples) {
        int k;
        switch(sampleFormat){
            case avutil.AV_SAMPLE_FMT_FLTP://平面型左右声道分开。
                FloatBuffer leftData = (FloatBuffer) samples[0];
                ByteBuffer TLData = floatToByteValue(leftData, getVolume());
                FloatBuffer rightData = (FloatBuffer) samples[1];
                ByteBuffer TRData = floatToByteValue(rightData, getVolume());
                byte[] tl = TLData.array();
                byte[] tr = TRData.array();
                byte[] combine = new byte[tl.length + tr.length];
                k = 0;
                for(int i = 0; i< tl.length; i=i+2) {//混合两个声道。
                    for (int j = 0; j < 2; j++) {
                        combine[j+4*k] = tl[i + j];
                        combine[j + 2+4*k] = tr[i + j];
                    }
                    k++;
                }
                sourceDataLine.write(combine,0, combine.length);
                break;
            case avutil.AV_SAMPLE_FMT_S16://非平面型左右声道在一个buffer中。
                ShortBuffer ILData = (ShortBuffer) samples[0];
                TLData = shortToByteValue(ILData,getVolume());
                tl = TLData.array();
                sourceDataLine.write(tl,0, tl.length);
                break;
            case avutil.AV_SAMPLE_FMT_FLT://float非平面型
                leftData = (FloatBuffer) samples[0];
                TLData = floatToByteValue(leftData,getVolume());
                tl = TLData.array();
                sourceDataLine.write(tl,0, tl.length);
                break;
            case avutil.AV_SAMPLE_FMT_S16P://平面型左右声道分开
                ILData = (ShortBuffer) samples[0];
                ShortBuffer IRData = (ShortBuffer) samples[1];
                TLData = shortToByteValue(ILData,getVolume());
                TRData = shortToByteValue(IRData,getVolume());
                tl = TLData.array();
                tr = TRData.array();
                combine = new byte[tl.length+ tr.length];
                k = 0;
                for(int i = 0; i< tl.length; i=i+2) {
                    for (int j = 0; j < 2; j++) {
                        combine[j+4*k] = tl[i + j];
                        combine[j + 2+4*k] = tr[i + j];
                    }
                    k++;
                }
                sourceDataLine.write(combine,0, combine.length);
                break;
            default:
                checkError = true;
                break;
        }
    }

    public void close(){
        closeSourceDataLine(sourceDataLine);
    }


    public void init(FFmpegFrameGrabber grabber) {
        this.sampleFormat = grabber.getSampleFormat();
        switch(grabber.getSampleFormat()){
            case avutil.AV_SAMPLE_FMT_U8:
                break;
            case avutil.AV_SAMPLE_FMT_S16:
            case avutil.AV_SAMPLE_FMT_FLT:
            case avutil.AV_SAMPLE_FMT_S16P:
            case avutil.AV_SAMPLE_FMT_FLTP:
                audioFormat = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED,grabber.getSampleRate(),16,grabber.getAudioChannels(),grabber.getAudioChannels()*2,grabber.getSampleRate(),true);
                break;
            case avutil.AV_SAMPLE_FMT_S32:
            case avutil.AV_SAMPLE_FMT_U8P:
            case avutil.AV_SAMPLE_FMT_DBL:
            case avutil.AV_SAMPLE_FMT_DBLP:
                break;
            case avutil.AV_SAMPLE_FMT_S32P:
                audioFormat = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED,grabber.getSampleRate(),32,grabber.getAudioChannels(),grabber.getAudioChannels()*2,grabber.getSampleRate(),true);
                break;
            case avutil.AV_SAMPLE_FMT_S64:
                break;
            case avutil.AV_SAMPLE_FMT_S64P:
                break;
            default:
                checkError = true;
        }

        if(checkError) return;

        DataLine.Info dataLineInfo = new DataLine.Info(SourceDataLine.class,
                audioFormat, AudioSystem.NOT_SPECIFIED);
        try {
            sourceDataLine = (SourceDataLine)AudioSystem.getLine(dataLineInfo);
            sourceDataLine.open(audioFormat);
            sourceDataLine.start();
        } catch (LineUnavailableException e) {
            checkError = true;
            closeSourceDataLine(sourceDataLine);
        }
    }

    private static ByteBuffer shortToByteValue(ShortBuffer arr,float vol) {
        int len  = arr.capacity();
        ByteBuffer bb = ByteBuffer.allocate(len * 2);
        for(int i = 0;i<len;i++){
            bb.putShort(i*2,(short)((float)arr.get(i)*vol));
        }
        return bb;
    }

    private static ByteBuffer floatToByteValue(FloatBuffer arr,float vol){
        int len = arr.capacity();
        float f;
        float v;
        ByteBuffer res = ByteBuffer.allocate(len*2);
        v = 32768.0f*vol;
        for(int i=0;i<len;i++){
            f = arr.get(i)*v;
            if(f>v) f = v;
            if(f<-v) f = v;
            //默认转为大端序
            res.putShort(i*2,(short)f);
        }
        return res;
    }

    private static void closeSourceDataLine(SourceDataLine sourceDataLineIn){
        try {
            if(sourceDataLineIn != null){
                sourceDataLineIn.stop();
                sourceDataLineIn.close();
            }
        }catch (Exception e){
            //
        }
    }

}
