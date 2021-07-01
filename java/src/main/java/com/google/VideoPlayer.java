package com.google;

import java.util.*;
import java.util.stream.Collectors;

public class VideoPlayer {

  private final Random generator;
  private final VideoLibrary videoLibrary;
  private final List<VideoPlaylist> playlists;
  private Video currentPlayingVideo;
  private boolean isPlaying;

  public VideoPlayer() {
    this.generator = new Random();
    this.videoLibrary = new VideoLibrary();
    this.playlists = new ArrayList<>();
    this.currentPlayingVideo = null;
    this.isPlaying = false;
  }

  public void numberOfVideos() {
    System.out.printf("%s videos in the library%n", videoLibrary.getVideos().size());
  }

  // format: title video_id tags
  public void showAllVideos() {
    List<Video> videos = videoLibrary.getVideos();
    Collections.sort(videos);

    System.out.println("Here's a list of all available videos:");
    videos.forEach(System.out::println);
  }

  public void playVideo(String videoId) {
    Video newVideo = videoLibrary.getVideo(videoId);

    if (newVideo == null) {
      System.out.println("Cannot play video: Video does not exist");
      return;
    }

    // if video currently playing/paused
    if (currentPlayingVideo != null) {
      stopVideo();
    }

    if (newVideo.isFlagged()) {
      System.out.println(
          "Cannot play video: Video is currently flagged (reason: "
              + newVideo.getFlagReason()
              + ")");
      return;
    }

    currentPlayingVideo = newVideo;
    isPlaying = true;
    System.out.println("Playing video: " + currentPlayingVideo.getTitle());
  }

  public void stopVideo() {
    if (currentPlayingVideo == null) {
      System.out.println("Cannot stop video: No video is currently playing");
      return;
    }

    System.out.println("Stopping video: " + currentPlayingVideo.getTitle());
    currentPlayingVideo = null;
  }

  public void playRandomVideo() {
    List<Video> videos =
        videoLibrary.getVideos().stream()
            .filter((v -> v.getFlagReason() == null))
            .collect(Collectors.toList());

    if (videos.size() == 0) {
      System.out.println("No videos available");
      return;
    }

    Video randomVideo = videos.get(generator.nextInt(videos.size()));
    playVideo(randomVideo.getVideoId());
  }

  public void pauseVideo() {
    if (currentPlayingVideo == null) {
      System.out.println("Cannot pause video: No video is currently playing");
      return;
    }

    // currentVideo != null
    if (!isPlaying) {
      System.out.println("Video already paused: " + currentPlayingVideo.getTitle());
    } else {
      System.out.println("Pausing video: " + currentPlayingVideo.getTitle());
      isPlaying = false;
    }
  }

  public void continueVideo() {
    if (currentPlayingVideo == null) {
      System.out.println("Cannot continue video: No video is currently playing");
      return;
    }

    // currentVideo != null
    if (isPlaying) {
      System.out.println("Cannot continue video: Video is not paused");
    } else {
      System.out.println("Continuing video: " + currentPlayingVideo.getTitle());
      isPlaying = true;
    }
  }

  public void showPlaying() {
    if (currentPlayingVideo == null) {
      System.out.println("No video is currently playing");
      return;
    }

    System.out.println(
        "Currently playing: " + currentPlayingVideo + (!isPlaying ? " - PAUSED" : ""));
  }

  private Optional<VideoPlaylist> findPlaylist(String playlistName) {
    return playlists.stream().filter(p -> p.getTitle().equalsIgnoreCase(playlistName)).findFirst();
  }

  public void createPlaylist(String playlistName) {
    findPlaylist(playlistName)
        .ifPresentOrElse(
            p ->
                System.out.println(
                    "Cannot create playlist: A playlist with the same name already exists"),
            () -> {
              playlists.add(new VideoPlaylist(playlistName));
              System.out.println("Successfully created new playlist: " + playlistName);
            });
  }

  public void addVideoToPlaylist(String playlistName, String videoId) {
    Optional<VideoPlaylist> foundPlaylist = findPlaylist(playlistName);

    if (foundPlaylist.isPresent()) {
      VideoPlaylist playlist = foundPlaylist.get();
      Video video = videoLibrary.getVideo(videoId);

      if (video == null) {
        System.out.println("Cannot add video to " + playlistName + ": Video does not exist");
        return;
      }

      // video is flagged
      if (video.getFlagReason() != null) {
        System.out.println(
            "Cannot add video to "
                + playlistName
                + ": Video is currently flagged (reason: "
                + video.getFlagReason()
                + ")");
        return;
      }

      // video != null
      if (playlist.add(video)) {
        System.out.println("Added video to " + playlistName + ": " + video.getTitle());
      } else {
        System.out.println("Cannot add video to " + playlistName + ": Video already added");
      }
    } else {
      System.out.println("Cannot add video to " + playlistName + ": Playlist does not exist");
    }
  }

  public void showAllPlaylists() {
    if (playlists.isEmpty()) {
      System.out.println("No playlists exist yet");
      return;
    }

    Collections.sort(playlists);
    System.out.println("Showing all playlists:");
    playlists.forEach(p -> System.out.println("  " + p.getTitle()));
  }

