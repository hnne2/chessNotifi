package push700.demo;

import jakarta.persistence.*;

@Entity
@Table(name = "Push")
public class Push {
    @Column(name = "app")
    String app;
    @Column(name = "title")
    String title;
    @Column(name = "text")
    String text;
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    public Push(){
    }
    public String getApp() {
        return app;
    }

    public void setApp(String app) {
        this.app = app;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
