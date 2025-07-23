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
package priv.seventeen.artist.arcartx.media.player;

import lombok.Getter;
import priv.seventeen.artist.arcartx.common.api.events.EventHandler;
import priv.seventeen.artist.arcartx.common.api.events.Listener;
import priv.seventeen.artist.arcartx.common.api.events.minecraft.screen.GameLayerRenderEvent;
import priv.seventeen.artist.arcartx.common.api.events.minecraft.screen.ScreenChangeEvent;
import priv.seventeen.artist.arcartx.common.api.scheduler.ArcartXScheduler;
import priv.seventeen.artist.arcartx.common.api.texture.DynamicTexture;
import priv.seventeen.artist.arcartx.media.player.type.VideoPlayer;
import priv.seventeen.artist.arcartx.media.utils.Utils;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


/**
 * @program: ArcartX_MediaPlayer
 * @description: 播放器管理
 * @author: 17Artist
 * @create: 2025-07-22 05:58
 **/
public class ScreenPlayerManager implements Listener  {

    private static final ExecutorService videoExecutor = Executors.newSingleThreadExecutor();

    @Getter
    private volatile static DynamicTexture canvas;

    @Getter
    private volatile static VideoPlayer currentPlayer = null;

    private volatile static boolean playing = false;

    private static void createPlayer(String url) {
        closePlayer();
        playing = true;
        currentPlayer = new VideoPlayer(url);
        ArcartXScheduler.ensureSync(()->{
            canvas = new DynamicTexture();
        });
    }

    private static void closePlayer() {
        playing = false;
        if(currentPlayer != null) {
            currentPlayer.shutdown = true;
            currentPlayer = null;
        }
        ArcartXScheduler.ensureSync(()->{
            if(canvas != null){
                canvas.delete();
                canvas = null;
            }
        });
    }

    // 关闭UI后关闭播放器
    @EventHandler
    public void onScreenCloseEvent(ScreenChangeEvent event){
        if(playing){
            closePlayer();
        }
    }

    // 更新纹理
    @EventHandler
    public  void update(GameLayerRenderEvent.Post event){
        if(playing && currentPlayer != null  && canvas != null){
            VideoPlayer.Data data = currentPlayer.getAndClearData();
            if(data != null){
                canvas.update(data.pixels(), data.width(), data.height());
            }
        }
    }

    public static void play(String url) {
        try {
            String target = Utils.parseUrl(url);
            if(target != null){
                createPlayer(target);
                videoExecutor.submit(()->currentPlayer.play());
            } else {
                closePlayer();
            }
        } catch (Exception e) {
            closePlayer();
        }
    }

    public static void playFail() {
        closePlayer();
    }




}
