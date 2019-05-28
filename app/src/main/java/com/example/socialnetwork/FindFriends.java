package com.example.socialnetwork;

import com.google.firebase.auth.FirebaseAuth;

public class FindFriends {
    public String profileimage, username, level, team, uid;

    public FindFriends() {
    }

    public FindFriends(String profileimage, String username, String level, String team) {
        this.profileimage = profileimage;
        this.username = username;
        this.level = level;
        this.team = team;
    }

    public String getProfileimage() {
        return profileimage;
    }

    public void setProfileimage(String profileimage) {
        this.profileimage = profileimage;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getTeam() {
        return team;
    }

    public void setTeam(String team) {
        this.team = team;
    }
}
