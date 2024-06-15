package keyvi.objects;


public class UserResult {
    private final UserModel user;
    private final String errorMessage;

    public UserResult(UserModel user, String errorMessage) {
        this.user = user;
        this.errorMessage = errorMessage;
    }

    public UserModel getUser() {
        return user;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}