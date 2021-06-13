package VideoHoster.repository;

import VideoHoster.model.Video;
import org.springframework.stereotype.Repository;

import javax.persistence.*;
import java.util.List;

//The annotation is a special type of @Component annotation which describes that the class defines a data repository
@Repository
public class VideoRepository {

    //Get an instance of EntityManagerFactory from persistence unit with name as 'videoHoster'
    @PersistenceUnit(unitName = "videoHoster")
    private EntityManagerFactory emf;


    //The method receives the Video object to be persisted in the database
    //Creates an instance of EntityManager
    //Starts a transaction
    //The transaction is committed if it is successful
    //The transaction is rolled back in case of unsuccessful transaction
    public Video uploadVideo(Video newVideo) {

        EntityManager em = emf.createEntityManager();
        EntityTransaction transaction = em.getTransaction();

        try {
            transaction.begin();
            em.persist(newVideo);
            transaction.commit();
        } catch (Exception e) {
            transaction.rollback();
        }
        return newVideo;
    }

    //The method creates an instance of EntityManager
    //Executes JPQL query to fetch all the videos from the database
    //Returns the list of all the videos fetched from the database
    public List<Video> getAllVideos() {
        EntityManager em = emf.createEntityManager();
        TypedQuery<Video> query = em.createQuery("SELECT i from Video i", Video.class);
        List<Video> resultList = query.getResultList();

        return resultList;
    }

    //The method creates an instance of EntityManager
    //Executes JPQL query to fetch the video from the database with corresponding title
    //Returns the video in case the video is found in the database
    //Returns null if no video is found in the database
    public Video getVideoByTitle(String title) {
        EntityManager em = emf.createEntityManager();
        try {
            TypedQuery<Video> typedQuery = em.createQuery("SELECT i from Video i where i.title =:title", Video.class).setParameter("title", title);
            return typedQuery.getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }
    }

    //The method creates an instance of EntityManager
    //Executes JPQL query to fetch the video from the database with corresponding id
    //Returns the video fetched from the database
    public Video getVideo(Integer videoId) {
        EntityManager em = emf.createEntityManager();
        TypedQuery<Video> typedQuery = em.createQuery("SELECT i from Video i where i.id =:videoId", Video.class).setParameter("videoId", videoId);
        Video video = typedQuery.getSingleResult();
        return video;
    }

    //The method receives the Video object to be updated in the database
    //Creates an instance of EntityManager
    //Starts a transaction
    //The transaction is committed if it is successful
    //The transaction is rolled back in case of unsuccessful transaction
    public void updateVideo(Video updatedVideo) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction transaction = em.getTransaction();

        try {
            transaction.begin();
            em.merge(updatedVideo);
            transaction.commit();
        } catch (Exception e) {
            transaction.rollback();
        }
    }

    //The method receives the Video id of the video to be deleted in the database
    //Creates an instance of EntityManager
    //Starts a transaction
    //Get the video with corresponding video id from the database
    //This changes the state of the video model from detached state to persistent state, which is very essential to use the remove() method
    //If you use remove() method on the object which is not in persistent state, an exception is thrown
    //The transaction is committed if it is successful
    //The transaction is rolled back in case of unsuccessful transaction
    public void deleteVideo(Integer videoId) {
        EntityManager em = emf.createEntityManager();
        EntityTransaction transaction = em.getTransaction();

        try {
            transaction.begin();
            Video video = em.find(Video.class, videoId);
            em.remove(video);
            transaction.commit();
        } catch (Exception e) {
            transaction.rollback();
        }
    }

}
