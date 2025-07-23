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
package priv.seventeen.artist.arcartx.media;

import lombok.Getter;
import priv.seventeen.artist.arcartx.common.api.events.ArcartXEventManager;
import priv.seventeen.artist.arcartx.common.expansion.ArcartXExpansion;
import priv.seventeen.artist.arcartx.common.game.Minecraft;
import priv.seventeen.artist.arcartx.media.network.NetworkListener;
import priv.seventeen.artist.arcartx.media.player.ScreenPlayerManager;
import priv.seventeen.artist.arcartx.media.shimmer.ShimmerVideoPlayer;
import priv.seventeen.artist.arcartx.shimmer.callable.CallableManager;

import java.io.File;

/**
 * @program: MediaPlayerModule
 * @description: 多媒体播放器
 * @author: 17Artist
 * @create: 2024-10-08 18:11
 **/
public class MediaPlayerModule extends ArcartXExpansion {

    @Getter private final static File mediaFolder = new File(Minecraft.INSTANCE.getGameDirectory(),"ArcartX" + File.separator + "media");

    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Override
    public void onEnable() {
        mediaFolder.mkdirs();
        CallableManager.INSTANCE.registerStaticFunction(ShimmerVideoPlayer.class);
        CallableManager.INSTANCE.registerStaticFunction(ShimmerVideoPlayer.class);
        ArcartXEventManager.registerListener(new ScreenPlayerManager());
        ArcartXEventManager.registerListener(new NetworkListener());

    }


}
