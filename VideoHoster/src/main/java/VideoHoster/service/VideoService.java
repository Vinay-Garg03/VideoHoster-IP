package VideoHoster.service;

import VideoHoster.model.Video;
import VideoHoster.repository.VideoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class VideoService {
    @Autowired
    private VideoRepository videoRepository;

    //Call the getAllVideos() method in the Repository and obtain a List of all the videos in the database
    public List<Video> getAllVideos() {
        return videoRepository.getAllVideos();
    }


    //The method calls the createVideo() method in the Repository and passes the video to be persisted in the database
    public void uploadVideo(Video video) {
        videoRepository.uploadVideo(video);
    }


    //The method calls the getVideoByTitle() method in the Repository and passes the title of the video to be fetched
    public Video getVideoByTitle(String title) {
        return videoRepository.getVideoByTitle(title);
    }

    //The method calls the getVideo() method in the Repository and passes the id of the video to be fetched
    public Video getVideo(Integer videoId) {
        return videoRepository.getVideo(videoId);
    }

    //The method calls the updateVideo() method in the Repository and passes the Video to be updated in the database
    public void updateVideo(Video updatedVideo) {
        videoRepository.updateVideo(updatedVideo);
    }

    //The method calls the deleteVideo() method in the Repository and passes the Video id of the video to be deleted in the database
    public void deleteVideo(Integer videoId) {
        videoRepository.deleteVideo(videoId);
    }

}
