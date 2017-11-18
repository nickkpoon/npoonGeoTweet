package edu.ucsb.cs.cs184.npoon.npoongeotweet;


import android.app.DialogFragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by nickkpoon on 11/17/17.
 */

public class TweetFragment extends DialogFragment {

    public TweetFragment()
    {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View v = inflater.inflate(R.layout.fragment_tweet, container, false);

        String author = getArguments().getString("Author");
        //Log.d("DIALOGFRAGMENT", "AUTHOR IS: " + author);
        String content = getArguments().getString("Content");
        String likes = getArguments().getString("Likes");

        TextView authorView = (TextView)v.findViewById(R.id.Author);
        TextView contentView = (TextView)v.findViewById(R.id.Content);
        TextView likeView = v.findViewById(R.id.Likes);

        authorView.setText(author);
        contentView.setText(content);
        likeView.setText(likes);



        return v;

    }
}
