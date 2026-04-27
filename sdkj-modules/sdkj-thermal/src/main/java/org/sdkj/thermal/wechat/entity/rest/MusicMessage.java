package org.sdkj.thermal.wechat.entity.rest;

/**
 * 音乐消息
 *
 */
public class MusicMessage extends BaseMessage {
    // 音乐
    private org.sdkj.thermal.wechat.entity.rest.Music Music;

    public org.sdkj.thermal.wechat.entity.rest.Music getMusic() {
        return Music;
    }

    public void setMusic(org.sdkj.thermal.wechat.entity.rest.Music music) {
        Music = music;
    }
}
