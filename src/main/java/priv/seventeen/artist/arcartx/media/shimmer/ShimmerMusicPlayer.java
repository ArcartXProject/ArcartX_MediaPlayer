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
package priv.seventeen.artist.arcartx.media.shimmer;

import priv.seventeen.artist.arcartx.media.player.BgmPlayerManager;
import priv.seventeen.artist.arcartx.shimmer.annotations.ShimmerInvokeHandler;
import priv.seventeen.artist.arcartx.shimmer.annotations.ShimmerNamespace;
import priv.seventeen.artist.arcartx.shimmer.callable.InvocationData;

/**
 * @program: ArcartX_MediaPlayer
 * @description: 音频播放器
 * @author: 17Artist
 * @create: 2025-07-23 06:19
 **/
@ShimmerNamespace("MusicPlayer")
public class ShimmerMusicPlayer {




    // 播放指定资源
    @ShimmerInvokeHandler("play")
    public static void play(InvocationData data){
        if(data.size() >= 1){
            BgmPlayerManager.play(data.get(0).stringValue());
        }
    }

    // 停止
    @ShimmerInvokeHandler("stop")
    public static void stop(InvocationData data){
        BgmPlayerManager.stop();
    }

    // 设置音量
    @ShimmerInvokeHandler("setVolume")
    public static void setVolume(InvocationData data){
        if(data.size() >= 1){
            float volume = data.get(0).floatValue();
            BgmPlayerManager.setVolume(volume);
        }
    }

    // 获取音量
    @ShimmerInvokeHandler("getVolume")
    public static float getVolume(InvocationData data){
        return BgmPlayerManager.getVolume();
    }

    // 设置循环模式
    @ShimmerInvokeHandler("setLoop")
    public static void setLoop(InvocationData data){
        if(data.size() >= 1){
            boolean loop = data.get(0).booleanValue();
            BgmPlayerManager.setLoop(loop);
        }
    }

    // 获取是否循环
    @ShimmerInvokeHandler("isLoop")
    public static boolean isLoop(InvocationData data){
        return BgmPlayerManager.isLoop();
    }


}
