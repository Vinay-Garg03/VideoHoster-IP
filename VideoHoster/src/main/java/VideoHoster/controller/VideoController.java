package VideoHoster.controller;

import VideoHoster.model.Tag;
import VideoHoster.model.User;
import VideoHoster.model.Video;
import VideoHoster.service.TagService;
import VideoHoster.service.VideoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.*;

@Controller
public class VideoController {

    @Autowired
    private VideoService videoService;

    @Autowired
    private TagService tagService;

    //This method displays all the videos in the user home page after successful login
    @RequestMapping("videos")
    public String getUserVideos(Model model) {
        List<Video> videos = videoService.getAllVideos();
        model.addAttribute("videos", videos);
        return "videos";
    }

    //This method is called when the details of the specific video with corresponding title are to be displayed
    //The logic is to get the video from the databse with corresponding title. After getting the video from the database the details are shown
    //First receive the dynamic parameter in the incoming request URL in a string variable 'title' and also the Model type object
    //Call the getVideoByTitle() method in the business logic to fetch all the details of that video
    //Add the video in the Model type object with 'video' as the key
    //Return 'videos/video.html' file

    //Also now you need to add the tags of an video in the Model type object
    //Here a list of tags is added in the Model type object
    //this list is then sent to 'videos/video.html' file and the tags are displayed
    @RequestMapping("/videos/{title}")
    public String showVideo(@PathVariable("title") String title, Model model) {
        Video video = videoService.getVideoByTitle(title);
        model.addAttribute("video", video);
        model.addAttribute("tags", video.getTags());
        return "videos/video";
    }

    //This controller method is called when the request pattern is of type 'videos/upload'
    //The method returns 'videos/upload.html' file
    @RequestMapping("/videos/upload")
    public String newVideo() {
        return "videos/upload";
    }

    //This controller method is called when the request pattern is of type 'videos/upload' and also the incoming request is of POST type
    //The method receives all the details of the video to be stored in the database, and now the video will be sent to the business logic to be persisted in the database
    //After you get the videoFile, set the user of the video by getting the logged in user from the Http Session
    //Convert the video to Base64 format and store it as a string in the 'videoFile' attribute
    //Set the date on which the video is posted
    //After storing the video, this method directs to the logged in user homepage displaying all the videos

    //Get the 'tags' request parameter using @RequestParam annotation which is just a string of all the tags
    //Store all the tags in the database and make a list of all the tags using the findOrCreateTags() method
    //set the tags attribute of the video as a list of all the tags returned by the findOrCreateTags() method
    @RequestMapping(value = "/videos/upload", method = RequestMethod.POST)
    public String createVideo(@RequestParam("file") MultipartFile file, @RequestParam("tags") String tags, Video newVideo, HttpSession session) throws IOException {

        User user = (User) session.getAttribute("loggeduser");
        newVideo.setUser(user);
        String uploadedVideoData = convertUploadedFileToBase64(file);
        newVideo.setVideoFile(uploadedVideoData);

        List<Tag> videoTags = findOrCreateTags(tags);
        newVideo.setTags(videoTags);
        newVideo.setDate(new Date());
        videoService.uploadVideo(newVideo);
        return "redirect:/videos";
    }

    //This controller method is called when the request pattern is of type 'editVideo'
    //This method fetches the video with the corresponding id from the database and adds it to the model with the key as 'video'
    //The method then returns 'videos/edit.html' file wherein you fill all the updated details of the video

    //The method first needs to convert the list of all the tags to a string containing all the tags separated by a comma and then add this string in a Model type object
    //This string is then displayed by 'edit.html' file as previous tags of an video
    @RequestMapping(value = "/editVideo")
    public String editVideo(@RequestParam("videoId") Integer videoId, Model model) {
        Video video = videoService.getVideo(videoId);

        String tags = convertTagsToString(video.getTags());
        model.addAttribute("video", video);
        model.addAttribute("tags",tags);
        return "videos/edit";
    }

    //This controller method is called when the request pattern is of type 'videos/edit' and also the incoming request is of PUT type
    //The method receives the videoFile, videoId, updated video, along with the Http Session
    //The method adds the new videoFile to the updated video if user updates the videoFile and adds the previous videoFile to the new updated video if user does not choose to update the videoFile
    //Set an id of the new updated video
    //Set the user using Http Session
    //Set the date on which the video is posted
    //Call the updateVideo() method in the business logic to update the video
    //Direct to the same page showing the details of that particular updated video

    //The method also receives tags parameter which is a string of all the tags separated by a comma using the annotation @RequestParam
    //The method converts the string to a list of all the tags using findOrCreateTags() method and sets the tags attribute of an video as a list of all the tags
    @RequestMapping(value = "/editVideo", method = RequestMethod.PUT)
    public String editVideoSubmit(@RequestParam("file") MultipartFile file, @RequestParam("videoId") Integer videoId, @RequestParam("tags") String tags, Video updatedVideo, HttpSession session) throws IOException {

        Video video = videoService.getVideo(videoId);
        String updatedVideoData = convertUploadedFileToBase64(file);
        List<Tag> videoTags = findOrCreateTags(tags);

        if (updatedVideoData.isEmpty())
            updatedVideo.setVideoFile(video.getVideoFile());
        else {
            updatedVideo.setVideoFile(updatedVideoData);
        }

        updatedVideo.setId(videoId);
        User user = (User) session.getAttribute("loggeduser");
        updatedVideo.setUser(user);
        updatedVideo.setTags(videoTags);
        updatedVideo.setDate(new Date());

        videoService.updateVideo(updatedVideo);
        return "redirect:/videos/" + updatedVideo.getTitle();
    }


    //This controller method is called when the request pattern is of type 'deleteVideo' and also the incoming request is of DELETE type
    //The method calls the deleteVideo() method in the business logic passing the id of the video to be deleted
    //Looks for a controller method with request mapping of type '/videos'
    @RequestMapping(value = "/deleteVideo", method = RequestMethod.DELETE)
    public String deleteVideoSubmit(@RequestParam(name = "videoId") Integer videoId) {
        videoService.deleteVideo(videoId);
        return "redirect:/videos";
    }


    //This method converts the video to Base64 format
    private String convertUploadedFileToBase64(MultipartFile file) throws IOException {
        return Base64.getEncoder().encodeToString(file.getBytes());
    }

    //findOrCreateTags() method has been implemented, which returns the list of tags after converting the ‘tags’ string to a list of all the tags and also stores the tags in the database if they do not exist in the database. Observe the method and complete the code where required for this method.
    //Try to get the tag from the database using getTagByName() method. If tag is returned, you need not to store that tag in the database, and if null is returned, you need to first store that tag in the database and then the tag is added to a list
    //After adding all tags to a list, the list is returned
    private List<Tag> findOrCreateTags(String tagNames) {
        StringTokenizer st = new StringTokenizer(tagNames, ",");
        List<Tag> tags = new ArrayList<Tag>();

        while (st.hasMoreTokens()) {
            String tagName = st.nextToken().trim();
            Tag tag = tagService.getTagByName(tagName);

            if (tag == null) {
                Tag newTag = new Tag(tagName);
                tag = tagService.createTag(newTag);
            }
            tags.add(tag);
        }
        return tags;
    }

    private String convertTagsToString(List<Tag> tags) {
        StringBuilder tagString = new StringBuilder();

        for (int i = 0; i <= tags.size() - 2; i++) {
            tagString.append(tags.get(i).getName()).append(",");
        }

        Tag lastTag = tags.get(tags.size() - 1);
        tagString.append(lastTag.getName());

        return tagString.toString();
    }
}
