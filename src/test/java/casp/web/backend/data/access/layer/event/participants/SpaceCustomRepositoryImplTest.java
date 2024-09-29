package casp.web.backend.data.access.layer.event.participants;

import casp.web.backend.TestFixture;
import casp.web.backend.data.access.layer.dog.DogHasHandlerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;

import static org.assertj.core.api.Assertions.assertThat;

@DataMongoTest
class SpaceCustomRepositoryImplTest {
    @Autowired
    private DogHasHandlerRepository dogHasHandlerRepository;
    @Autowired
    private SpaceRepository spaceRepository;
    private Space space;

    @BeforeEach
    void setUp() {
        spaceRepository.deleteAll();
        dogHasHandlerRepository.deleteAll();

        space = TestFixture.createSpace();
        dogHasHandlerRepository.save(space.getDogHasHandler());
        spaceRepository.save(space);
    }

    @Test
    void findAllByMemberId() {
        var spaces = spaceRepository.findAllByMemberId(space.getDogHasHandler().getMemberId());

        assertThat(spaces).containsExactly(space);
    }

    @Test
    void findAllByDogId() {
        var spaces = spaceRepository.findAllByDogId(space.getDogHasHandler().getDogId());

        assertThat(spaces).containsExactly(space);
    }
}
