import com.google.gson.annotations.SerializedName;

public class Replies {
    @SerializedName("Post ID")
    private int postID;
    @SerializedName("Reply Content")
    private String replyContent;
    @SerializedName("Reply Stars")
    private int replyStars;
    @SerializedName("Reply Author")
    private String replyAuthor;
    @SerializedName("Secondary Reply Content")
    private String secondaryReplyContent;
    @SerializedName("Secondary Reply Stars")
    private int secondaryReplyStars;
    @SerializedName("Secondary Reply Author")
    private String secondaryReplyAuthor;

    private int reply_1_id;

    public Replies() {
    }

    public Replies(int postID, String replyContent, int replyStars, String replyAuthor, String secondaryReplyContent, int secondaryReplyStars, String secondaryReplyAuthor) {
        this.postID = postID;
        this.replyContent = replyContent;
        this.replyStars = replyStars;
        this.replyAuthor = replyAuthor;
        this.secondaryReplyContent = secondaryReplyContent;
        this.secondaryReplyStars = secondaryReplyStars;
        this.secondaryReplyAuthor = secondaryReplyAuthor;
    }

    public int getReply_1_id() {

        return reply_1_id;
    }

    public int getPostID() {
        return postID;
    }

    public void setPostID(int postID) {
        this.postID = postID;
    }

    public String getReplyContent() {
        return replyContent;
    }

    public void setReplyContent(String replyContent) {
        this.replyContent = replyContent;
    }

    public int getReplyStars() {
        return replyStars;
    }

    public void setReplyStars(int replyStars) {
        this.replyStars = replyStars;
    }

    public String getReplyAuthor() {
        return replyAuthor;
    }

    public void setReplyAuthor(String replyAuthor) {
        this.replyAuthor = replyAuthor;
    }

    public String getSecondaryReplyContent() {
        return secondaryReplyContent;
    }

    public void setSecondaryReplyContent(String secondaryReplyContent) {
        this.secondaryReplyContent = secondaryReplyContent;
    }

    public int getSecondaryReplyStars() {
        return secondaryReplyStars;
    }

    public void setSecondaryReplyStars(int secondaryReplyStars) {
        this.secondaryReplyStars = secondaryReplyStars;
    }

    public String getSecondaryReplyAuthor() {
        return secondaryReplyAuthor;
    }

    public void setSecondaryReplyAuthor(String secondaryReplyAuthor) {
        this.secondaryReplyAuthor = secondaryReplyAuthor;
    }

    @Override
    public String toString() {
        return "Replies{" +
                "postID=" + postID +
                ", replyContent='" + replyContent + '\'' +
                ", replyStars=" + replyStars +
                ", replyAuthor='" + replyAuthor + '\'' +
                ", secondaryReplyContent='" + secondaryReplyContent + '\'' +
                ", secondaryReplyStars=" + secondaryReplyStars +
                ", secondaryReplyAuthor='" + secondaryReplyAuthor + '\'' +
                '}';
    }
}
