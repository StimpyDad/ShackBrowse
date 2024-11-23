package net.swigglesoft.shackbrowse;

public class AppConstants
{
    public static final String SHACKNEWS_TXT = "shacknews";
    public static final String SHACKNEWS_AUTHOR = "Shacknews";
    public static final String SHACKNEWS_HOST = "shacknews.com";
    public static final String SHACKNEWS_HOST_WWW = "www.shacknews.com";
    public static final String SHACKNEWS_URL = "https://" + SHACKNEWS_HOST_WWW;
    public static final String SHACKNEWS_URL_CORTEX = SHACKNEWS_URL + "/cortex/";
    public static final String SHACKNEWS_CHATTY_URL = SHACKNEWS_URL + "/chatty";

    static final int POST_MAX_CHARLENGTH = 4900;

    static final String POST_TYPE_CORTEX = "cortex";
    static final String POST_TYPE_INTERESTING = "interesting";
    static final String POST_TYPE_INFORMATIVE = "informative";
    static final String POST_TYPE_NUKED = "nuked";
    static final String POST_TYPE_NWS = "nws";
    static final String POST_TYPE_OFFTOPIC = "offtopic"; // offtopic and tangent are same category
    static final String POST_TYPE_ONTOPIC = "ontopic";
    static final String POST_TYPE_POLITICAL = "political";
    static final String POST_TYPE_STUPID = "stupid";
    static final String POST_TYPE_TANGENT = "tangent"; // tangent and offtopic are same category

    static final int POST_TYPE_INTERESTING_ID = 1;
    static final int POST_TYPE_NUKED_ID = 8;
    static final int POST_TYPE_NWS_ID = 2;
    static final int POST_TYPE_ONTOPIC_ID = 5;
    static final int POST_TYPE_POLITICAL_ID = 9;
    static final int POST_TYPE_STUPID_ID = 3;
    static final int POST_TYPE_TANGENT_ID = 4;

    static final String TAG_TYPE_LOL = "lol";
    static final String TAG_TYPE_TAG = "tag";
    static final String TAG_TYPE_INF = "inf";
    static final String TAG_TYPE_UNF = "unf";
    static final String TAG_TYPE_WTF = "wtf";
    static final String TAG_TYPE_WOW = "wow";
    static final String TAG_TYPE_AWW = "aww";

    static final int TAG_TYPEID_LOL = 2;
    static final int TAG_TYPEID_TAG = 3;
    static final int TAG_TYPEID_INF = 4;
    static final int TAG_TYPEID_UNF = 5;
    static final int TAG_TYPEID_WTF = 6;
    static final int TAG_TYPEID_WOW = 7;
    static final int TAG_TYPEID_AWW = 9;

    static final String URL_LOGIN = SHACKNEWS_URL + "/account/signin";
    static final String URL_CHECKUSEREXISTS = SHACKNEWS_URL + "/account/username_exists";
    static final String URL_MODERATION = SHACKNEWS_URL + "/mod_chatty.x";
    static final String URL_CREATEPOST = SHACKNEWS_URL + "/api/chat/create/17.json";
    static final String URL_TAGHANDLING = SHACKNEWS_URL + "/api2/api-index.php";
    static final String URL_SHACKMSGPOST = SHACKNEWS_URL + "/messages/send";
    static final String URL_SHACKMSGREAD = SHACKNEWS_URL + "/messages/read";

    static final String USERNAME_TMWTB = "the man with the briefcase";

    static final String USERPREF_SHOWINFORMATIVE = "showInformative";
    static final String USERPREF_SHOWTANGENT = "showTangent";
    static final String USERPREF_SHOWSTUPID = "showStupid";
    static final String USERPREF_SHOWNWS = "showNWS";
    static final String USERPREF_SHOWPOLITICAL = "showPolitical";
    static final String USERPREF_SHOWONTOPIC = "showOntopic";
    static final String USERPREF_SHOWCORTEX = "showCortex";


    static final int USERTYPE_EMPLOYEE = 2;
    static final int USERTYPE_MODERATOR = 1;



    // no longer used
    static final String LOL_URL = "http://www.lmnopc.com/greasemonkey/shacklol/report.php";
    static final String URL_CREATEPOSTCHATTY = SHACKNEWS_URL + "/post_chatty.x";
}
