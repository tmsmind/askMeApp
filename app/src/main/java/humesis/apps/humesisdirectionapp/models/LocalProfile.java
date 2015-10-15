package humesis.apps.humesisdirectionapp.models;

/**
 * Created by dhanraj on 09/10/15.
 */
public class LocalProfile {

    public String name;
    public String profilePic;
    public String email;
    public String coverPic;

    public LocalProfile() {
        this.name = "";
        this.profilePic = "";
        this.email = "";
        this.coverPic = "";
    }

    public LocalProfile(String name, String profilePic, String email, String coverPic) {
        this.name = name;
        this.profilePic = profilePic;
        this.email = email;
        this.coverPic = coverPic;
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

    public String getCoverPic() {
        return coverPic;
    }

    public void setCoverPic(String coverPic) {
        this.coverPic = coverPic;
    }

    @Override
    public String toString() {
        return "LocalProfile{" +
                "name='" + name + '\'' +
                ", profilePic='" + profilePic + '\'' +
                ", email='" + email + '\'' +
                ", coverPic='" + coverPic + '\'' +
                '}';
    }
}