  public void showPlaylist(String playlistName) {
    Optional<VideoPlaylist> foundPlaylist = findPlaylist(playlistName);

    if (foundPlaylist.isEmpty()) {
      System.out.println("Cannot show playlist " + playlistName + ": Playlist does not exist");
      return;
    }

    VideoPlaylist playlist = foundPlaylist.get();

    System.out.println("Showing playlist: " + playlistName);

    if (playlist.getSize() == 0) {
      System.out.println("  No videos here yet");
    } else {
      playlist.getVideos().forEach(v -> System.out.println("  " + v));
    }
  }

  public void removeFromPlaylist(String playlistName, String videoId) {
    Optional<VideoPlaylist> foundPlaylist = findPlaylist(playlistName);

    if (foundPlaylist.isEmpty()) {
      System.out.println("Cannot remove video from " + playlistName + ": Playlist does not exist");
      return;
    }

    Video video = videoLibrary.getVideo(videoId);

    if (video == null) {
      System.out.println("Cannot remove video from " + playlistName + ": Video does not exist");
      return;
    }
    // video exists && playlist exists

    VideoPlaylist playlist = foundPlaylist.get();
    if (!playlist.remove(video)) {
      System.out.println("Cannot remove video from " + playlistName + ": Video is not in playlist");
    } else {
      System.out.println("Removed video from " + playlistName + ": " + video.getTitle());
    }
  }

  public void clearPlaylist(String playlistName) {
    findPlaylist(playlistName)
        .ifPresentOrElse(
            p -> {
              p.clear();
              System.out.println("Successfully removed all videos from " + playlistName);
            },
            () ->
                System.out.println(
                    "Cannot clear playlist " + playlistName + ": Playlist does not exist"));
  }

  public void deletePlaylist(String playlistName) {
    // A playlist is uniquely identified by title (ignore case), so at most one is removed
    boolean isRemoved = playlists.removeIf(p -> p.getTitle().equalsIgnoreCase(playlistName));

    if (isRemoved) {
      System.out.println("Deleted playlist: " + playlistName);
    } else {
      System.out.println("Cannot delete playlist " + playlistName + ": Playlist does not exist");
    }
  }

  private boolean isSubstringIgnoreCase(String s1, String s2) {
    return s1.toLowerCase().contains(s2.toLowerCase());
  }

  private void handleVideoSearchResults(List<Video> results, String searchTerm) {
    if (results.isEmpty()) {
      System.out.println("No search results for " + searchTerm);
      return;
    }

    System.out.println("Here are the results for " + searchTerm + ":");

    for (int i = 0; i < results.size(); i++) {
      System.out.println((i + 1) + ") " + results.get(i));
    }

    Scanner scanner = new Scanner(System.in);

    System.out.println(
        "Would you like to play any of the above? If yes, specify the number of the video.\n"
            + "If your answer is not a valid number, we will assume it's a no.");

    // Ignore if input invalid (not in range/string instead of number)
    String input = scanner.nextLine();
    int index;
    try {
      index = Integer.parseInt(input);
    } catch (NumberFormatException e) {
      return;
    }

    if (index < 1 || index > results.size()) {
      return;
    }

    // Play video
    playVideo(results.get(index - 1).getVideoId());
  }

  public void searchVideos(String searchTerm) {
    List<Video> matchingVideos =
        videoLibrary.getVideos().stream()
            .filter(v -> isSubstringIgnoreCase(v.getTitle(), searchTerm))
            .filter(v -> v.getFlagReason() == null)
            .sorted()
            .collect(Collectors.toList());

    handleVideoSearchResults(matchingVideos, searchTerm);
  }

  // if any of the tag of a video matches, return true
  public void searchVideosWithTag(String videoTag) {
    List<Video> matchingVideos =
        videoLibrary.getVideos().stream()
            .filter(v -> v.getTags().stream().anyMatch(t -> t.equalsIgnoreCase(videoTag)))
            .filter(v -> v.getFlagReason() == null)
            .sorted()
            .collect(Collectors.toList());

    handleVideoSearchResults(matchingVideos, videoTag);
  }

  // Default: no reason provided
  public void flagVideo(String videoId) {
    flagVideo(videoId, "Not supplied");
  }

  public void flagVideo(String videoId, String reason) {
    Video video = videoLibrary.getVideo(videoId);

    if (video == null) {
      System.out.println("Cannot flag video: Video does not exist");
      return;
    }

    if (video.isFlagged()) {
      System.out.println("Cannot flag video: Video is already flagged");
      return;
    }

    video.setFlag(true, reason);

    if (currentPlayingVideo != null && currentPlayingVideo.getVideoId().equals(videoId)) {
      stopVideo();
      currentPlayingVideo = null;
    }

    System.out.println(
        "Successfully flagged video: " + video.getTitle() + " (reason: " + reason + ")");
  }

  public void allowVideo(String videoId) {
    Video video = videoLibrary.getVideo(videoId);

    if (video == null) {
      System.out.println("Cannot remove flag from video: Video does not exist");
      return;
    }

    if (!video.isFlagged()) {
      System.out.println("Cannot remove flag from video: Video is not flagged");
      return;
    }

    video.setFlag(false, null);
    System.out.println("Successfully removed flag from video: " + video.getTitle());
  }
}
