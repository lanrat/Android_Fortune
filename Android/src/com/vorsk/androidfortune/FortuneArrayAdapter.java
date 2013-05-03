package com.vorsk.androidfortune;

import android.content.Context;

/*
 * Adapted from http://stackoverflow.com/questions/2109271/can-any-one-provide-me-example-of-two-line-list-item-in-android
 */
public class FortuneArrayAdapter extends TwoLineArrayAdapter<Fortune> {
    public FortuneArrayAdapter(Context context, Fortune[] fortunes) {
        super(context, fortunes);
    }

    @Override
    public String lineOneText(Fortune fortune) {
        return fortune.getDate().toString();
    }

    @Override
    public String lineTwoText(Fortune fortune) {
        return fortune.getFortune();
    }
}