package at.unleashit.caspmodulith.services.gateway;

import at.unleashit.caspmodulith.services.members.MemberExternalAPI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api")
@Validated
public class GatewayRestController {

    private MemberExternalAPI memberExternalAPI;

    @Autowired
    public GatewayRestController(MemberExternalAPI memberExternalAPI) {
        this.memberExternalAPI = memberExternalAPI;
    }

    @DeleteMapping({"/member/{id}"})
    public ResponseEntity<?> deleteMember(@PathVariable("id") UUID id) {
        try {
            memberExternalAPI.deactivateMember(id);
            return ResponseEntity.ok("Member was deleted");
        } catch(Exception e) {
            return new ResponseEntity<>(e.getMessage(),
                    HttpStatus.EXPECTATION_FAILED);
        }
    }

}
