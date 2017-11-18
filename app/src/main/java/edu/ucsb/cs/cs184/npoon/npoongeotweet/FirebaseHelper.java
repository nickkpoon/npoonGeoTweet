package edu.ucsb.cs.cs184.npoon.npoongeotweet;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Donghao Ren on 03/11/2017.
 */

/**
 * This is a Firebase helper starter class we have created for you
 * In your Activity, please call FirebaseHelper.Initialize() to setup the Firebase
 * Put your application logic in OnDatabaseInitialized where you'll have the database object initialized
 */
public class FirebaseHelper {
    /** This is a message data structure that mirrors our Firebase data structure for your convenience */
    public static class Message implements Serializable {
        public double longitude;
        public double latitude;
        public String author;
        public String content;
        public double timestamp;
        public int likes;
    }

    /** ============================================================================================
     * Retrieve Firebase access tokens from a server we setup.
     * You should call FirebaseHelper.Initialize() when your activity starts to initiate the database helper.
     * You don't need to change the code in this section unless we instruct you to.
     */
    private static class RetrieveFirebaseTokensTask extends AsyncTask<Void, Void, ArrayList<String>> {
        private FirebaseTokensListener listener;

        public interface FirebaseTokensListener {
            void onTokens(String url, String apiKey, String applicationID);
        }

        public RetrieveFirebaseTokensTask(FirebaseTokensListener listener) {
            super();
            this.listener = listener;
        }

        @Override
        protected ArrayList<String> doInBackground(Void... params) {
            try {
                // Retrieve data from the url
                URL pageUrl = new URL("http://cs.jalexander.ninja:8080/file/firebase.txt");
                URLConnection connection = pageUrl.openConnection();
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String url = reader.readLine().trim();
                String apikey = reader.readLine().trim();
                String applicationID = reader.readLine().trim();
                ArrayList<String> result = new ArrayList<>();
                result.add(url);
                result.add(apikey);
                result.add(applicationID);
                reader.close();
                return result;
            } catch (Exception e) {
                return null;
            }
        }

        @Override
        protected void onPostExecute(ArrayList<String> result) {
            if (listener != null) {
                listener.onTokens(result.get(0), result.get(1), result.get(2));
            }
        }
    }

    /** Keep track of initialized state, so we don't initialize multiple times */
    private static boolean initialized = false;

    /** The Firebase database object */
    private static FirebaseDatabase db;

    /** Initialize the firebase instance */
    public static void Initialize(final Context context) {
        if (!initialized) {
            initialized = true;
            // We retrieve the database access tokens from a server because we may change the actual database url if we run out of quota.
            // This is a very simple example of a "configuration server" -- instead of hardcoding the database configuration, we retrieve them from a server.
            // This approach makes it easy for us to change the database url without having you modify your code.
            RetrieveFirebaseTokensTask retrieveTokensTask = new RetrieveFirebaseTokensTask(new RetrieveFirebaseTokensTask.FirebaseTokensListener() {
                @Override
                public void onTokens(String url, String apiKey, String applicationID) {
                    // Once we get the tokens from our configuration server, we initialize the database API as follows:
                    FirebaseApp.initializeApp(context, new FirebaseOptions.Builder()
                            .setDatabaseUrl(url)
                            .setApiKey(apiKey)
                            .setProjectId("cs184-hw5")
                            .setApplicationId(applicationID)
                            .build()
                    );
                    // Call the OnDatabaseInitialized to setup application logic
                    OnDatabaseInitialized(context);
                }
            });
            retrieveTokensTask.execute();
        }
    }
    /** ============================================================================================
     */
    static ArrayList<LocationPoint> messageList = new ArrayList<>();

    public static void populateTweets(LocationPoint LP)
    {
        messageList.add(LP);
    }

    public static ArrayList<LocationPoint> getTweets()
    {
        if(messageList != null)
        {
            Log.d("NOT-NULL MESSAGELIST", "NOT-NULL MESSAGES!!!");
            return messageList;
        }
        else {
            Log.d("NULL MESSAGELIST", "NULL MESSAGES!!!");
            return null;
        }

    }

    /** This is called once we initialize the firebase database object */
    private static void OnDatabaseInitialized(Context context) {
        db = FirebaseDatabase.getInstance();
        queryDB(db, context);
        // TODO: Setup your callbacks to listen for /posts.
        // Your code should handle post added, post updated, and post deleted events.

    }

    public static void queryDB(FirebaseDatabase db, final Context context)
    {
        DatabaseReference myReference = db.getReference("posts");
        Log.d("FB initialized", "FB INITIALIZED");
        myReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    Log.d("ONCHILDADD initialized", "ONCHILDADDED INITIALIZED");


                LocationPoint tweets = dataSnapshot.getValue(LocationPoint.class);
                double longitude = tweets.getLongitude();
                double latitude = tweets.getLatitude();
                String author = tweets.getAuthor();
                    Log.d("captured Author", author);
                String content = tweets.getContent();
                    Log.d("captured Tag", content);
                double timestamp = tweets.getTimestamp();
                int likes = tweets.getLikes();

                populateTweets(tweets);
                    Log.d("TWEETLIST ADDED", "ADDED TO LIST");

                updateListener(tweets);

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                LocationPoint tweets = dataSnapshot.getValue(LocationPoint.class);
                Log.d("ONCHILDCHANGED CALLED", "ONCHILDCHANGED");

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                LocationPoint tweets = dataSnapshot.getValue(LocationPoint.class);
                Log.d("REMOVEDTWEET", tweets.getAuthor());
                updateRemoveListener(tweets);

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public static void updateListener(LocationPoint LP)
    {
        LocationPoint.loadData(LP);
    }

    public static void updateRemoveListener(LocationPoint LP)
    {
        LocationPoint.removeData(LP);
    }

    public static void updateChangeListener(LocationPoint LP)
    {
        LocationPoint.changeData(LP);
    }
    // TODO: Add methods for increasing the number of likes
    // TODO: You *may* create a listener mechanism so that your Activity and Fragments can register callbacks to the database helper
}