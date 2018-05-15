package com.coreybutts.ledcontrolpanel;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by Borey on 5/8/2018.
 */

public class MyLeds extends Fragment
{
    View myView;
    @android.support.annotation.Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @android.support.annotation.Nullable ViewGroup container, Bundle savedInstanceState)
    {
        myView = inflater.inflate(R.layout.my_leds, container, false);
        return myView;
    }

    public void textStatusSetText(String input)
    {
        TextView text = this.getView().findViewById(R.id.text_status);
        text.setText(input);
    }
}
