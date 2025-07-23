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

import priv.seventeen.artist.arcartx.common.game.Minecraft;
import priv.seventeen.artist.arcartx.media.player.ScreenPlayerManager;
import priv.seventeen.artist.arcartx.shimmer.annotations.ShimmerInvokeHandler;
import priv.seventeen.artist.arcartx.shimmer.annotations.ShimmerNamespace;
import priv.seventeen.artist.arcartx.shimmer.callable.InvocationData;


/**
 * @program: ArcartX_MediaPlayer
 * @description: VideoPlayer
 * @author: 17Artist
 * @create: 2024-12-13 06:58
 **/
@ShimmerNamespace("VideoPlayer")
public class ShimmerVideoPlayer {




    // 播放指定资源
    @ShimmerInvokeHandler("play")
    public static void play(InvocationData data){
        if(data.size() >= 1 && Minecraft.INSTANCE.openedScreen()){
            ScreenPlayerManager.play(data.get(0).stringValue());
        }  else {
            ScreenPlayerManager.playFail();
        }
    }


    // 切换是否暂停
    @ShimmerInvokeHandler("togglePause")
    public static void pause(InvocationData data){
        if(ScreenPlayerManager.getCurrentPlayer() == null){
            return;
        }
        ScreenPlayerManager.getCurrentPlayer().playOrPause();
    }

    @ShimmerInvokeHandler("isPause")
    public static boolean isPause(InvocationData data){
        if(ScreenPlayerManager.getCurrentPlayer() == null){
            return true;
        }
        return ScreenPlayerManager.getCurrentPlayer().pause;
    }


    // 获取视频宽度
    @ShimmerInvokeHandler("getWidth")
    public static int getWidth(InvocationData data){
        if( ScreenPlayerManager.getCurrentPlayer() == null){
            return 0;
        }
        return ScreenPlayerManager.getCurrentPlayer().getVideoWidth();
    }

    // 获取视频高度
    @ShimmerInvokeHandler("getHeight")
    public static int getHeight(InvocationData data){
        if( ScreenPlayerManager.getCurrentPlayer() == null){
            return 0;
        }
        return ScreenPlayerManager.getCurrentPlayer().getVideoHeight();
    }


    // 设置是否循环
    @ShimmerInvokeHandler("setLoop")
    public static void setLoop(InvocationData data){
        if( ScreenPlayerManager.getCurrentPlayer() == null){
            return;
        }
        ScreenPlayerManager.getCurrentPlayer().loop(data.get(0).booleanValue());
    }


    // 设置进度
    @ShimmerInvokeHandler("setProgress")
    public static void setProgress(InvocationData data){
        if( ScreenPlayerManager.getCurrentPlayer() == null){
            return;
        }
        ScreenPlayerManager.getCurrentPlayer().setProgress(data.get(0).doubleValue());
    }

    // 获取进度
    @ShimmerInvokeHandler("getProgress")
    public static double getProgress(InvocationData data){
        if( ScreenPlayerManager.getCurrentPlayer() == null){
            return 0;
        }
        return ScreenPlayerManager.getCurrentPlayer().getProgress();
    }

    // 快进
    @ShimmerInvokeHandler("forward")
    public static void forward(InvocationData data){
        if( ScreenPlayerManager.getCurrentPlayer() == null){
            return;
        }
        ScreenPlayerManager.getCurrentPlayer().seek(true);
    }

    // 快退
    @ShimmerInvokeHandler("backward")
    public static void backward(InvocationData data){
        if( ScreenPlayerManager.getCurrentPlayer() == null){
            return;
        }
        ScreenPlayerManager.getCurrentPlayer().seek(false);
    }


    // 获取纹理渲染ID
    @ShimmerInvokeHandler("getTexture")
    public static String getTextureID(InvocationData data){
        return ScreenPlayerManager.getCanvas() != null ? ScreenPlayerManager.getCanvas().getID() : "NULL";
    }


}
