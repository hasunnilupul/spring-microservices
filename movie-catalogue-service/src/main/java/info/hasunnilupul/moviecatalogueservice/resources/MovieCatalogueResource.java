package info.hasunnilupul.moviecatalogueservice.resources;

import info.hasunnilupul.moviecatalogueservice.model.CatalogueItem;
import info.hasunnilupul.moviecatalogueservice.model.Movie;
import info.hasunnilupul.moviecatalogueservice.model.UserRating;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/catalogue")
public class MovieCatalogueResource {

    private final WebClient.Builder webClientBuilder;

    public MovieCatalogueResource(WebClient.Builder webClientBuilder) {
        this.webClientBuilder = webClientBuilder;
    }

    @GetMapping("/{userId}")
    public List<CatalogueItem> getCatalogue(@PathVariable("userId") String userId) {
        UserRating ratings = webClientBuilder.build().get().uri("http://ratings-data-service/ratings/users/" + userId).retrieve().bodyToMono(UserRating.class).block();
        return ratings.getUserRating().stream().map(rating -> {
            Movie movie = webClientBuilder.build().get().uri("http://movie-info-service/movies/" + rating.getMovieId()).retrieve().bodyToMono(Movie.class).block();
            return new CatalogueItem(movie.getName(), "A team of earth mightiest", rating.getRating());
        }).collect(Collectors.toList());
    }
}