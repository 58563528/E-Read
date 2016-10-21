package com.zmt.e_read.Utils;

import android.util.Log;

import com.zmt.e_read.Module.Image;
import com.zmt.e_read.Module.Movie;
import com.zmt.e_read.Module.MovieChannel;
import com.zmt.e_read.Module.News;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Dangelo on 2016/9/28.
 */
public class Analyse {

    public void analyseNewsList(boolean loading, String channelID, String jsonData, List<News> newsList){
        try {
            if(!loading){
                newsList.clear();
            } else {
                newsList.remove(newsList.size() - 1);
            }
            JSONObject jsonObject = new JSONObject(jsonData);
            JSONArray jsonArray = jsonObject.getJSONArray(channelID.substring(0, channelID.length() - 1));
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject newsObject = (JSONObject)jsonArray.get(i);
                String title = newsObject.getString("title");
                String time = newsObject.getString("ptime");
                String imageSrc = newsObject.getString("imgsrc");
                News news = new News();
                news.setTitle(title).addImageSrc(imageSrc).setTime(time);
                if(newsObject.has("url_3w") || newsObject.has("url")){
                    news.setType(News.TEXT_NEWS);
                    String content = newsObject.getString("digest");
                    String docId = newsObject.getString("docid");
                    String source = newsObject.getString("source");
                    String contentUrl;
                    if(newsObject.has("url_3w")){
                        contentUrl = newsObject.getString("url_3w");
                    } else {
                        contentUrl = newsObject.getString("url_3w");
                    }
                    news.setSource(source).setDigest(content).setDocId(docId).setContentUrl(contentUrl);
                } else {
                    news.setType(News.IMAGE_NEWS);
                    if(newsObject.has("imgextra")){
                        JSONArray imageArray = newsObject.getJSONArray("imgextra");
                        for (int j = 0; j <imageArray.length(); j++) {
                            JSONObject object = (JSONObject)imageArray.get(j);
                            news.addImageSrc(object.get("imgsrc").toString());
                        }
                    }
                }
                newsList.add(news);
            }
            newsList.add(null);
        } catch (JSONException e) {
            Log.e("error", e.toString());
        }
    }

    public String analyseNewsDetail(String docId, String jsonData){
        try {
            JSONObject jsonObject = new JSONObject(jsonData);
            JSONObject object = (JSONObject) jsonObject.get(docId);
            String news_detail = object.get("body").toString();
            String imageUrl;
            String imageElement;
            JSONArray jsonArray = object.getJSONArray("img");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject imageObject = (JSONObject) jsonArray.get(i);
                imageUrl = imageObject.get("src").toString();
                imageElement = "<img src=\"" + imageUrl +  "\"/><br>";
                news_detail = news_detail.replace(imageObject.get("ref").toString(), imageElement);
            }
            return news_detail;
        } catch (JSONException e) {
            Log.e("json error", e.toString());
            return null;
        }
    }

    public void analyseImage(boolean loading, String jsonData, List<Image> imageList){
        if(!loading){
            imageList.clear();
        } else {
            imageList.remove(imageList.size() - 1);
        }
        try {
            JSONObject jsonObject = new JSONObject(jsonData);
            JSONArray jsonArray = jsonObject.getJSONArray("results");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject object = (JSONObject) jsonArray.get(i);
                Image image = new Image();
                image.setImageDesc(object.get("desc").toString())
                        .setImageUrl(object.get("url").toString());
                imageList.add(image);
            }
        } catch (JSONException e) {
            Log.e("json error", e.toString());
        }
    }

    public String analyseMovieList(boolean loading, String type, String html, List<Movie> movieList){
        if(loading){
            movieList.remove(movieList.size() - 1);
        } else {
            movieList.clear();
        }
        int count = 1;
        if(type.equals(MovieChannel.NewestFilm)){
            count = 0;
        }
        try{
            Document document = Jsoup.parse(html);
            Elements elements = document.getElementsByClass("co_content8");
            for (Element element : elements) {
                if(element.tagName().equals("div")){
                    /**
                     * movie node
                     */
                    Elements movieElements = element.getElementsByTag("table");
                    for (Element movieElement : movieElements){
                        /**
                         * Link Node，include movie link and movie name
                         */
                        Elements hrefElement = movieElement.getElementsByTag("a");
                        Log.e("link node", hrefElement.size() + "");
                        String movieName = hrefElement.get(count).text();
                        String movieUrlSuffix = hrefElement.get(count).attr("href");
                        Elements timeElement = movieElement.getElementsByTag("font");
                        String releaseTime = timeElement.get(0).text();
                        Movie movie = new Movie();
                        movie.setName(movieName).setReleaseTime(releaseTime).setUrl(Movie.url + movieUrlSuffix);
                        movieList.add(movie);
                    }
                    movieList.add(null);
                    /**
                     * movie count
                     */
                    Elements countElements = element.getElementsByClass("x");
                    return countElements.get(0).text();
                }
            }
            return Movie.ERROR;
        } catch(Exception e){
            Log.e("analyse error", e.toString());
            return Movie.TAG;
        }
    }

    public void analyseMovieDetail(String html, Map<String, String> map){
        int count = 0;
        Document document = Jsoup.parse(html);
        Log.e("html", document.toString());
        Element element = document.getElementById("Zoom");
        Elements elements = element.getElementsByTag("p");
        Element movieInfo = elements.get(count);
        Elements imageElements = movieInfo.getElementsByTag("img");
        Elements downloadElement = element.getElementsByTag("tbody");
        String downloadUrl = downloadElement.get(count).getElementsByTag("a").get(count).attr("href");
        map.put("movie_downloadUrl", downloadUrl);
        map.put("movie_image", movieInfo.getElementsByTag("img").get(count).attr("src"));
        map.put("movie_content", movieInfo.text());
        if(imageElements.size() == 2){
            map.put("movie_preview", movieInfo.getElementsByTag("img").get(1).attr("src"));
        }
    }

}