package casp.web.backend.dog.presentation;

import casp.web.backend.dog.DogHasHandlerDto;
import casp.web.backend.dog.DogHasHandlerService;
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
import java.util.Set;

import static casp.web.backend.dog.DogHasHandlerMapper.DOG_HAS_HANDLER_MAPPER;
import static casp.web.backend.dog.presentation.DogHasHandlerReadMapper.READ_MAPPER;
import static casp.web.backend.dog.presentation.DogHasHandlerWriteMapper.WRITE_MAPPER;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DogHasHandlerRestControllerTest {
    @Mock
    private DogHasHandlerService dogHasHandlerService;

    @InjectMocks
    private DogHasHandlerRestController controller;
    private DogHasHandlerDto dogHasHandlerDto;

    @BeforeEach
    void setUp() {
        dogHasHandlerDto = DOG_HAS_HANDLER_MAPPER.toTarget(TestFixture.createDogHasHandler());
        dogHasHandlerDto.setMemberId(dogHasHandlerDto.getMember().getId());
        dogHasHandlerDto.setDogId(dogHasHandlerDto.getDog().getId());
    }

    @Test
    void getDogHasHandlerById() {
        when(dogHasHandlerService.getDogHasHandlerById(dogHasHandlerDto.getId())).thenReturn(dogHasHandlerDto);

        var response = controller.getDogHasHandlerById(dogHasHandlerDto.getId());

        assertSame(HttpStatus.OK, response.getStatusCode());
        assertThat(response.getBody()).isEqualTo(READ_MAPPER.toTarget(dogHasHandlerDto));
    }

    @Test
    void saveDogHasHandler() {
        when(dogHasHandlerService.saveDogHasHandler(argThat(dhh -> dogHasHandlerDto.getId().equals(dhh.getId())))).thenReturn(dogHasHandlerDto);

        var response = controller.saveDogHasHandler(WRITE_MAPPER.toTarget(dogHasHandlerDto));

        assertSame(HttpStatus.OK, response.getStatusCode());
        assertThat(response.getBody()).isEqualTo(READ_MAPPER.toTarget(dogHasHandlerDto));
    }

    @Test
    void deleteDogHasHandlerById() {
        var response = controller.deleteDogHasHandlerById(dogHasHandlerDto.getId());

        assertSame(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(dogHasHandlerService).deleteDogHasHandlerById(dogHasHandlerDto.getId());
    }

    @Test
    void searchByName() {
        when(dogHasHandlerService.searchByValue(dogHasHandlerDto.getDog().getName(), Pageable.unpaged())).thenReturn(new PageImpl<>(List.of(dogHasHandlerDto)));

        var response = controller.searchByValue(dogHasHandlerDto.getDog().getName(), Pageable.unpaged());

        assertSame(HttpStatus.OK, response.getStatusCode());
        assertThat(response.getBody()).containsExactly(READ_MAPPER.toTarget(dogHasHandlerDto));
    }

    @Test
    void getAllDogHasHandlers() {
        when(dogHasHandlerService.getAllDogHasHandlers(Pageable.unpaged())).thenReturn(new PageImpl<>(List.of(dogHasHandlerDto)));

        var response = controller.getAllDogHasHandlers(Pageable.unpaged());

        assertSame(HttpStatus.OK, response.getStatusCode());
        assertThat(response.getBody()).containsExactly(READ_MAPPER.toTarget(dogHasHandlerDto));
    }

    @Test
    void getDogHasHandlersByHandlerIds() {
        var idSet = Set.of(dogHasHandlerDto.getId());
        when(dogHasHandlerService.getDogHasHandlersByIds(idSet)).thenReturn(Set.of(dogHasHandlerDto));

        var response = controller.getDogHasHandlersByHandlerIds(idSet);

        assertSame(HttpStatus.OK, response.getStatusCode());
        assertThat(response.getBody()).containsExactly(READ_MAPPER.toTarget(dogHasHandlerDto));
    }

    @Test
    void getMembersEmailByIds() {
        var idSet = Set.of(dogHasHandlerDto.getId());
        when(dogHasHandlerService.getEmailsByDogHasHandlersIds(idSet)).thenReturn(Set.of(dogHasHandlerDto.getMember().getEmail()));

        var response = controller.getMembersEmailByIds(idSet);

        assertSame(HttpStatus.OK, response.getStatusCode());
        assertThat(response.getBody()).containsExactly(dogHasHandlerDto.getMember().getEmail());
    }

    @Test
    void getDogHasHandlerByMemberId() {
        when(dogHasHandlerService.getDogHasHandlerByMemberId(dogHasHandlerDto.getMemberId())).thenReturn(Set.of(dogHasHandlerDto));

        var response = controller.getDogHasHandlerByMemberId(dogHasHandlerDto.getMemberId());

        assertSame(HttpStatus.OK, response.getStatusCode());
        assertThat(response.getBody()).containsExactly(READ_MAPPER.toTarget(dogHasHandlerDto));
    }

    @Test
    void getDogHasHandlerByDogId() {
        when(dogHasHandlerService.getDogHasHandlerByDogId(dogHasHandlerDto.getDogId())).thenReturn(Set.of(dogHasHandlerDto));

        var response = controller.getDogHasHandlerByDogId(dogHasHandlerDto.getDogId());

        assertSame(HttpStatus.OK, response.getStatusCode());
        assertThat(response.getBody()).containsExactly(READ_MAPPER.toTarget(dogHasHandlerDto));
    }

    @Test
    void correctEntityStatus() {
        var response = controller.correctEntityStatus();

        assertSame(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(dogHasHandlerService).correctEntityStatus();
    }
}
