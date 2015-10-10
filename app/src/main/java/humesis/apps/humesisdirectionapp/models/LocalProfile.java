package humesis.apps.humesisdirectionapp.models;

/**
 * Created by dhanraj on 09/10/15.
 */
public class LocalProfile {

    String name;
    String profilePic;
    String email;

    public LocalProfile() {
        this.name = "";
        this.profilePic = "";
        this.email = "";
    }

    public LocalProfile(String name, String profilePic, String email) {
        this.name = name;
        this.profilePic = profilePic;
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProfilePic() {
        return profilePic;
    }

    public void setProfilePic(String profilePic) {
        this.profilePic = profilePic;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
