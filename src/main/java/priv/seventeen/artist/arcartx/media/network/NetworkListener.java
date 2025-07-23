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
package priv.seventeen.artist.arcartx.media.network;

import priv.seventeen.artist.arcartx.common.api.events.EventHandler;
import priv.seventeen.artist.arcartx.common.api.events.Listener;
import priv.seventeen.artist.arcartx.common.api.events.arcartx.client.CustomPacketEvent;
import priv.seventeen.artist.arcartx.media.player.BgmPlayerManager;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @program: ArcartX_MediaPlayer
 * @description: 通讯
 * @author: 17Artist
 * @create: 2025-07-23 05:58
 **/
public class NetworkListener implements Listener {

    private static final ExecutorService networkExecutor = Executors.newSingleThreadExecutor();

    @EventHandler
    public void onCustomPack(CustomPacketEvent event){
        if(event.getId().equalsIgnoreCase("arcartx_bgm")){
            networkExecutor.submit(() ->{
                if(event.getArgSize() == 1 && event.getArg(0).equalsIgnoreCase("stop")){
                    BgmPlayerManager.stop();
                    return;
                }
                if(event.getArgSize() == 2){
                    switch (event.getArg(0)){
                        case "play" -> {
                            String urlOrFilePath = event.getArg(1);
                            BgmPlayerManager.play(urlOrFilePath);
                        }
                        case "set_loop" -> {
                            boolean loop = Boolean.parseBoolean(event.getArg(1));
                            BgmPlayerManager.setLoop(loop);
                        }
                        case "set_volume" -> {
                            float volume = Float.parseFloat(event.getArg(1));
                            BgmPlayerManager.setVolume(volume);
                        }
                    }
                }
            });
        }
    }
}
