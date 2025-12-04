package ca.lizardwizard.ebookclient.objects;

public class Book {
    private int id;
    private String name;
    private String author;
    private String description;
    private String coverURL;

    public Book(int id, String name, String author, String description, String coverURL) {
        this.id = id;
        this.name = name;
        this.author = author;
        this.description = description;
        this.coverURL = coverURL;
    }

    public String getCoverURL() {
        return coverURL;
    }

    public void setCoverURL(String coverURL) {
        this.coverURL = coverURL;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
