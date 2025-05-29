package casp.web.backend.dog;

import casp.web.backend.common.enums.EntityStatus;
import casp.web.backend.common.reference.DogReference;
import casp.web.backend.common.reference.DogReferenceRepository;
import casp.web.backend.common.reference.MemberReference;
import casp.web.backend.common.reference.MemberReferenceRepository;
import casp.web.backend.deprecated.dog.DogHasHandlerOldRepository;
import casp.web.backend.dog.data.DogHasHandler;
import casp.web.backend.dog.data.DogHasHandlerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.*;
import java.util.stream.Stream;

import static casp.web.backend.dog.DogHasHandlerMapper.DOG_HAS_HANDLER_MAPPER;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DogHasHandlerServiceImplTest {
    @Mock
    private DogHasHandlerOldRepository dogHasHandlerOldRepository;
    @Mock
    private MemberReferenceRepository memberReferenceRepository;
    @Mock
    private DogReferenceRepository dogReferenceRepository;
    @Mock
    private DogHasHandlerRepository dogHasHandlerRepository;
    @Captor
    private ArgumentCaptor<DogHasHandler> dogHasHandlerCaptor;
    @Captor
    private ArgumentCaptor<Set<DogHasHandler>> dogHasHandlerSetCaptor;

    @InjectMocks
    private DogHasHandlerServiceImpl dogHasHandlerService;

    private DogReference dog;
    private MemberReference member;
    private DogHasHandler dogHasHandler;
    private DogHasHandlerDto dogHasHandlerDto;
    private PageImpl<DogHasHandler> dogHasHandlerPage;
    private Set<DogHasHandler> dogHasHandlerSet;

    @BeforeEach
    void setUp() {
        dogHasHandler = TestFixture.createDogHasHandler();
        dog = dogHasHandler.getDog();
        member = dogHasHandler.getMember();

        dogHasHandlerDto = DOG_HAS_HANDLER_MAPPER.toTarget(dogHasHandler);
        dogHasHandlerDto.setMemberId(member.getId());
        dogHasHandlerDto.setDogId(dog.getId());

        dogHasHandlerPage = new PageImpl<>(List.of(dogHasHandler));
        dogHasHandlerSet = Set.of(dogHasHandler);
    }

    @Test
    void deleteDogHasHandlersByDogId() {
        when(dogHasHandlerRepository.findAllByDogIdAndNotDeleted(dog.getId())).thenReturn(dogHasHandlerSet);

        dogHasHandlerService.deleteDogHasHandlersByDogId(dog.getId());

        verify(dogHasHandlerRepository).save(dogHasHandlerCaptor.capture());
        assertSame(EntityStatus.DELETED, dogHasHandlerCaptor.getValue().getEntityStatus());
    }

    @Test
    void deleteDogHasHandlersByMemberId() {
        when(dogHasHandlerRepository.findAllByMemberIdAndNotDeleted(member.getId())).thenReturn(dogHasHandlerSet);

        dogHasHandlerService.deleteDogHasHandlersByMemberId(member.getId());

        verify(dogHasHandlerRepository).save(dogHasHandlerCaptor.capture());
        assertSame(EntityStatus.DELETED, dogHasHandlerCaptor.getValue().getEntityStatus());
    }

    @Test
    void getAllDogHasHandlers() {
        when(dogHasHandlerRepository.findAllByEntityStatus(EntityStatus.ACTIVE, Pageable.unpaged())).thenReturn(dogHasHandlerPage);

        var dogHasHandlerDtoPage = dogHasHandlerService.getAllDogHasHandlers(Pageable.unpaged());

        assertThat(dogHasHandlerDtoPage).containsExactly(dogHasHandlerDto);
    }

    @Test
    void getDogHasHandlersByIds() {
        when(dogHasHandlerRepository.findAllByIdInAndEntityStatus(Set.of(dogHasHandler.getId()), EntityStatus.ACTIVE)).thenReturn(dogHasHandlerSet);

        var dogHasHandlerDtoSet = dogHasHandlerService.getDogHasHandlersByIds(Set.of(dogHasHandler.getId()));

        assertThat(dogHasHandlerDtoSet).containsExactly(dogHasHandlerDto);
    }

    @Test
    void deactivateDogHasHandlersByMemberId() {
        when(dogHasHandlerRepository.findAllByMemberIdAndEntityStatus(member.getId(), EntityStatus.ACTIVE)).thenReturn(dogHasHandlerSet);

        dogHasHandlerService.deactivateDogHasHandlersByMemberId(member.getId());

        verify(dogHasHandlerRepository).save(dogHasHandlerCaptor.capture());
        assertSame(EntityStatus.INACTIVE, dogHasHandlerCaptor.getValue().getEntityStatus());
    }

    @Test
    void activateDogHasHandlersByMemberId() {
        dogHasHandler.setEntityStatus(EntityStatus.INACTIVE);
        when(dogHasHandlerRepository.findAllByMemberIdAndEntityStatus(member.getId(), EntityStatus.INACTIVE)).thenReturn(Set.of(dogHasHandler));

        dogHasHandlerService.activateDogHasHandlersByMemberId(member.getId());

        verify(dogHasHandlerRepository).save(dogHasHandlerCaptor.capture());
        assertSame(EntityStatus.ACTIVE, dogHasHandlerCaptor.getValue().getEntityStatus());
    }

    @Test
    void searchByName() {
        when(dogHasHandlerRepository.findAllByValue(member.getLastName(), Pageable.unpaged())).thenReturn(dogHasHandlerPage);

        var dogHasHandlerDtoPage = dogHasHandlerService.searchByValue(member.getLastName(), Pageable.unpaged());

        assertThat(dogHasHandlerDtoPage).containsExactly(dogHasHandlerDto);
    }

    @Test
    void getEmailsByDogHasHandlersIds() {
        when(dogHasHandlerRepository.findAllByIdInAndEntityStatus(Set.of(dogHasHandler.getId()), EntityStatus.ACTIVE)).thenReturn(dogHasHandlerSet);

        var emailSet = dogHasHandlerService.getEmailsByDogHasHandlersIds(Set.of(dogHasHandler.getId()));

        assertThat(emailSet).containsExactly(member.getEmail());
    }

    @ParameterizedTest
    @MethodSource()
    void migrateDataToV2(EntityStatusData data) {
        var dogHasHandlerV1 = mock(casp.web.backend.deprecated.dog.DogHasHandler.class, Answers.RETURNS_DEEP_STUBS);
        when(dogHasHandlerV1.getDogId()).thenReturn(dog.getId());
        when(dogHasHandlerV1.getMemberId()).thenReturn(member.getId());
        when(dogHasHandlerV1.getEntityStatus()).thenReturn(EntityStatus.ACTIVE);
        when(dogHasHandlerOldRepository.findAll()).thenReturn(List.of(dogHasHandlerV1));
        dog.setEntityStatus(data.dogStatus);
        member.setEntityStatus(data.memberStatus);
        when(dogReferenceRepository.findById(dog.getId())).thenReturn(Optional.of(dog));
        when(memberReferenceRepository.findById(member.getId())).thenReturn(Optional.of(member));

        dogHasHandlerService.migrateDataToV2();

        verify(dogHasHandlerRepository).saveAll(dogHasHandlerSetCaptor.capture());

        assertThat(dogHasHandlerSetCaptor.getValue())
                .singleElement()
                .satisfies(dhh -> {
                    assertSame(data.expectedDogHasHandlerStatus, dhh.getEntityStatus());
                    assertSame(dog, dhh.getDog());
                    assertSame(member, dhh.getMember());
                });
    }

    @Test
    void getDogHasHandlerByMemberId() {
        when(dogHasHandlerRepository.findAllByMemberIdAndEntityStatus(member.getId(), EntityStatus.ACTIVE)).thenReturn(dogHasHandlerSet);

        var dogHasHandlerDtoSet = dogHasHandlerService.getDogHasHandlerByMemberId(member.getId());

        assertThat(dogHasHandlerDtoSet).containsExactly(dogHasHandlerDto);
    }

    @Test
    void getDogHasHandlerByDogId() {
        when(dogHasHandlerRepository.findAllByDogIdAndNotDeleted(dog.getId())).thenReturn(dogHasHandlerSet);

        var dogHasHandlerDtoSet = dogHasHandlerService.getDogHasHandlerByDogId(dog.getId());

        assertThat(dogHasHandlerDtoSet).containsExactly(dogHasHandlerDto);
    }

    @Nested
    class DeleteDogHasHandlerById {
        @Test
        void handlerExist() {
            when(dogHasHandlerRepository.findOneByIdAndEntityStatus(dogHasHandler.getId(), EntityStatus.ACTIVE)).thenReturn(Optional.of(dogHasHandler));

            dogHasHandlerService.deleteDogHasHandlerById(dogHasHandler.getId());

            verify(dogHasHandlerRepository).save(dogHasHandlerCaptor.capture());
            assertSame(EntityStatus.DELETED, dogHasHandlerCaptor.getValue().getEntityStatus());
        }

        @Test
        void handlerDoesNotExist() {
            var unknownId = UUID.randomUUID();
            assertThrows(NoSuchElementException.class, () -> dogHasHandlerService.deleteDogHasHandlerById(unknownId));
        }
    }

    @Nested
    class SaveDogHasHandler {
        @BeforeEach
        void setUp() {
            dogHasHandlerDto.setMember(null);
            dogHasHandlerDto.setDog(null);
        }

        @Test
        void dogDoesNotExist() {
            when(dogReferenceRepository.findOneByIdAndEntityStatus(dog.getId(), EntityStatus.ACTIVE)).thenReturn(Optional.empty());

            assertThrows(NoSuchElementException.class, () -> dogHasHandlerService.saveDogHasHandler(dogHasHandlerDto));
        }

        @Test
        void memberDoesNotExist() {
            when(dogReferenceRepository.findOneByIdAndEntityStatus(dog.getId(), EntityStatus.ACTIVE)).thenReturn(Optional.of(dog));
            when(memberReferenceRepository.findOneByIdAndEntityStatus(member.getId(), EntityStatus.ACTIVE)).thenReturn(Optional.empty());

            assertThrows(NoSuchElementException.class, () -> dogHasHandlerService.saveDogHasHandler(dogHasHandlerDto));
        }

        @Test
        void memberAndDogExist() {
            when(dogReferenceRepository.findOneByIdAndEntityStatus(dog.getId(), EntityStatus.ACTIVE)).thenReturn(Optional.of(dog));
            when(memberReferenceRepository.findOneByIdAndEntityStatus(member.getId(), EntityStatus.ACTIVE)).thenReturn(Optional.of(member));
            when(dogHasHandlerRepository.save(dogHasHandler)).thenAnswer(i -> i.getArgument(0));

            var actualDogHasHandlerDto = dogHasHandlerService.saveDogHasHandler(dogHasHandlerDto);

            assertSame(dog, actualDogHasHandlerDto.getDog());
            assertSame(member, actualDogHasHandlerDto.getMember());
        }

        @Test
        void dogHasHandlerExist() {
            var existingDogHasHandler = mock(DogHasHandler.class);
            when(existingDogHasHandler.getId()).thenReturn(dogHasHandler.getId());
            when(dogReferenceRepository.findOneByIdAndEntityStatus(dog.getId(), EntityStatus.ACTIVE)).thenReturn(Optional.of(dog));
            when(memberReferenceRepository.findOneByIdAndEntityStatus(member.getId(), EntityStatus.ACTIVE)).thenReturn(Optional.of(member));
            when(dogHasHandlerRepository.findByDogIdAndMemberId(dog.getId(), member.getId())).thenReturn(Optional.of(existingDogHasHandler));
            when(dogHasHandlerRepository.save(dogHasHandler)).thenAnswer(i -> i.getArgument(0));

            var actualDogHasHandlerDto = dogHasHandlerService.saveDogHasHandler(dogHasHandlerDto);

            assertSame(dog, actualDogHasHandlerDto.getDog());
            assertSame(member, actualDogHasHandlerDto.getMember());
        }

        @Test
        void dontSaveDuplicatedDogHasHandlers() {
            var existingDogHasHandler = new DogHasHandler();
            existingDogHasHandler.setDog(dog);
            existingDogHasHandler.setMember(member);
            when(dogReferenceRepository.findOneByIdAndEntityStatus(dog.getId(), EntityStatus.ACTIVE)).thenReturn(Optional.of(dog));
            when(memberReferenceRepository.findOneByIdAndEntityStatus(member.getId(), EntityStatus.ACTIVE)).thenReturn(Optional.of(member));
            when(dogHasHandlerRepository.findByDogIdAndMemberId(dog.getId(), member.getId())).thenReturn(Optional.of(existingDogHasHandler));

            assertThrows(IllegalStateException.class, () -> dogHasHandlerService.saveDogHasHandler(dogHasHandlerDto));
        }
    }

    @Nested
    class GetDogHasHandlerById {
        @Test
        void handlerExist() {
            when(dogHasHandlerRepository.findOneByIdAndEntityStatus(dogHasHandler.getId(), EntityStatus.ACTIVE)).thenReturn(Optional.of(dogHasHandler));

            var actualDogHasHandlerDto = dogHasHandlerService.getDogHasHandlerById(dogHasHandler.getId());

            assertEquals(dogHasHandlerDto, actualDogHasHandlerDto);
        }

        @Test
        void handlerDoesNotExist() {
            var unknownId = UUID.randomUUID();
            assertThrows(NoSuchElementException.class, () -> dogHasHandlerService.getDogHasHandlerById(unknownId));
        }
    }

    private static Stream<EntityStatusData> migrateDataToV2() {
        return Stream.of(new EntityStatusData(EntityStatus.DELETED, EntityStatus.INACTIVE, EntityStatus.DELETED),
                new EntityStatusData(EntityStatus.ACTIVE, EntityStatus.DELETED, EntityStatus.DELETED),
                new EntityStatusData(EntityStatus.INACTIVE, EntityStatus.ACTIVE, EntityStatus.INACTIVE),
                new EntityStatusData(EntityStatus.ACTIVE, EntityStatus.INACTIVE, EntityStatus.INACTIVE));
    }

    private record EntityStatusData(EntityStatus dogStatus, EntityStatus memberStatus,
                                    EntityStatus expectedDogHasHandlerStatus) {
    }
}
