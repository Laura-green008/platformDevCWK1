package org.me.gcu.platformdevcwk1;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.util.Xml;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class IncidentsChecker  extends AppCompatActivity implements View.OnClickListener {

    private String urlSource = "https://trafficscotland.org/rss/feeds/currentincidents.aspx";
    private RecyclerView;
    private List<IncidentsResults.RssFeedModel> mFeedModelList;
    private String mFeedTitle;
    private String mFeedLink;
    private String mFeedDescription;

    public static final String TAG = "MyActivity";

    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.incidents_checker);


    }
    public void onClick(View v){
//        new runIncidentsChecker().execute((Void) null);
        new runIncidentsChecker();

    }
private class runIncidentsChecker extends AsyncTask<void, void, boolean>{

    @Override
    protected void onPreExecute() {
        mSwipeLayout.setRefreshing(true);
       // urlLink = mEditText.getText().toString();
    }

    protected Boolean doInBackground(Void... voids) {
        try {

            URL url = new URL(urlSource);
            InputStream inputStream = url.openConnection().getInputStream();
            mFeedModelList = parseFeed(inputStream);
            return true;
        } catch (IOException e) {
            Log.e(TAG, "Error", e);
        } catch (XmlPullParserException e) {
            Log.e(TAG, "Error", e);
        }
        return false;
    }

    public class RssFeedModel {

        public String title;
        public String link;
        public String description;

        public RssFeedModel(String title, String link, String description) {
            this.title = title;
            this.link = link;
            this.description = description;
        }
    }

    public List<IncidentsResults.RssFeedModel> parseFeed(InputStream inputStream) throws XmlPullParserException,
            IOException {
        String title = null;
        String link = null;
        String description = null;
        boolean isItem = false;
        List<IncidentsResults.RssFeedModel> items = new ArrayList<>();

        try {
            XmlPullParser xmlPullParser = Xml.newPullParser();
            xmlPullParser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            xmlPullParser.setInput(inputStream, null);

            xmlPullParser.nextTag();
            while (xmlPullParser.next() != XmlPullParser.END_DOCUMENT) {
                int eventType = xmlPullParser.getEventType();

                String name = xmlPullParser.getName();
                if(name == null)
                    continue;

                if(eventType == XmlPullParser.END_TAG) {
                    if(name.equalsIgnoreCase("item")) {
                        isItem = false;
                    }
                    continue;
                }

                if (eventType == XmlPullParser.START_TAG) {
                    if(name.equalsIgnoreCase("item")) {
                        isItem = true;
                        continue;
                    }
                }

                Log.d("MyXmlParser", "Parsing name ==> " + name);
                String result = "";
                if (xmlPullParser.next() == XmlPullParser.TEXT) {
                    result = xmlPullParser.getText();
                    xmlPullParser.nextTag();
                }

                if (name.equalsIgnoreCase("title")) {
                    title = result;
                } else if (name.equalsIgnoreCase("link")) {
                    link = result;
                } else if (name.equalsIgnoreCase("description")) {
                    description = result;
                }

                if (title != null && link != null && description != null) {
                    if(isItem) {
                        IncidentsResults.RssFeedModel item = new IncidentsResults.RssFeedModel(title, link, description);
                        items.add(item);
                    }
                    else {
                        mFeedTitle = title;
                        mFeedLink = link;
                        mFeedDescription = description;
                    }

                    title = null;
                    link = null;
                    description = null;
                    isItem = false;
                }
            }

            return items;
        } finally {
            inputStream.close();
        }



    }

    @Override
    protected void onPostExecute(Boolean success) {
        //mSwipeLayout.setRefreshing(false);

        if (success) {
            mFeedTitleTextView.setText("Feed Title: " + mFeedTitle);
            mFeedDescriptionTextView.setText("Feed Description: " + mFeedDescription);
            mFeedLinkTextView.setText("Feed Link: " + mFeedLink);
            // Fill RecyclerView
            // mRecyclerView.setAdapter(new RssFeedListAdapter(mFeedModelList));
        } else {
//            Toast.makeText(MainActivity.this,
//                    "Enter a valid Rss feed url",
//                    Toast.LENGTH_LONG).show();
        }
    }
}


}

}
