package org.example.bookingservice.models.services.impl;

import feign.FeignException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import org.example.bookingservice.clients.MovieClient;
import org.example.bookingservice.exceptions.MovieNotFoundException;
import org.example.bookingservice.exceptions.MovieServiceException;
import org.example.bookingservice.models.dto.responses.MovieResponse;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MovieGatewayService {

    private final MovieClient movieClient;

    @CircuitBreaker(name = "movieService", fallbackMethod = "getMovieFallback")
    public MovieResponse getMovieById(Long movieId) {
        MovieResponse movie = movieClient.getMovieById(movieId);

        if (movie == null || movie.id() == null || movie.ticketPrice() == null) {
            throw new MovieServiceException(
                    "Phim không tồn tại " + movieId
            );
        }
        if (movie.ticketPrice() < 0) {
            throw new MovieServiceException(
                    "Giá vé phim không hợp lệ " + movieId
            );
        }

        return movie;
    }

    private MovieResponse getMovieFallback(Long movieId, Throwable throwable) {
        if (throwable instanceof FeignException.NotFound) {
            throw new MovieNotFoundException(movieId);
        }
        if (throwable instanceof MovieNotFoundException movieNotFoundException) {
            throw movieNotFoundException;
        }
        if (throwable instanceof MovieServiceException movieServiceException) {
            throw movieServiceException;
        }

        throw new MovieServiceException(
                "Xuất hiện lỗi khi truy cập phim " + movieId,
                throwable
        );
    }
}
