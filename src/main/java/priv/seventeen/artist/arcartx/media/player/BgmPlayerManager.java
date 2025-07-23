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
import lombok.Setter;
import priv.seventeen.artist.arcartx.media.player.type.BgmPlayer;
import priv.seventeen.artist.arcartx.media.utils.Utils;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @program: ArcartX_MediaPlayer
 * @description: 背景音乐管理
 * @author: 17Artist
 * @create: 2025-07-22 20:50
 **/
public class BgmPlayerManager {

    private static final ExecutorService bgmExecutor = Executors.newSingleThreadExecutor();

    @Getter @Setter
    private static volatile boolean loop = false;

    @Getter @Setter
    private static volatile float volume = 1.0f;

    private static volatile BgmPlayer currentPlayer = null;

    private static void createPlayer(String url) {
        closePlayer();
        currentPlayer = new BgmPlayer(url);
    }

    private static void closePlayer() {
        if(currentPlayer != null) {
            currentPlayer.shutdown = true;
            currentPlayer = null;
        }
    }

    // 播放
    public static void play(String url) {
        try {
            String target = Utils.parseUrl(url);
            if(target != null){
                createPlayer(target);
                bgmExecutor.submit(()->currentPlayer.play());
            } else {
                closePlayer();
            }
        } catch (Exception e) {
            closePlayer();
        }
    }

    public static void stop(){
        closePlayer();
    }





}
