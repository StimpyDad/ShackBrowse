package net.swigglesoft.shackbrowse;

import android.text.Spannable;
import android.view.View;

public class PostFormatter {

    public static Spannable formatContent(Post post, boolean multiLine)
    {
        return formatContent(post, null, multiLine);
    }
    
    public static Spannable formatContent(Post post, View view, boolean multiLine)
    {
        String content = post.getContent();

        // fix escaped html and make relative link absolute for shack author posts
        if (post.getUserName().equalsIgnoreCase(AppConstants.SHACKNEWS_AUTHOR))
        {
            content = contentFixForShackAuthor(content);
        }
        return ShackTags.fromHtml(content, view, !multiLine, true, post.getSpoiledHash());
    }

    public static Spannable formatContent(Thread thread, boolean multiLine)
    {
        return formatContent(thread, multiLine, true);
    }
    
    public static Spannable formatContent(Thread thread, boolean multiLine, boolean showTags)
    {
        return formatContent(thread.getUserName(), thread.getContent(), null, multiLine, showTags);
    }
    
    public static Spannable formatContent(Message thread, boolean multiLine, boolean showTags)
    {
        return formatContent(thread.getUserName(), thread.getContent(), null, multiLine, showTags);
    }

    public static Spannable formatContent(String userName, String content, final View view, boolean multiLine, boolean showTags)
    {
        // fix escaped html and make relative link absolute for shack author posts
        if (userName.equalsIgnoreCase(AppConstants.SHACKNEWS_AUTHOR))
        {
            content = contentFixForShackAuthor(content);
        }
        
        return ShackTags.fromHtml(content, view, !multiLine, showTags);
    }

    private static String contentFixForShackAuthor(String content)
    {
        content = content.replaceAll("&lt;(/?)a(.*?)&gt;", "<$1a$2>");
        content = content.replaceAll("&lt;br /&gt;", "<br />");
        content = content.replaceAll("&lt;(/?)span(.*?)&gt;", "<$1span$2>");
        content = content.replaceAll("href=\"/", "href=\"" + AppConstants.SHACKNEWS_URL + "/");
        return content;
    }
}
