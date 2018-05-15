package com.coreybutts.ledcontrolpanel;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Borey on 5/8/2018.
 */

public class StaticPatterns extends Fragment
{
    View myView;
    @android.support.annotation.Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @android.support.annotation.Nullable ViewGroup container, Bundle savedInstanceState)
    {
        myView = inflater.inflate(R.layout.static_patterns, container, false);
        return myView;
    }
}
