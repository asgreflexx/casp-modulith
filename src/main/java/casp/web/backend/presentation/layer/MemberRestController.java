package casp.web.backend.presentation.layer;

import casp.web.backend.business.logic.layer.interfaces.IMemberService;
import casp.web.backend.data.access.layer.entities.Member;
import casp.web.backend.data.access.layer.enumerations.EntityStatus;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;
import java.util.UUID;

@RestController
@RequestMapping("/member")
@Validated
public class MemberRestController {

    private final IMemberService memberService;

    @Autowired
    MemberRestController(IMemberService memberService) {
        this.memberService = memberService;
    }

    @GetMapping({"/page"})
    public ResponseEntity<?> getData(@RequestParam Integer page, @RequestParam String numberOfElements, @RequestParam EntityStatus entityStatus) {

        try {
            return ResponseEntity.ok(memberService.getMembersByPage(page, numberOfElements, entityStatus));
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(),
                    HttpStatus.EXPECTATION_FAILED); // The http status can be changed for another 4XX
        }
    }

    @GetMapping({"/{id}"})
    public ResponseEntity<?> getDataById(@PathVariable("id") UUID id) {

        try {
            return ResponseEntity.ok(memberService.getMemberById(id));
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(),
                    HttpStatus.EXPECTATION_FAILED);
        }
    }

    @GetMapping({"/searchFnLn"})
    public ResponseEntity<?> getDataByFirstNameAndLastName(@RequestParam String firstName, @RequestParam String lastName) {

        try {
            return ResponseEntity.ok(memberService.getMembersByString(firstName, lastName));

        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(),
                    HttpStatus.EXPECTATION_FAILED);
        }
    }

    @PostMapping()
    public ResponseEntity<?> saveMember(@Valid @RequestBody Member member) {

        try {
            return ResponseEntity.ok(memberService.saveMember(member));
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(),
                    HttpStatus.EXPECTATION_FAILED);
        }
    }

    @DeleteMapping({"/{id}"})
    public ResponseEntity<?> deleteMethod(@PathVariable("id") UUID id) {

        try {
//          FIXME  calendarProxy.setMemberEntriesAndEventsStatus(id, EntityStatus.DELETED);
            memberService.deleteMemberById(id);
            return ResponseEntity.ok("Member was deleted");
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(),
                    HttpStatus.EXPECTATION_FAILED);
        }
    }

    @PutMapping({"{id}/deactivate"})
    public ResponseEntity<?> inactiveMethod(@PathVariable("id") UUID id) {
        try {
//        FIXME    calendarProxy.setMemberEntriesAndEventsStatus(id, EntityStatus.INACTIVE);
            memberService.inactiveMember(id);
            return ResponseEntity.ok("Member was deactivated");
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(),
                    HttpStatus.EXPECTATION_FAILED);
        }
    }

    @PutMapping("{id}/activate")
    public ResponseEntity<?> activateMember(@PathVariable("id") UUID id) {
        try {
//      FIXME      calendarProxy.setMemberEntriesAndEventsStatus(id, EntityStatus.ACTIVE);
            memberService.activateMember(id);
            return ResponseEntity.ok("Member was activated");
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(),
                    HttpStatus.EXPECTATION_FAILED);
        }
    }

    @GetMapping("/searchMemberByName")
    public ResponseEntity<?> searchMembersByFirstNameOrLastName(@RequestParam(required = false, defaultValue = "") String name) {
        try {
            return ResponseEntity.ok(memberService.searchMembersByFirstNameOrLastName(name));
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(),
                    HttpStatus.EXPECTATION_FAILED);
        }
    }

    @GetMapping("/getRoles")
    public ResponseEntity<?> getRoles() {
        try {
            return ResponseEntity.ok(memberService.listAllAvailableRoles());
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(),
                    HttpStatus.EXPECTATION_FAILED);
        }
    }

    @GetMapping("/get-emails-by-ids")
    public Set<String> getMembersEmailByIds(@RequestParam @Size(min = 1) Set<UUID> membersId) {
        return memberService.getMembersEmailByIds(membersId);
    }
}
