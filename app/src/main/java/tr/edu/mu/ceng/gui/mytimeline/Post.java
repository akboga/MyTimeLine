package tr.edu.mu.ceng.gui.mytimeline;

import com.google.firebase.Timestamp;

public class Post {
    private String imagePath;
    private String message;
    private Timestamp date;

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Timestamp getDate() {
        return date;
    }

    public void setDate(Timestamp date) {
        this.date = date;
    }
}