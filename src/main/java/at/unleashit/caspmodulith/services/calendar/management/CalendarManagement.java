package at.unleashit.caspmodulith.services.calendar.management;

import at.unleashit.caspmodulith.enums.EntityStatus;
import at.unleashit.caspmodulith.services.MemberStatusChangeEvent;
import at.unleashit.caspmodulith.services.calendar.CalendarExternalAPI;
import at.unleashit.caspmodulith.services.calendar.CalendarInternalAPI;
import org.springframework.modulith.events.ApplicationModuleListener;

import java.util.UUID;

public class CalendarManagement implements CalendarExternalAPI, CalendarInternalAPI {

    @ApplicationModuleListener
    void onMemberStatusChange(MemberStatusChangeEvent memberStatusChangeEvent) {

    }

}
