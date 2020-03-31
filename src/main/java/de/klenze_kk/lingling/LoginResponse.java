package de.klenze_kk.lingling;

import de.klenze_kk.lingling.logic.User;

public interface LoginResponse {

    public boolean successful();

    public boolean userExists();

    public User getCreatedUser() throws UnsupportedOperationException;

    public static final class SuccessfulLogin implements LoginResponse {

        private final User user;

        protected SuccessfulLogin(User user) {
            this.user = user;
        }

        public boolean successful() {
            return true;
        }

        public boolean userExists() {
            return true;
        }

        public User getCreatedUser() {
            return user;
        }

    }

    public static enum FailedLogin implements LoginResponse {

        USER_UNKNOWN(false), WRONG_PASSWORD(true);

        private final boolean userExists;

        private FailedLogin(boolean userExists) {
            this.userExists = userExists;
        }

        public boolean successful() {
            return false;
        }

        public boolean userExists() {
            return userExists;
        }

        public User getCreatedUser() throws UnsupportedOperationException {
            throw new UnsupportedOperationException("Login failed - no user was created");
        }

    }

}
