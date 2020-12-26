package com.example.app_maulisa;

public class Githubuser {

    String login;
    String html_url;
    String avatar_url;

    public Githubuser(String name, String urlhtml , String image) {
        this.login = name;
        this.html_url = urlhtml;
        this.avatar_url = image;

    }

    public String getName() {
        return login;
    }

    public String getImage() {
        return avatar_url;
    }

    public String getUrlname() {
        return html_url;
    }
}
