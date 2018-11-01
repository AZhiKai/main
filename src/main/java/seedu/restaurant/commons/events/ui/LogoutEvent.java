package seedu.restaurant.commons.events.ui;

import seedu.restaurant.commons.core.session.UserSession;
import seedu.restaurant.commons.events.BaseEvent;

/**
 * Indicates a user has just logged out of system.
 */
public class LogoutEvent extends BaseEvent {

    public LogoutEvent() {
        UserSession.destroy();
    }

    @Override
    public String toString() {
        return getClass().getSimpleName();
    }
}
