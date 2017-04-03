package facebook;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.csvreader.CsvWriter;

import facebook4j.Comment;
import facebook4j.Facebook;
import facebook4j.FacebookException;
import facebook4j.FacebookFactory;
import facebook4j.Group;
import facebook4j.PagableList;
import facebook4j.Post;
import facebook4j.Reading;
import facebook4j.ResponseList;
import facebook4j.auth.AccessToken;

public class FacebookUtiliy {

	public static Facebook getFacebookConnection() throws FacebookException {
		// TODO Auto-generated method stub

		String AppsID = "225045584362946";
		String AppsSecret = "5f4f60e91c7292474caf2008f6a33816";
		String accesstoken = "EAADMrYIdPcIBABvKZCEQ7ZAemzGWVYlqBGXZAWPaXpwmqrYJG8uZBy7ovNpJJfeVyK84EMuv8GXAkvLA2cexD6Fk7dC6ZCpQktOK97SfV9X5kYJoTdOEar1Bod8yfu6rZCn0uQuzZAI2iqsgROeqHDxz7Lbmu6C4sd937s3Jd667lnjjCZBwXPZBB3CESBUVhZAyEZD";
		Facebook facebook = new FacebookFactory().getInstance();
		facebook.setOAuthAppId(AppsID, AppsSecret);
		AccessToken extendedToken = facebook.extendTokenExpiration(accesstoken);

		facebook.setOAuthAccessToken(extendedToken);// new
													// AccessToken(AppsID+"|"+AppsSecret)
//		String grpid = "1168762613184406";
//		String outputFile = "users.csv";
//		String grpid = "159095767506446";
		//String grpid = "149532052203991";
		//String grpid = "";
		//String grpid = "1214391721984536";
		String grpid = "";
		String outputFile = "knittinggroups.csv";
		ResponseList<Group> grp = facebook.getGroups();
		
		boolean alreadyExists = new File(outputFile).exists();

		try {
			// use FileWriter constructor that specifies open for appending
			CsvWriter csvOutput = new CsvWriter(new FileWriter(outputFile, true), ',');

			// if the file didn't already exist then we need to write out the
			// header line
			if (!alreadyExists) {
				csvOutput.write("groupid");
				csvOutput.write("postid");
				csvOutput.write("userName");
				csvOutput.write("userid");
				csvOutput.write("commentid");
				csvOutput.write("comment_user_name");
				csvOutput.write("comment_user_id");
				// csvOutput.write("post");
				// csvOutput.write("comment");
				csvOutput.write("postdate");
				csvOutput.write("commentdate");

				csvOutput.endRecord();
			}

			// Set limit to 25 feeds.
			ResponseList<Post> feeds = facebook.getFeed(grpid, new Reading().limit(100));

			while (feeds != null) {

				for (int i = 0; i < feeds.size(); i++) {
					// Get post.
					Post post = feeds.get(i);

					// Get (string) message.
					// System.out.println(post.getFrom() + "\t" +
					// post.getCreatedTime());
					PagableList<Comment> comments = null;
					try {
						comments = post.getComments();
					} catch (Exception e) {
						comments = null;
					}
					if (comments == null) {
						csvOutput.write(grpid);
						csvOutput.write(post.getId());
						csvOutput.write(post.getFrom().getName());
						csvOutput.write(post.getFrom().getId());
						csvOutput.write("null");
						csvOutput.write("null");
						csvOutput.write("null");
						// csvOutput.write(post.getMessage());
						// csvOutput.write("null");
						csvOutput.write(formatDateToMysql(post.getCreatedTime()));
						csvOutput.write("null");

						csvOutput.endRecord();

					}

					while (comments != null) {
						try {
							System.out.println(i);
							for (int ic = 0; ic < comments.size(); ic++) {
								// Get post.
								Comment comment = comments.get(ic);

								csvOutput.write(grpid);
								csvOutput.write(post.getId());
								csvOutput.write(post.getFrom().getName());
								csvOutput.write(post.getFrom().getId());
								csvOutput.write(comment.getId());
								csvOutput.write(comment.getFrom().getName());
								csvOutput.write(comment.getFrom().getId());
								// csvOutput.write(post.getMessage());
								// csvOutput.write(comment.getMessage());
								csvOutput.write(formatDateToMysql(post.getCreatedTime()));
								csvOutput.write(formatDateToMysql(comment.getCreatedTime()));

								csvOutput.endRecord();
							}

							comments = facebook.fetchNext(comments.getPaging());
						} catch (Exception e) {
							comments = null;
						}

					}

				}
				try {
					feeds = facebook.fetchNext(feeds.getPaging());
				} catch (Exception e) {
					feeds = null;
				}

			}

			csvOutput.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
		return facebook;

	}

	public static void main(String[] args) {
		try {
			getFacebookConnection();
		} catch (FacebookException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static String formatDateToMysql(Date dte) {
		String sdte = null;
		if (dte != null) {
			SimpleDateFormat dfmysql = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			sdte = "'" + dfmysql.format(dte) + "'";
		}
		return sdte;
	}
}
