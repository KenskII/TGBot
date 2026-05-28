package org.example;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Parser {
    private static final String CINEMA_URL = "https://kinoteatr.ru/raspisanie-kinoteatrov/belgorod/belgorodskiy/";

    public static List<MovieSession> parseMovieSessions() throws IOException {
        List<MovieSession> sessions = new ArrayList<>();
        Document doc = Jsoup.connect(CINEMA_URL).get();

        //блоки с фильмами
        Elements movieElements = doc.select(".shedule_movie.bordered");

        for (Element movieElement : movieElements) {
            String title = movieElement.select(".shedule_movie_description > .movie_card_header.title").text();
            String genre = movieElement.select(".shedule_movie_description > .movie_card_raiting.sub_title").text()
                    .replaceFirst("\\d+\\+", "").trim();
            String duration = movieElement.select(".shedule_movie_description  > .title").last().text();

            //сеансы
            Elements sessionElements = movieElement.select(".shedule_session");
            List<String> sessionTimes = new ArrayList<>();

            for (Element sessionElement : sessionElements) {
                String time = sessionElement.select(".shedule_session_time").text().trim();
                String price = sessionElement.select(".shedule_session_price").text().trim();
                String format = sessionElement.select(".shedule_session_format").first().text().trim();

                sessionTimes.add(String.format("%s (%s) - %s", time, format, price));
            }

            if (!sessionTimes.isEmpty()) {
                sessions.add(new MovieSession(title, genre, duration, sessionTimes));
            }
        }

        return sessions;
    }

    public static class MovieSession {
        private final String title;
        private final String genre;
        private final String duration;
        private final List<String> sessions;

        public MovieSession(String title, String genre, String duration, List<String> sessions) {
            this.title = title;
            this.genre = genre;
            this.duration = duration;
            this.sessions = sessions;
        }

        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("*").append(title).append("*").append("\n");
            sb.append("Жанр: ").append(genre).append("\n");
            sb.append("Длительность: ").append(duration).append("\n");
            sb.append("Сеансы:\n");

            for (String session : sessions) {
                sb.append("• ").append(session).append("\n");
            }

            return sb.toString();
        }
    }
}