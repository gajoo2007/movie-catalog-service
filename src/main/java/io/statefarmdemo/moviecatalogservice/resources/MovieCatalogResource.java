package io.statefarmdemo.moviecatalogservice.resources;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

import io.statefarmdemo.moviecatalogservice.models.CatalogItem;
import io.statefarmdemo.moviecatalogservice.models.Movie;
import io.statefarmdemo.moviecatalogservice.models.Rating;
import io.statefarmdemo.moviecatalogservice.models.UserRating;

@RestController
@RequestMapping("/catalog")
public class MovieCatalogResource {

	@Autowired
	private RestTemplate restTemplate;
	
	@Autowired
	private WebClient.Builder webClientBuilder;
	
	@RequestMapping("/{userId}")
	public List<CatalogItem> getCatalog(@PathVariable("userId") String userId){

		/*
		 * List<Rating> ratings = Arrays.asList( new Rating("1234", 4), new
		 * Rating("4567", 5) );
		 */
		
		UserRating ratings = restTemplate.getForObject("http://localhost:8083/ratingdata/users/" +userId, UserRating.class);
		
		return ratings.getUserRating().stream().map(rating -> {
			//For each movie id, call movie info service and get details.
			Movie movie = restTemplate.getForObject("http://localhost:8082/movies/" +rating.getMovieId(), Movie.class);
			// Put them all together
			return new CatalogItem(movie.getName(), "Desc", rating.getRating());
		})
		.collect(Collectors.toList());
	}
}

//React way using WebClient
/*Movie movie = webClientBuilder.build()
			.get()
			.uri("http://localhost:8082/movies/" +rating.getMovieId())
			.retrieve()
			.bodyToMono(Movie.class)
			.block();
*/
