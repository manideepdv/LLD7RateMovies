package com.example.bmsbookticket.services;

import com.example.bmsbookticket.exceptions.MovieNotFoundException;
import com.example.bmsbookticket.exceptions.UserNotFoundException;
import com.example.bmsbookticket.models.Movie;
import com.example.bmsbookticket.models.Rating;
import com.example.bmsbookticket.models.User;
import com.example.bmsbookticket.repositories.MovieRepository;
import com.example.bmsbookticket.repositories.RatingRepository;
import com.example.bmsbookticket.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.OptionalDouble;

@Service
public class RatingsServiceImpl implements RatingsService {
    private final UserRepository userRepository;
    private final MovieRepository movieRepository;
    private final RatingRepository ratingRepository;

    @Autowired
    public RatingsServiceImpl(UserRepository userRepository, MovieRepository movieRepository, RatingRepository ratingRepository) {
        this.userRepository = userRepository;
        this.movieRepository = movieRepository;
        this.ratingRepository = ratingRepository;
    }

    @Override
    public Rating rateMovie(int userId, int movieId, int rating) throws UserNotFoundException, MovieNotFoundException {
        Optional<User> optionalUser = userRepository.findById(userId);
        if (optionalUser.isEmpty()) {
            throw new UserNotFoundException("User not found");
        }
        Optional<Movie> optionalMovie = movieRepository.findById(movieId);
        if (optionalMovie.isEmpty()) {
            throw new MovieNotFoundException("Movie not found");
        }
        Movie movie = optionalMovie.get();
        User user = optionalUser.get();
        Optional<Rating> optionalRating = ratingRepository.findByUserAndMovie(user, movie);
        if (optionalRating.isEmpty()) {
            Rating ratingObj = new Rating();
            ratingObj.setUser(user);
            ratingObj.setMovie(movie);
            ratingObj.setRating(rating);
            return ratingRepository.save(ratingObj);
        } else {
            Rating ratingObj = optionalRating.get();
            ratingObj.setRating(rating);
            return ratingRepository.save(ratingObj);
        }
    }

    @Override
    public double getAverageRating(int movieId) throws MovieNotFoundException {
        Optional<Movie> optionalMovie = movieRepository.findById(movieId);
        if (optionalMovie.isEmpty()) {
            throw new MovieNotFoundException("Movie not found");
        }
        Movie movie = optionalMovie.get();
        List<Rating> ratingListByMovie = ratingRepository.findAllByMovie(movie);
        OptionalDouble optionalDoubleAverageRating = ratingListByMovie.stream().mapToDouble(Rating::getRating).average();
        if (optionalDoubleAverageRating.isPresent()) {
            return optionalDoubleAverageRating.getAsDouble();
        }
        return 0;
    }
}
