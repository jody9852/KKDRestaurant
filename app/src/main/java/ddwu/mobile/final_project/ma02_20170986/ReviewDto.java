package ddwu.mobile.final_project.ma02_20170986;

public class ReviewDto {

    private long _id;
    private String photoPath;
    private String name;
    private String review;
    private String rating;

    public long get_id() {
        return _id;
    }
    public void set_id(long _id) {
        this._id = _id;
    }
    public String getPhotoPath() {
        return photoPath;
    }
    public void setPhotoPath(String photoPath) {
        this.photoPath = photoPath;
    }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getReview() {
        return review;
    }
    public void setReview(String review) {
        this.review = review;
    }
    public String getRating() { return rating; }
    public void setRating(String rating) { this.rating = rating; }

    @Override
    public String toString() {
        return new String(_id + " : " + photoPath);
    }
}
