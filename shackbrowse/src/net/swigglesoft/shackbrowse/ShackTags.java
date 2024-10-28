package net.swigglesoft.shackbrowse;
import java.io.StringReader;
import java.lang.reflect.Method;
import java.util.HashMap;


import org.ccil.cowan.tagsoup.HTMLSchema;
import org.ccil.cowan.tagsoup.Parser;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import android.annotation.SuppressLint;
import android.graphics.Typeface;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.style.CharacterStyle;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StrikethroughSpan;
import android.text.style.StyleSpan;
import android.text.style.TypefaceSpan;
import android.text.style.UpdateAppearance;
import android.util.Log;
import android.view.View;

public class ShackTags
{
    private static final HTMLSchema schema = new HTMLSchema();
    
    public static Spannable fromHtml(String source, View owner, Boolean single_line, Boolean showTags)
    {
        return fromHtml(source, owner, single_line, showTags, new HashMap <Integer, Boolean>());
    }

    public static Spannable fromHtml(String source, View owner, Boolean single_line, Boolean showTags, HashMap<Integer, Boolean> spoiled)
    {
        Parser parser = new Parser();
        try
        {
            parser.setProperty(Parser.schemaProperty, schema);
            ShackTagsConverter converter = new ShackTagsConverter(source.replace("\r", ""), parser, owner, single_line, showTags, spoiled);
            return converter.convert();
        }
        catch (Exception e)
        {
            Log.e("ShackTags", "Error parsing shack tags" + source, e);
            return new SpannableString("!!HTML Parsing Error!! Source:: " + source);
        }
    }
}