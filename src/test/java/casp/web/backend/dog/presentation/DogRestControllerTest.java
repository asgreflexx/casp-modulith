package casp.web.backend.dog.presentation;

import casp.web.backend.dog.DogDto;
import casp.web.backend.dog.DogService;
import casp.web.backend.dog.EuropeNetTasks;
import casp.web.backend.dog.TestFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.Optional;
import java.util.Random;

import static casp.web.backend.dog.DogMapper.DOG_MAPPER;
import static casp.web.backend.dog.presentation.DogReadMapper.READ_MAPPER;
import static casp.web.backend.dog.presentation.DogWriteMapper.WRITE_MAPPER;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DogRestControllerTest {
    @Mock
    private DogService dogService;
    @Mock
    private EuropeNetTasks europeNetTasks;

    @InjectMocks
    private DogRestController dogRestController;
    private DogDto dog;

    @BeforeEach
    void setUp() {
        dog = DOG_MAPPER.toTarget(TestFixture.createDog());
        dog.setChipNumber(String.valueOf(new Random().nextInt()));
    }

    @Test
    void getDogById() {
        when(dogService.getDogById(dog.getId())).thenReturn(dog);

        var response = dogRestController.getDogById(dog.getId());

        assertSame(HttpStatus.OK, response.getStatusCode());
        assertThat(response.getBody()).isEqualTo(READ_MAPPER.toTarget(dog));
    }

    @Test
    void getDogs() {
        when(dogService.getDogs("", Pageable.unpaged())).thenReturn(new PageImpl<>(List.of(dog)));

        var response = dogRestController.getDogs("", Pageable.unpaged());

        assertSame(HttpStatus.OK, response.getStatusCode());
        assertThat(response.getBody()).containsExactly(READ_MAPPER.toTarget(dog));
    }

    @Test
    void saveDog() {
        when(dogService.saveDog(dog)).thenReturn(dog);

        var response = dogRestController.saveDog(WRITE_MAPPER.toTarget(dog));

        assertSame(HttpStatus.OK, response.getStatusCode());
        assertThat(response.getBody()).isEqualTo(READ_MAPPER.toTarget(dog));
    }

    @Test
    void deleteDogById() {
        var response = dogRestController.deleteDogById(dog.getId());

        assertSame(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(dogService).deleteDogById(dog.getId());
    }

    @Test
    void register() {
        when(europeNetTasks.registerDogsManually(Pageable.unpaged())).thenReturn(new PageImpl<>(List.of(dog)));

        var response = dogRestController.register(Pageable.unpaged());

        assertSame(HttpStatus.OK, response.getStatusCode());
        assertThat(response.getBody()).containsExactly(READ_MAPPER.toTarget(dog));
    }

    @Test
    void getDogByChipNumber() {
        when(dogService.getDogByChipNumber(dog.getChipNumber())).thenReturn(Optional.of(dog));

        var response = dogRestController.getDogByChipNumber(dog.getChipNumber());

        assertSame(HttpStatus.OK, response.getStatusCode());
        assertThat(response.getBody()).isEqualTo(READ_MAPPER.toTarget(dog));
    }

    @Test
    void getDogsByNameOrOwnerName() {
        when(dogService.getDogsByNameOrOwnerName(dog.getName(), dog.getOwnerName(), Pageable.unpaged())).thenReturn(new PageImpl<>(List.of(dog)));

        var response = dogRestController.getDogsByNameOrOwnerName(dog.getName(), dog.getOwnerName(), Pageable.unpaged());

        assertSame(HttpStatus.OK, response.getStatusCode());
        assertThat(response.getBody()).containsExactly(READ_MAPPER.toTarget(dog));
    }
}
