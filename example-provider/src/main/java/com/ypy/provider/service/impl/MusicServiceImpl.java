package com.ypy.provider.service.impl;

import com.ypy.common.model.Music;
import com.ypy.common.service.MusicService;

public class MusicServiceImpl implements MusicService {
    @Override
    public Music getMusicById(long id) {
        Music music = new Music();
        music.setId(id);
        if (id == 0L) {
            music.setTitle("GoldBerg Variation, BWV 988");
            music.setComposer("Bach");
            music.setPlayer("Glen Gould");
        } else if (id == 1L) {
            music.setTitle("Oboe Quartet, K. 370");
            music.setComposer("Mozart");
            music.setPlayer("New York Classical Players");
        }
        return music;
    }
}
