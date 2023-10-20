package com.batch.errorhandling.titles;

public record Title(String id,
                    String title,
                    String type,
                    String description,
                    int release_year,
                    String age_certification,
                    int runtime,

                    String genres,
                    String production_countries,
                    String seasons,
                    String imdb_id,
                    String imdb_score,
                    String imdb_votes,
                    String tmdb_popularity,
                    String tmdb_score) {
}
