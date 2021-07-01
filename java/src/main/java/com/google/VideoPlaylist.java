package com.google;

import java.util.ArrayList;
import java.util.List;

/** A class used to represent a Playlist. */
class VideoPlaylist implements Comparable<VideoPlaylist> {
  private final String title;
  private List<Video> videos;

  public VideoPlaylist(String title) {
    this.title = title;
    this.videos = new ArrayList<>();
  }

  public String getTitle() {
    return title;
  }

  public int getSize() {
    return videos.size();
  }

  public List<Video> getVideos() {
    return new ArrayList<>(videos);
  }

  private boolean find(Video video) {
    return videos.stream().anyMatch(v -> v.equals(video));
  }

  public boolean add(Video video) {
    if (find(video)) {
      return false;
    }

    videos.add(video);
    return true;
  }

  // Returns true if video is removed, false otherwise
  public boolean remove(Video video) {
    return videos.remove(video);
  }

  public void clear() {
    videos = new ArrayList<>();
  }

  @Override
  public int compareTo(VideoPlaylist other) {
    return this.title.compareToIgnoreCase(other.title);
  }
}
